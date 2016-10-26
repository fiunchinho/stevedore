package stevedore;

import stevedore.infrastructure.ConnectionException;

import java.util.Optional;

public interface ProjectRepository {
    void save(Project project);

    Optional<Project> load(String projectId) throws ConnectionException;
}
