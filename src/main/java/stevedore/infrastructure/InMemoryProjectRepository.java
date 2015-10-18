package stevedore.infrastructure;

import stevedore.Project;
import stevedore.ProjectRepository;

import java.util.HashMap;

public class InMemoryProjectRepository implements ProjectRepository {
    HashMap<String,Project> projects = new HashMap<String,Project>();

    @Override
    public void save(Project project) {
        projects.put(project.name(), project);
    }

    @Override
    public Project load(String projectName) {
        return projects.get(projectName);
    }

}
