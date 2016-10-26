package stevedore.infrastructure.read;

import stevedore.Project;

import java.util.HashMap;
import java.util.Optional;

public class InMemoryProjectRepository {
    HashMap<String, Project> projects = new HashMap();

    public Optional<Project> load(String projectId) {
        return Optional.of(projects.get(projectId));
    }

    public void save(Project project) {

    }
}
