package stevedore;

import java.util.ArrayList;

public class ProjectBuilder {
    private String name = "some-project";
    private String repository = "vendor/repository";
    private ArrayList<Environment> environments = new ArrayList<Environment>();

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
        Project project = new Project(name, repository);
        environments.forEach(environment -> project.addEnvironment(environment));

        return project;
    }
}
