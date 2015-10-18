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

    private Environment getEnvironment(String name) {
        return new Environment(name, "eu-west-1", "vpc-123abc", "keys", getIrrelevantAwsIdentity());
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }
}