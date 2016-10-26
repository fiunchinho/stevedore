package stevedore;

import stevedore.infrastructure.ConnectionException;

public interface EnvironmentRepository {
    void save(Environment environment);

    Environment load(String environmentName) throws ConnectionException;
}
