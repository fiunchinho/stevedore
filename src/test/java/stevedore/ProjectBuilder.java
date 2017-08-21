package stevedore;

import java.util.ArrayList;

public class ProjectBuilder {
    private String id;
    private ArrayList<Environment> environments = new ArrayList<>();

    public ProjectBuilder withId(String projectId) {
        this.id = projectId;
        return this;
    }

    public ProjectBuilder withEnvironment(Environment environment) {
        this.environments.add(environment);
        return this;
    }

    public Project build() {
        Project project = Project.create(id);

        environments.forEach(project::addEnvironment);

        return project;
    }
}
