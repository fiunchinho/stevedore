package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import org.junit.Before;
import org.junit.Test;
import stevedore.*;
import stevedore.events.DeployWasMade;
import stevedore.infrastructure.InMemoryProjectRepository;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DeployGivenReleaseTest {
    private ProjectRepository projectRepository;
    private IMessageBus messageBus = mock(IMessageBus.class);

    private Project project;
    private Environment environment;

    private Version getVersion(String version) {
        return new Version(version);
    }

    @Test(expected = DeployNotFoundException.class)
    public void ItFailsDeployingReleaseThatNotExists() throws DeployNotFoundException {
        String projectName = "some-project";
        String environmentName = "prod";

        Project project = givenProject(projectName);

        DeployGivenRelease useCase = new DeployGivenRelease(projectRepository, messageBus);
        useCase.deploy(projectName, environmentName, "1.0");
    }

    @Test
    public void ItDeploysSpecificRelease() throws DeployNotFoundException {
        String projectName = "some-project";
        String environmentName = "prod";

        Project project = givenProject(projectName);
        Environment environment = project.getEnvironment(environmentName);
        environment.tagRelease(getVersion("1.0"));
        environment.release(getVersion("1.0"));
        environment.tagRelease(getVersion("2.0"));
        environment.release(getVersion("2.0"));
        environment.startDeploy(getVersion("2.0"));

        DeployGivenRelease useCase = new DeployGivenRelease(projectRepository, messageBus);
        useCase.deploy(projectName, environmentName, "2.0");
    }

    @Test
    public void ItDeploysLatestRelease() throws DeployNotFoundException {
        String projectName = "some-project";
        String environmentName = "prod";

        Project project = givenProject(projectName);
        Environment environment = project.getEnvironment(environmentName);
        environment.tagRelease(getVersion("1.0"));
        environment.release(getVersion("1.0"));
        environment.tagRelease(getVersion("2.0"));
        environment.release(getVersion("2.0"));
        environment.startDeploy();

        DeployGivenRelease useCase = new DeployGivenRelease(projectRepository, messageBus);
        useCase.deploy(projectName, environmentName, "2.0");
    }

    @Test
    public void ItRollsBackToPreviousRelease() throws ReleaseNotFoundException {
        String projectName = "some-project";
        String environmentName = "prod";

        Project project = givenProject(projectName);
        Environment environment = project.getEnvironment(environmentName);
        environment.tagRelease(getVersion("1.0"));
        environment.release(getVersion("1.0"));
        environment.tagRelease(getVersion("2.0"));
        environment.release(getVersion("2.0"));
        environment.startDeploy(getVersion("2.0"));
        environment.deploy(getVersion("2.0"));
        environment.startDeploy(getVersion("1.0"));

        assertTrue(environment.currentRelease().equalsTo("2.0"));

        DeployGivenRelease useCase = new DeployGivenRelease(projectRepository, messageBus);
        useCase.deploy(projectName, environmentName, "1.0");

        assertTrue(environment.currentRelease().equalsTo("1.0"));

        verify(messageBus, times(8)).publish(any(DeployWasMade.class));
    }

    private Project givenProject(String projectName) {
        Environment environment = getEnvironment("prod");
        ProjectBuilder buildProject = new ProjectBuilder();
        Project project = buildProject
                .withName(projectName)
                .withEnvironment(environment)
                .build();

        projectRepository.save(project);

        return project;
    }

    @Before
    public void getProjectRepository() {
        projectRepository = new InMemoryProjectRepository();
    }

    private Environment getEnvironment(String name) {
        return new Environment(name, "eu-west-1", "vpc-123abc", "keys", getIrrelevantAwsIdentity());
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }

}