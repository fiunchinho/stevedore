package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import org.junit.Test;
import stevedore.*;
import stevedore.infrastructure.InMemoryProjectRepository;
import stevedore.messagebus.Message;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RequestReleaseTest {

    private ProjectRepository projectRepository = new InMemoryProjectRepository();

    @Test
    public void ItReleasesNewVersion() {
        String projectName = "some-project";
        String environmentName = "prod";
        String releaseName = "1.23";

        Project project = givenProject(projectName);

        IMessageBus messageBus = mock(IMessageBus.class);

        RequestRelease useCase = new RequestRelease(projectRepository, messageBus);
        useCase.release(projectName, environmentName, releaseName);

        verify(messageBus).publish(any(Message.class));
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

    private Environment getEnvironment(String name) {
        return new Environment(name, "eu-west-1", "vpc-123abc", "keys", getIrrelevantAwsIdentity());
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }

}