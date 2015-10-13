package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.*;
import stevedore.events.DeployWasRequested;
import stevedore.events.VersionWasReleased;
import stevedore.infrastructure.AnsibleDeployer;

public class ReleaseVersion {
    private final ProjectRepository projectRepository;
    private final Deployer deployer;
    private final IMessageBus messageBus;

    public ReleaseVersion(ProjectRepository projectRepository, Deployer deployer, IMessageBus messageBus) {
        this.projectRepository = projectRepository;
        this.deployer = deployer;
        this.messageBus = messageBus;
    }

    public void release(String projectName, String environmentName, String releaseName) {
        Project project = projectRepository.findOneByName(projectName);
        Environment environment = project.getEnvironment(environmentName);

        Release release = environment.getRelease(releaseName);
        try {
            deployer.release(project, environment, release);
            release.isReady();
        } catch (Exception e) {
            release.isFailed();
        }finally {
            projectRepository.save(project);
            messageBus.publish(
                    new VersionWasReleased(projectName, environmentName, releaseName)
            );
        }
    }
}
