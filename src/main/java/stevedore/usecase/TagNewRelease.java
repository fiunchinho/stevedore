package stevedore.usecase;

import com.google.common.eventbus.EventBus;
import stevedore.*;
import stevedore.infrastructure.ConnectionException;

public class TagNewRelease {
    private final ProjectRepository projectRepository;
    private final EventBus messageBus;

    public TagNewRelease(ProjectRepository projectRepository, EventBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void release(String projectId, String environmentId, String releaseName) throws ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        Project project = projectRepository
                .load(projectId)
                .orElseThrow(ProjectNotFoundException::new);

        Environment environment = project
                .getEnvironment(environmentId)
                .orElseThrow(EnvironmentNotFoundException::new);

        environment.release(new Version(releaseName));
        environment.recordedEvents().forEach(messageBus::post);
    }
}
