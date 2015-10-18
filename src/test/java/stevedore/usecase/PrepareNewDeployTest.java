package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import org.junit.Test;
import stevedore.*;
import stevedore.events.DeployWasStarted;
import stevedore.infrastructure.InMemoryProjectRepository;
import stevedore.messagebus.Message;

import static org.hamcrest.CoreMatchers.anything;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PrepareNewDeployTest {
    private ProjectRepository projectRepository = new InMemoryProjectRepository();
    private IMessageBus messageBus = mock(IMessageBus.class);

    private Project project;
    private Environment environment;

    @Test
    public void itDeploysARelease() throws ReleaseNotFoundException {
        IMessageBus messageBus = mock(IMessageBus.class);

        String projectName = "some-project";
        String environmentName = "prod";
        String releaseName = "1.23";
        getEnvironment(environmentName);
        environment.tagRelease(new Version(releaseName));
        environment.release(new Version(releaseName));
        ProjectBuilder buildProject = new ProjectBuilder();
        Project project = buildProject
                .withName(projectName)
                .withEnvironment(environment)
                .build();

        givenProject(project);

        whenDeployIsStarted(projectName, environmentName, releaseName);

        thenDeployStatusIs(releaseName, DeployStatus.inProgress());

        assertFalse(environment.recordedEvents().isEmpty());
        assertEquals(environment.recordedEvents().size(), 3);

//        verify(messageBus, times(3)).publish(anything());
    }

    private void whenDeployIsStarted(String projectName, String environmentName, String releaseName) {
        PrepareNewDeploy useCase = new PrepareNewDeploy(projectRepository, messageBus);
        useCase.deploy(projectName, environmentName, releaseName);
    }

    private void thenDeployStatusIs(String releaseName, DeployStatus.Status status) {
        Deploy deploy = environment.getDeploy(releaseName);
        assertEquals(status, deploy.status());
    }

    private Project givenProject(Project project) {
        projectRepository.save(project);
        return project;
    }

    private Environment getEnvironment(String name) {
        environment = new Environment(name, "eu-west-1", "vpc-123abc", "keys", getIrrelevantAwsIdentity());
        return environment;
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }

}