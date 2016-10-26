package stevedore.usecase;

import com.google.common.eventbus.EventBus;
import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.*;
import stevedore.infrastructure.ConnectionException;

import java.util.Optional;

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
                .orElseThrow(() -> new ProjectNotFoundException());

        Environment environment = project
                .getEnvironment(environmentId)
                .orElseThrow(() -> new EnvironmentNotFoundException());

        environment.tagRelease(new Version(releaseName));

        environment.recordedEvents().forEach(messageBus::post);
    }
}
