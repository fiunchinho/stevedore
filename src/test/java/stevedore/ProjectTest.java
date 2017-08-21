package stevedore;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProjectTest {

    @Test
    public void itParsesProjectId() throws Exception {
        Project project = Project.create("vendorName/project-name");
        assertEquals("Project id is the full repository name", "vendorName/project-name", project.id());
        assertEquals("Vendor name contains the name of the company", "vendorName", project.vendor());
        assertEquals("Name is the repository name without the vendor", "project-name", project.name());
    }

    @Test
    public void itAddsEnvironments() {
        ProjectBuilder buildProject = new ProjectBuilder();
        Project project = buildProject.withId("vendor/project").build();
        assertEquals(0, project.getEnvironments().size());

        String environmentName = "production";
        String region = "eu-west-1";
        project.addEnvironment(environmentName, region, "vpc-123abc", "keys", getIrrelevantAwsIdentity());

        assertEquals(1, project.getEnvironments().size());
        Environment environment = project.getEnvironment(environmentName).get();
        assertEquals(environment.name(), environmentName);
    }

    private AwsIdentity getIrrelevantAwsIdentity() {
        return new AwsIdentity("access", "secret");
    }
}