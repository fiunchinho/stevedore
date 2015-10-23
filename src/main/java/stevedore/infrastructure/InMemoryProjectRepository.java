package stevedore.infrastructure;

import stevedore.Project;
import stevedore.ProjectRepository;

import java.util.HashMap;
import java.util.Optional;

public class InMemoryProjectRepository implements ProjectRepository {
    HashMap<String,Project> projects = new HashMap<>();

    @Override
    public void save(Project project) {
        projects.put(project.id(), project);
    }

    @Override
    public Optional<Project> load(String projectId) {
        return Optional.ofNullable(projects.get(projectId));
    }

}
