package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.*;
import stevedore.events.ReleaseWasRequested;

public class RequestRelease {
    private final ProjectRepository projectRepository;
    private final IMessageBus messageBus;

    public RequestRelease(ProjectRepository projectRepository, IMessageBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void release(String projectName, String environmentName, String releaseName) {
        Project project = projectRepository.findOneByName(projectName);
        Environment environment = project.getEnvironment(environmentName);

        environment.release(new Version(releaseName));
        projectRepository.save(project);
        messageBus.publish(
                new ReleaseWasRequested(projectName, environmentName, releaseName)
        );
    }
}
