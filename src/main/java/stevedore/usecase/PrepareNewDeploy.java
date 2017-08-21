package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.*;
import stevedore.infrastructure.ConnectionException;

public class PrepareNewDeploy {
    private final ProjectRepository projectRepository;
    private final IMessageBus messageBus;

    public PrepareNewDeploy(ProjectRepository projectRepository, IMessageBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void deploy(String projectId, String environmentName, String releaseName) throws ReleaseNotFoundException, ProjectNotFoundException, ConnectionException, EnvironmentNotFoundException {
        Project project = projectRepository
                .load(projectId)
                .orElseThrow(ProjectNotFoundException::new);

        Environment environment = project
                .getEnvironment(environmentName)
                .orElseThrow(EnvironmentNotFoundException::new);

        environment.startDeploy(new Version(releaseName));
        environment.recordedEvents().forEach(messageBus::publish);
    }
}
