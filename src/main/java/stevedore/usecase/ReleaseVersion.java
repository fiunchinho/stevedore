package stevedore.usecase;

import stevedore.*;
import stevedore.infrastructure.AnsibleDeployer;

public class ReleaseVersion {
    private final ProjectRepository projectRepository;
    private final Deployer deployer;

    public ReleaseVersion(ProjectRepository projectRepository, Deployer deployer) {
        this.projectRepository = projectRepository;
        this.deployer = deployer;
    }

    public void release(String projectName, String environmentName, String releaseName) {
        Project project = projectRepository.findOneByName(projectName);
        Environment environment = project.getEnvironment(environmentName);

        Release release = environment.release(new Version(releaseName));
        deployer.release(project, environment, release);
    }
}
