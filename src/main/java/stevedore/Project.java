package stevedore;

import java.util.HashMap;

public class Project {
    private final String name;
    private final String repository;
    private HashMap<String, Environment> environments = new HashMap<String, Environment>();

    public Project(String name, String repository) {
        this.name = name;
        this.repository = repository;
    }

    public String name() {
        return name;
    }

    public String repository() {
        return repository;
    }

    public HashMap getEnvironments() {
        return environments;
    }

    public void addEnvironment(Environment environment) {
        this.environments.put(environment.name(), environment);
    }

    public Environment getEnvironment(String environmentName) {
        return environments.get(environmentName);
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> project = new HashMap<>();
        project.put("name", name);
        project.put("repository", repository);

        return project;
    }
}
