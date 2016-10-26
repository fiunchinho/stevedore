package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import org.junit.Before;
import org.junit.Test;
import stevedore.*;
import stevedore.events.ReleaseWasPushed;
import stevedore.events.ReleaseWasTagged;
import stevedore.infrastructure.ConnectionException;
import stevedore.infrastructure.InMemoryProjectRepository;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class PushNewReleaseTest {

    private ProjectRepository projectRepository;
    private IMessageBus messageBus;

    private Environment environment;

    @Test
    public void ItReleasesNewVersion() throws ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        UUID projectId = UUID.randomUUID();
        String environmentName = "prod";
        String releaseName = "1.23";

        givenProject(projectId, environmentName, releaseName);

        whenReleaseIsPushedTo(projectId, environmentName, releaseName);

        thenReleaseStatusIs(releaseName, ReleaseStatus.ready());
        thenEventsArePublished();
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
        return environment;
    }

    private void whenReleaseIsPushedTo(UUID projectId, String environmentName, String releaseName) throws ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        PushNewRelease useCase = new PushNewRelease(projectRepository, messageBus);
        useCase.release(projectId.toString(), environmentName, releaseName);
    }

    private void thenReleaseStatusIs(String releaseName, ReleaseStatus.Status status) {
        Release release = environment.getRelease(releaseName).get();
        assertEquals(status, release.status());
    }

    private void thenEventsArePublished() {
        verify(messageBus, atLeastOnce()).publish(any(ReleaseWasPushed.class));
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