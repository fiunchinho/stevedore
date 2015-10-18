package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import org.junit.Test;
import stevedore.*;
import stevedore.events.ReleaseWasPushed;
import stevedore.infrastructure.InMemoryProjectRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PushNewReleaseTest {

    private ProjectRepository projectRepository = new InMemoryProjectRepository();
    private IMessageBus messageBus = mock(IMessageBus.class);

    private Project project;
    private Environment environment;

    @Test
    public void ItReleasesNewVersion() {
        String projectName = "some-project";
        String environmentName = "prod";
        String releaseName = "1.23";

        getEnvironment("prod");
        environment.tagRelease(new Version(releaseName));
        ProjectBuilder buildProject = new ProjectBuilder();
        Project project = buildProject
                .withName(projectName)
                .withEnvironment(environment)
                .build();

        givenProject(project);
        whenReleaseIsPushedTo(projectName, environmentName, releaseName);

        Release release = environment.getRelease(releaseName);
        assertEquals(ReleaseStatus.ready(), release.status());

        thenReleaseStatusIs(releaseName, ReleaseStatus.ready());

        verify(messageBus, times(2)).publish(any(ReleaseWasPushed.class));
    }

    private Project givenProject(Project project) {
        projectRepository.save(project);
        this.project = project;

        return project;
    }

    private void whenReleaseIsPushedTo(String projectName, String environmentName, String releaseName) {
        PushNewRelease useCase = new PushNewRelease(projectRepository, messageBus);
        useCase.release(projectName, environmentName, releaseName);
    }

    private void thenReleaseStatusIs(String releaseName, ReleaseStatus.Status status) {
        Release release = environment.getRelease(releaseName);
        assertEquals(status, release.status());
    }

    private Environment getEnvironment(String name) {
        this.environment = new Environment(name, "eu-west-1", "vpc-123abc", "keys", getIrrelevantAwsIdentity());
        return environment;
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }

}