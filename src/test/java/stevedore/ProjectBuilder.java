package stevedore;

import java.util.ArrayList;
import java.util.UUID;

public class ProjectBuilder {
    private UUID id;
    private String name = "project";
    private String vendor = "vendor";
    private String repository = "vendor/project";
    private ArrayList<Environment> environments = new ArrayList<>();

    public ProjectBuilder withId(UUID projectId) {
        this.id = projectId;
        return this;
    }

    public ProjectBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProjectBuilder withRepository(String repository) {
        this.repository = repository;
        return this;
    }

    public ProjectBuilder withEnvironment(Environment environment) {
        this.environments.add(environment);
        return this;
    }

    public Project build() {
        Project project = (id == null)? Project.create(name + "/" + vendor) : Project.create(id, name + "/" + vendor);

        environments.forEach(environment -> project.addEnvironment(environment));

        return project;
    }
}
