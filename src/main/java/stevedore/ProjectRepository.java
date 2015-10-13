package stevedore;

import java.util.HashMap;

public interface ProjectRepository {
    void add(Project project);

    HashMap findAll();

    Project findOneByName(String projectName);
}
