package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import org.junit.Test;
import stevedore.*;
import stevedore.events.VersionWasReleased;
import stevedore.infrastructure.InMemoryProjectRepository;

import static org.junit.Assert.assertEquals;
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

        Environment environment = getEnvironment("prod");
        Release release = environment.release(new Version(releaseName));
        ProjectBuilder buildProject = new ProjectBuilder();
        Project project = buildProject
                .withName(projectName)
                .withEnvironment(environment)
                .build();

        givenProject(project);

        Deployer deployer = mock(Deployer.class);
        IMessageBus messageBus = mock(IMessageBus.class);
//        when(deployer.release(project, environmentName, releaseName)).thenReturn(new Release(releaseName));

        assertEquals(ReleaseStatus.inProgress(), release.status());
        ReleaseVersion useCase = new ReleaseVersion(projectRepository, deployer, messageBus);
        useCase.release(projectName, environmentName, releaseName);
        assertEquals(ReleaseStatus.ready(), release.status());

        verify(deployer).release(eq(project), eq(project.getEnvironment(environmentName)), any(Release.class));
        verify(messageBus).publish(any(VersionWasReleased.class));
    }

    private Project givenProject(Project project) {
        projectRepository.save(project);

        return project;
    }

    private Environment getEnvironment(String name) {
        return new Environment(name, "eu-west-1", "vpc-123abc", "keys", getIrrelevantAwsIdentity());
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }

}