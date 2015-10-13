package stevedore.infrastructure;

import stevedore.Project;
import stevedore.ProjectRepository;

import java.util.HashMap;

public class InMemoryProjectRepository implements ProjectRepository {
    HashMap<String,Project> projects = new HashMap<String,Project>();

    @Override
    public void add(Project project) {
        projects.put(project.name(), project);
    }

    @Override
    public HashMap findAll() {
        return projects;
    }

    @Override
    public Project findOneByName(String projectName) {
        return projects.get(projectName);
    }

}
