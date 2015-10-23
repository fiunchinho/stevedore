package stevedore;

import java.util.Optional;

public interface ProjectRepository {
    void save(Project project);

    Optional<Project> load(String projectId);
}
