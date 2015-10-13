package stevedore;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class ProjectTest {
    @Test
    public void itAddsEnvironments() {
        ProjectBuilder buildProject = new ProjectBuilder();
        Project project = buildProject.withName("Some-Project").build();
        assertEquals(0, project.getEnvironments().size());

        Environment environment = getEnvironment("production");
        project.addEnvironment(environment);

        assertEquals(1, project.getEnvironments().size());
        assertEquals(environment, project.getEnvironment("production"));
    }

    @Test
    public void itReleasesNewVersions() {
        Environment environment = getEnvironment("production");
        assertEquals(0, environment.releases().size());
        Release release = environment.release(new Version("1.0"));
        assertEquals(1, environment.releases().size());
        assertThat(release, instanceOf(Release.class));
    }

    @Test
    public void itDeploysARelease() throws ReleaseNotFoundException {
        Environment environment = getEnvironment("production");
        assertEquals(0, environment.deploys().size());
        Version version = new Version("1.0");

        Release release = environment.release(version);
        Deploy deploy = environment.deploy(version);

        assertEquals(1, environment.deploys().size());
        assertEquals(release, environment.currentRelease());
        assertThat(deploy, instanceOf(Deploy.class));
    }

    @Test
    public void itDeploysLatestRelease() {
        Environment environment = getEnvironment("production");
        Version version1 = new Version("1.0");
        Version version2 = new Version("2.0");
        Release release1 = environment.release(version1);
        Release release2 = environment.release(version2);
        Deploy deploy = environment.deploy();
        assertEquals(release2, deploy.release());
        assertEquals(release2, environment.currentRelease());
    }

    private Environment getEnvironment(String name) {
        return new Environment(name, "eu-west-1", "vpc-123abc", "keys", getIrrelevantAwsIdentity());
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }
}