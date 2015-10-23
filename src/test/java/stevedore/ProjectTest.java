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

        String environmentName = "production";
        String region = "eu-west-1";
        project.addEnvironment(environmentName, region, "vpc-123abc", "keys", getIrrelevantAwsIdentity());

        assertEquals(1, project.getEnvironments().size());
        Environment environment = project.getEnvironment(environmentName);
        assertEquals(environment.name(), environmentName);
        assertEquals(environment.region(), region);
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }
}