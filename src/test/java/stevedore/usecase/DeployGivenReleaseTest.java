package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import org.junit.Before;
import org.junit.Test;
import stevedore.*;
import stevedore.events.DeployWasMade;
import stevedore.infrastructure.ConnectionException;
import stevedore.infrastructure.InMemoryProjectRepository;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class DeployGivenReleaseTest {
    private ProjectRepository projectRepository;
    private IMessageBus messageBus;

    private Environment environment;

    private Version getVersion(String version) {
        return new Version(version);
    }

    @Test(expected = DeployNotFoundException.class)
    public void ItFailsDeployingReleaseThatNotExists() throws DeployNotFoundException, ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        String projectId = "somevendor/some-project";
        String environmentName = "prod";

        givenEnvironment(projectId, environmentName);
        givenProject(projectId);

        whenDeploying(projectId, environmentName, "1.0");
    }

    @Test
    public void ItDeploysSpecificRelease() throws DeployNotFoundException, ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        String projectId = "somevendor/some-project";
        String environmentName = "prod";

        givenEnvironment(projectId, environmentName);
        givenProject(projectId);

        whenReleaseIsTagged(getVersion("1.0"));
        whenReleaseIsPushed(getVersion("1.0"));

        whenReleaseIsTagged(getVersion("2.0"));
        whenReleaseIsPushed(getVersion("2.0"));

        whenDeployIsStarted(getVersion("2.0"));

        whenDeploying(projectId, environmentName, "2.0");
    }

    @Test
    public void ItDeploysLatestRelease() throws DeployNotFoundException, ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        String projectId = "somevendor/some-project";
        String environmentName = "prod";

        givenEnvironment(projectId, environmentName);
        givenProject(projectId);

        whenReleaseIsTagged(getVersion("1.0"));
        whenReleaseIsPushed(getVersion("1.0"));
        whenReleaseIsTagged(getVersion("2.0"));
        whenReleaseIsPushed(getVersion("2.0"));
        environment.startDeploy();

        whenDeploying(projectId, environmentName, "2.0");
    }

    @Test
    public void ItRollsBackToPreviousRelease() throws ReleaseNotFoundException, ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        String projectId = "somevendor/some-project";
        String environmentName = "prod";

        givenEnvironment(projectId, environmentName);
        givenProject(projectId);

        whenReleaseIsTagged(getVersion("1.0"));
        whenReleaseIsPushed(getVersion("1.0"));
        whenReleaseIsTagged(getVersion("2.0"));
        whenReleaseIsPushed(getVersion("2.0"));
        whenDeployIsStarted(getVersion("2.0"));
        environment.deploy(getVersion("2.0"));
        whenDeployIsStarted(getVersion("1.0"));

        assertTrue(environment.currentRelease().equals("2.0"));

        whenDeploying(projectId, environmentName, "1.0");

        assertTrue(environment.currentRelease().equals("1.0"));

        verify(messageBus, atLeastOnce()).publish(any(DeployWasMade.class));
    }

    @Test(expected = ProjectNotFoundException.class)
    public void itFailsTryingToDeployNonExistingProject() throws ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        String projectId = "somevendor/some-project";
        String environmentName = "prod";

        whenDeploying(projectId, environmentName, "1.0");
    }

    private void whenReleaseIsTagged(Version version) {
        environment.release(version);
    }

    private void whenReleaseIsPushed(Version version) {
        environment.release(version);
    }

    private void whenDeployIsStarted(Version version) {
        environment.startDeploy(version);
    }

    private void whenDeploying(String projectId, String environmentName, String releaseName) throws ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        DeployGivenRelease useCase = new DeployGivenRelease(projectRepository, messageBus);
        useCase.deploy(projectId, environmentName, releaseName);
    }

    @Before
    public void setUp() {
        projectRepository = new InMemoryProjectRepository();
        messageBus = mock(IMessageBus.class);
        environment = null;
    }

    private void givenProject(String projectId) {
        ProjectBuilder buildProject = new ProjectBuilder();
        Project project = buildProject
                .withId(projectId)
                .withEnvironment(environment)
                .build();

        projectRepository.save(project);
    }

    private Environment givenEnvironment(String projectId, String name) {
        environment = Environment.create(name);

        return environment;
    }
}