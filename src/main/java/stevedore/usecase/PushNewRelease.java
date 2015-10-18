package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.*;

public class PushNewRelease {
    private final ProjectRepository projectRepository;
    private final IMessageBus messageBus;

    public PushNewRelease(ProjectRepository projectRepository, IMessageBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void release(String projectName, String environmentName, String releaseName) {
        Project project = projectRepository.load(projectName);
        Environment environment = project.getEnvironment(environmentName);
        environment.release(new Version(releaseName));

        environment.recordedEvents().forEach(messageBus::publish);
//        messageBus.publish(
//                new VersionWasReleased(project, environment, new Version(releaseName))
//        );
    }
}
