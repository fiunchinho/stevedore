package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.*;
import stevedore.events.ReleaseWasDeployed;
import stevedore.events.VersionWasReleased;
import stevedore.infrastructure.AnsibleDeployer;

public class DeployVersion {
    private final ProjectRepository projectRepository;
    private final Deployer deployer;
    private final IMessageBus messageBus;

    public DeployVersion(ProjectRepository projectRepository, Deployer deployer, IMessageBus messageBus) {
        this.projectRepository = projectRepository;
        this.deployer = deployer;
        this.messageBus = messageBus;
    }

    public void deploy(String projectName, String environmentName, String releaseName) throws ReleaseNotFoundException {
        Project project = projectRepository.findOneByName(projectName);
        Environment environment = project.getEnvironment(environmentName);

        Version version = new Version(releaseName);
        Deploy deploy = environment.deploy(version);

        try {
            deployer.deploy(project, environment, version);
            deploy.isSuccessful();
        } catch (Exception e) {
            deploy.isFailed();
        }finally {
            projectRepository.save(project);
            messageBus.publish(
                    new ReleaseWasDeployed(projectName, environmentName, releaseName)
            );
        }
    }
}
