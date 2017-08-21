package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import org.junit.Before;
import org.junit.Test;
import stevedore.*;
import stevedore.infrastructure.ConnectionException;
import stevedore.infrastructure.InMemoryProjectRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PrepareNewDeployTest {
    private ProjectRepository projectRepository;
    private IMessageBus messageBus;

    private Environment environment;

    @Test
    public void itDeploysARelease() throws ReleaseNotFoundException, ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        String projectId = "somevendor/some-project";
        String environmentName = "prod";
        String releaseName = "1.23";

        givenProject(projectId, environmentName, releaseName);

        whenDeployIsStarted(projectId, environmentName, releaseName);

        thenDeployStatusIs(releaseName, DeployStatus.inProgress());
    }

    private void whenDeployIsStarted(String projectId, String environmentName, String releaseName) throws ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        PrepareNewDeploy useCase = new PrepareNewDeploy(projectRepository, messageBus);
        useCase.deploy(projectId, environmentName, releaseName);
    }

    private void thenDeployStatusIs(String releaseName, DeployStatus.Status status) {
        Deploy deploy = environment.getDeploy(releaseName);
        assertEquals(status, deploy.status());
    }

    private void givenProject(String projectId, String environmentName, String releaseName) {
        givenEnvironment(projectId, environmentName, new Version(releaseName));
        ProjectBuilder buildProject = new ProjectBuilder();
        Project project = buildProject
                .withId(projectId)
                .withEnvironment(environment)
                .build();

        projectRepository.save(project);
    }

    private Environment givenEnvironment(String projectId, String name, Version version) {
        environment = Environment.create(name);
        environment.release(version);
        environment.release(version);

        return environment;
    }

    @Before
    public void setUp() {
        projectRepository = new InMemoryProjectRepository();
        messageBus = mock(IMessageBus.class);
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }

}