package stevedore;

import java.util.HashMap;

public interface ProjectRepository {
    void save(Project project);

    Project load(String projectName);
}
