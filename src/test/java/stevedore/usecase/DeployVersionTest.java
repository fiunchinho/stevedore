package stevedore.usecase;

import org.junit.Before;
import org.junit.Test;
import stevedore.*;
import stevedore.infrastructure.AnsibleDeployer;
import stevedore.infrastructure.InMemoryProjectRepository;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DeployVersionTest {
    private ProjectRepository projectRepository;

    private Release getRelease(String version) {
        return new Release(version);
    }

    @Test(expected = ReleaseNotFoundException.class)
    public void ItFailsDeployingReleaseThatNotExists() throws ReleaseNotFoundException {
        String projectName = "some-project";
        String environmentName = "prod";

        Project project = givenProject(projectName);

        Deployer deployer = mock(Deployer.class);

        DeployVersion useCase = new DeployVersion(projectRepository, deployer);
        useCase.deploy(projectName, environmentName, "1.0");

        verify(deployer).deploy(eq(project), eq(project.getEnvironment(environmentName)), any(Release.class));
    }

    @Test
    public void ItDeploysNewRelease() throws ReleaseNotFoundException {
        String projectName = "some-project";
        String environmentName = "prod";

        Project project = givenProject(projectName);
        Environment environment = project.getEnvironment(environmentName);
        environment.release(getRelease("1.0"));
        environment.release(getRelease("2.0"));

        Deployer deployer = mock(Deployer.class);

        DeployVersion useCase = new DeployVersion(projectRepository, deployer);
        useCase.deploy(projectName, environmentName, "2.0");

        verify(deployer).deploy(eq(project), eq(project.getEnvironment(environmentName)), any(Release.class));
    }

    @Test
    public void ItRollsBackToPreviousRelease() throws ReleaseNotFoundException {
        String projectName = "some-project";
        String environmentName = "prod";

        Project project = givenProject(projectName);
        Environment environment = project.getEnvironment(environmentName);
        environment.release(getRelease("1.0"));
        environment.release(getRelease("2.0"));
        environment.deploy();

        Deployer deployer = mock(Deployer.class);

        DeployVersion useCase = new DeployVersion(projectRepository, deployer);
        useCase.deploy(projectName, environmentName, "1.0");

        assertTrue(environment.currentRelease().equalsTo("1.0"));

        verify(deployer).deploy(eq(project), eq(project.getEnvironment(environmentName)), any(Release.class));
    }

    private Project givenProject(String projectName) {
        Environment environment = getEnvironment("prod");
        ProjectBuilder buildProject = new ProjectBuilder();
        Project project = buildProject
                .withName(projectName)
                .withEnvironment(environment)
                .build();

        projectRepository.add(project);

        return project;
    }

    @Before
    public void getProjectRepository() {
        projectRepository = new InMemoryProjectRepository();
    }

    private Environment getEnvironment(String name) {
        return new Environment(name, "eu-west-1", "vpc-123abc", "keys", getIrrelevantAwsIdentity());
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }

}