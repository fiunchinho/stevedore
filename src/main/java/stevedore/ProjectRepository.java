package stevedore;

import java.util.HashMap;

public interface ProjectRepository {
    void save(Project project);

    HashMap findAll();

    Project findOneByName(String projectName);
}
