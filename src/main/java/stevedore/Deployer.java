package stevedore;

public interface Deployer {
    void release(Project project, Environment environment, Release release);
    void deploy(Project project, Environment environment, Version version);
}
