package stevedore;

import stevedore.events.EnvironmentWasCreated;
import stevedore.events.ProjectWasCreated;
import stevedore.messagebus.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Project {
    private String id;
    private String name;
    private String repository;
    private HashMap<String, Environment> environments = new HashMap<String, Environment>();
    private List<Message> recordedEvents = new ArrayList<>();
    private String vendor;

    public Project(String name, String repository) {
        this.id = name;
        this.name = name;
        this.repository = repository;
    }

    public Project() {
    }

    public Project(String projectName) {
        this.id = projectName;
        this.name = projectName;
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
        Message event = new EnvironmentWasCreated(id, environment.name(), environment.region(), environment.vpcId(), environment.keypair(), environment.awsIdentity().accessKey(), environment.awsIdentity().secretKey());
        apply(event);
        recordedEvents.add(event);

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

    public static Project create(String projectRepository) {
        Project project = new Project();
        Message event = new ProjectWasCreated(UUID.randomUUID().toString(), projectRepository);
        project.apply(event);
        project.recordedEvents.add(event);

        return project;
    }

    public void apply(Message event) {
        apply(event);
    }

    public void apply(ProjectWasCreated event) {
        id = event.getId();
        name = extractProjectNameFrom(event.repository);
        vendor = extractVendorNameFrom(event.repository);
        repository = event.repository;
    }

    public void apply(EnvironmentWasCreated event) {
        Environment environment = new Environment(event.environmentName, event.region, event.vpcId, event.keypair, new AwsIdentity(event.accessKey, event.secretKey));
        environments.put(event.environmentName, environment);
    }

    private String extractProjectNameFrom(String repository) {
        return repository.split("/")[1];
    }

    private String extractVendorNameFrom(String repository) {
        return repository.split("/")[1];
    }

    public List<Message> recordedEvents() {
        return recordedEvents;
    }
}
