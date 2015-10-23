package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.*;

import java.util.Optional;

public class TagNewRelease {
    private final ProjectRepository projectRepository;
    private final IMessageBus messageBus;

    public TagNewRelease(ProjectRepository projectRepository, IMessageBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void release(String projectId, String environmentName, String releaseName) throws ProjectNotFoundException {
        Project project = projectRepository
                .load(projectId)
                .orElseThrow(() -> new ProjectNotFoundException());

        Environment environment = project.getEnvironment(environmentName);
        environment.tagRelease(new Version(releaseName));

        environment.recordedEvents().forEach(messageBus::publish);
    }
}
