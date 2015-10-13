package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import org.junit.Test;
import stevedore.*;
import stevedore.infrastructure.InMemoryProjectRepository;
import stevedore.messagebus.Message;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RequestDeployTest {
    private ProjectRepository projectRepository = new InMemoryProjectRepository();

    @Test
    public void itDeploysARelease() throws ReleaseNotFoundException {
        IMessageBus messageBus = mock(IMessageBus.class);

        String projectName = "some-project";
        String environmentName = "prod";
        String releaseName = "1.23";
        Environment environment = getEnvironment(environmentName);
        environment.release(new Version(releaseName));
        ProjectBuilder buildProject = new ProjectBuilder();
        Project project = buildProject
                .withName(projectName)
                .withEnvironment(environment)
                .build();

        givenProject(project);

        RequestDeploy useCase = new RequestDeploy(projectRepository, messageBus);
        useCase.deploy(projectName, environmentName, releaseName);

        verify(messageBus).publish(any(Message.class));
    }

    private Project givenProject(Project project) {
        projectRepository.save(project);
        return project;
    }

    private Environment getEnvironment(String name) {
        return new Environment(name, "eu-west-1", "vpc-123abc", "keys", getIrrelevantAwsIdentity());
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }

}