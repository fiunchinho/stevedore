package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.*;

import java.util.Optional;

public class DeployGivenRelease {
    private final ProjectRepository projectRepository;
    private final IMessageBus messageBus;

    public DeployGivenRelease(ProjectRepository projectRepository, IMessageBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void deploy(String projectId, String environmentName, String releaseName) throws ReleaseNotFoundException, ProjectNotFoundException {
        Project project = projectRepository
                .load(projectId)
                .orElseThrow(() -> new ProjectNotFoundException());

        Environment environment = project.getEnvironment(environmentName);
        environment.deploy(new Version(releaseName));

        environment.recordedEvents().forEach(messageBus::publish);
    }
}
