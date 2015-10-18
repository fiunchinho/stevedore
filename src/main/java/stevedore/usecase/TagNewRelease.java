package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.*;

public class TagNewRelease {
    private final ProjectRepository projectRepository;
    private final IMessageBus messageBus;

    public TagNewRelease(ProjectRepository projectRepository, IMessageBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void release(String projectName, String environmentName, String releaseName) {
        Project project = projectRepository.load(projectName);
        Environment environment = project.getEnvironment(environmentName);

        environment.tagRelease(new Version(releaseName));
        environment.recordedEvents().forEach(messageBus::publish);
    }
}
