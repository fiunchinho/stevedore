package stevedore;

public interface EnvironmentRepository {
    void save(Environment environment);

    Environment load(String environmentName);
}
