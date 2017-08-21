package stevedore.usecase;

import com.google.common.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import stevedore.*;
import stevedore.events.ReleaseWasPushed;
import stevedore.infrastructure.ConnectionException;
import stevedore.infrastructure.InMemoryProjectRepository;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TagNewReleaseTest {

    private ProjectRepository projectRepository;
    private EventBus messageBus;

    @Test
    public void ItReleasesNewVersion() throws ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        String projectId = "somevendor/some-project";
        String environmentName = "prod";
        String releaseName = "1.23";

        givenProject(projectId);
        whenTaggingANewRelease(projectId, environmentName, releaseName);
        thenEventsArePublished();
    }

    private void whenTaggingANewRelease(String projectId, String environmentName, String releaseName) throws ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        TagNewRelease useCase = new TagNewRelease(projectRepository, messageBus);
        useCase.release(projectId, environmentName, releaseName);
    }

    private void thenEventsArePublished() {
        verify(messageBus).post(isA(ReleaseWasPushed.class));
    }

    @Before
    public void setUp() {
        projectRepository = new InMemoryProjectRepository();
        messageBus = mock(EventBus.class);
    }

    private Project givenProject(String projectId) {
        Environment environment = Environment.create("prod");
        ProjectBuilder buildProject = new ProjectBuilder();
        Project project = buildProject
                .withId(projectId)
                .withEnvironment(environment)
                .build();

        projectRepository.save(project);

        return project;
    }
}