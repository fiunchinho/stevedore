package stevedore;

import org.junit.Test;

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
        environment.release(new Release("1.0"));
        assertEquals(1, environment.releases().size());
    }

    @Test
    public void itDeploysARelease() throws ReleaseNotFoundException {
        Environment environment = getEnvironment("production");
        assertEquals(0, environment.deploys().size());
        Release release = new Release("1.0");

        environment.release(release);
        environment.deploy(release);

        assertEquals(1, environment.deploys().size());
        assertTrue(environment.currentRelease().equalsTo("1.0"));
    }

    @Test
    public void itDeploysLatestRelease() {
        Environment environment = getEnvironment("production");
        Release release1 = environment.release(new Release("1.0"));
        Release release2 = environment.release(new Release("2.0"));
        Deploy deploy = environment.deploy();
        assertEquals(release2, deploy.release());
        assertTrue(environment.currentRelease().equalsTo(release2));
    }

    private Environment getEnvironment(String name) {
        return new Environment(name, "eu-west-1", "vpc-123abc", "keys", getIrrelevantAwsIdentity());
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }
}