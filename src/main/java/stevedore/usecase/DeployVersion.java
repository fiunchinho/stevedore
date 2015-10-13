package stevedore.usecase;

import stevedore.*;
import stevedore.infrastructure.AnsibleDeployer;

public class DeployVersion {
    private final ProjectRepository projectRepository;
    private final Deployer deployer;

    public DeployVersion(ProjectRepository projectRepository, Deployer deployer) {
        this.projectRepository = projectRepository;
        this.deployer = deployer;
    }

    public void deploy(String projectName, String environmentName, String releaseName) throws ReleaseNotFoundException {
        Project project = projectRepository.findOneByName(projectName);
        Environment environment = project.getEnvironment(environmentName);
        Release release = new Release(releaseName);

        environment.deploy(release);
        deployer.deploy(project, environment, release);
    }
}
