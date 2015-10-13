package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.*;
import stevedore.events.DeployWasRequested;

public class RequestDeploy {
    private final ProjectRepository projectRepository;
    private final IMessageBus messageBus;

    public RequestDeploy(ProjectRepository projectRepository, IMessageBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void deploy(String projectName, String environmentName, String releaseName) throws ReleaseNotFoundException {
        Project project = projectRepository.findOneByName(projectName);
        Environment environment = project.getEnvironment(environmentName);

        Version version = new Version(releaseName);
        Deploy deploy = environment.deploy(version);
        projectRepository.save(project);
        messageBus.publish(
                new DeployWasRequested(projectName, environmentName, releaseName)
        );
    }
}
