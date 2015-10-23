package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import org.junit.Before;
import org.junit.Test;
import stevedore.*;
import stevedore.infrastructure.InMemoryProjectRepository;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PrepareNewDeployTest {
    private ProjectRepository projectRepository;
    private IMessageBus messageBus;

    private Environment environment;

    @Test
    public void itDeploysARelease() throws ReleaseNotFoundException, ProjectNotFoundException {
        UUID projectId = UUID.randomUUID();
        String environmentName = "prod";
        String releaseName = "1.23";

        givenProject(projectId, environmentName, releaseName);

        whenDeployIsStarted(projectId, environmentName, releaseName);

        thenDeployStatusIs(releaseName, DeployStatus.inProgress());
    }

    private void whenDeployIsStarted(UUID projectId, String environmentName, String releaseName) throws ProjectNotFoundException {
        PrepareNewDeploy useCase = new PrepareNewDeploy(projectRepository, messageBus);
        useCase.deploy(projectId.toString(), environmentName, releaseName);
    }

    private void thenDeployStatusIs(String releaseName, DeployStatus.Status status) {
        Deploy deploy = environment.getDeploy(releaseName);
        assertEquals(status, deploy.status());
    }

    private void givenProject(UUID projectId, String environmentName, String releaseName) {
        givenEnvironment(projectId, environmentName, new Version(releaseName));
        ProjectBuilder buildProject = new ProjectBuilder();
        Project project = buildProject
                .withId(projectId)
                .withEnvironment(environment)
                .build();

        projectRepository.save(project);
    }

    private Environment givenEnvironment(UUID projectId, String name, Version version) {
        environment = Environment.create(projectId.toString(), name, "eu-west-1", "vpc-123abc", "keys", getIrrelevantAwsIdentity());
        environment.tagRelease(version);
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