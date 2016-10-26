package stevedore.usecase;

import com.google.common.eventbus.EventBus;
import net.engio.mbassy.bus.common.IMessageBus;
import org.junit.Before;
import org.junit.Test;
import stevedore.*;
import stevedore.events.ReleaseWasTagged;
import stevedore.infrastructure.ConnectionException;
import stevedore.infrastructure.InMemoryProjectRepository;
import stevedore.messagebus.Message;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TagNewReleaseTest {

    private ProjectRepository projectRepository;
    private EventBus messageBus;

    @Test
    public void ItReleasesNewVersion() throws ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        UUID projectId = UUID.randomUUID();
        String environmentName = "prod";
        String releaseName = "1.23";

        givenProject(projectId);
        whenTaggingANewRelease(projectId, environmentName, releaseName);
        thenEventsArePublished();
    }

    private void whenTaggingANewRelease(UUID projectId, String environmentName, String releaseName) throws ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        TagNewRelease useCase = new TagNewRelease(projectRepository, messageBus);
        useCase.release(projectId.toString(), environmentName, releaseName);
    }

    private void thenEventsArePublished() {
        verify(messageBus).post(isA(ReleaseWasTagged.class));
    }

    @Before
    public void setUp() {
        projectRepository = new InMemoryProjectRepository();
        messageBus = mock(EventBus.class);
    }

    private Project givenProject(UUID projectId) {
        Environment environment = Environment.create(projectId.toString(), "prod", "eu-west-1", "vpc-123abc", "keys", getIrrelevantAwsIdentity());
        ProjectBuilder buildProject = new ProjectBuilder();
        Project project = buildProject
                .withId(projectId)
                .withEnvironment(environment)
                .build();

        projectRepository.save(project);

        return project;
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }

}