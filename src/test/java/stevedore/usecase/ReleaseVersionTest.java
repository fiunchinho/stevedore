package stevedore.usecase;

import org.junit.Test;
import stevedore.*;
import stevedore.infrastructure.AnsibleDeployer;
import stevedore.infrastructure.InMemoryProjectRepository;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ReleaseVersionTest {

    private ProjectRepository projectRepository = new InMemoryProjectRepository();

    @Test
    public void ItReleasesNewVersion() {
        String projectName = "some-project";
        String environmentName = "prod";
        String releaseName = "1.23";

        Project project = givenProject(projectName);

        Deployer deployer = mock(Deployer.class);
//        when(deployer.release(project, environmentName, releaseName)).thenReturn(new Release(releaseName));

        ReleaseVersion useCase = new ReleaseVersion(projectRepository, deployer);
        useCase.release(projectName, environmentName, releaseName);

        verify(deployer).release(eq(project), eq(project.getEnvironment(environmentName)), any(Release.class));
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

    private Environment getEnvironment(String name) {
        return new Environment(name, "eu-west-1", "vpc-123abc", "keys", getIrrelevantAwsIdentity());
    }

    private Release getRelease(String version) {
        return new Release(version);
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }

}