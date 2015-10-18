package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.*;

public class DeployGivenRelease {
    private final ProjectRepository projectRepository;
    private final IMessageBus messageBus;

    public DeployGivenRelease(ProjectRepository projectRepository, IMessageBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void deploy(String projectName, String environmentName, String releaseName) throws ReleaseNotFoundException {
        Project project = projectRepository.load(projectName);
        Environment environment = project.getEnvironment(environmentName);

        environment.deploy(new Version(releaseName));
        environment.recordedEvents().forEach(messageBus::publish);
    }
}
