package stevedore;

import stevedore.events.EnvironmentWasCreated;
import stevedore.events.ProjectWasCreated;
import stevedore.events.ProjectWasDeleted;
import stevedore.messagebus.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Project {
    private String id;
    private Integer version = 0;
    private String name;
    private String repository;
    private HashMap<String, Environment> environments = new HashMap<>();
    private List<Message> recordedEvents = new ArrayList<>();
    private String vendor;

    public Project() {
    }

    public static Project create(String projectRepository) {
        Project project = new Project();
        Message event = new ProjectWasCreated(UUID.randomUUID().toString(), projectRepository);
        project.apply(event);
        project.recordedEvents.add(event);

        return project;
    }

    public static Project create(UUID projectId, String projectRepository) {
        Project project = new Project();
        Message event = new ProjectWasCreated(projectId.toString(), projectRepository);
        project.apply(event);
        project.recordedEvents.add(event);

        return project;
    }

    public String id() {
        return id;
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

    public void addEnvironment(String environmentName, String region, String vpcId, String keypair, AwsIdentity awsIdentity) {
        Message event = new EnvironmentWasCreated(id, UUID.randomUUID().toString(), environmentName, region, vpcId, keypair, awsIdentity.accessKey(), awsIdentity.secretKey());
        apply(event);
        recordedEvents.add(event);
    }

    public void addEnvironment(Environment environment) {
        environments.put(environment.name(), environment);
    }

    public Environment getEnvironment(String environmentName) {
        return environments.get(environmentName);
    }

    public void delete() {
        Message event = new ProjectWasDeleted(id);
        apply(event);
        recordedEvents.add(event);
    }

    public void apply(Message event) {
        if (event instanceof ProjectWasCreated) {
            applyProjectWasCreated((ProjectWasCreated) event);
        } else if (event instanceof EnvironmentWasCreated) {
            applyEnvironmentWasCreated((EnvironmentWasCreated) event);
        }
        version++;
    }

    public void applyProjectWasCreated(ProjectWasCreated event) {
        id = event.data().get("projectId");
        repository = event.data().get("repository");
        name = extractProjectNameFrom(event.data().get("repository"));
        vendor = extractVendorNameFrom(event.data().get("repository"));
    }

    public void applyEnvironmentWasCreated(EnvironmentWasCreated event) {
        Environment environment = Environment.create(id, event.data().get("environmentName"), event.data().get("region"), event.data().get("vpcId"), event.data().get("keypair"), new AwsIdentity(event.data().get("accessKey"), event.data().get("secretKey")));
        environments.put(event.data().get("environmentName"), environment);
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> project = new HashMap<>();
        project.put("name", name);
        project.put("repository", repository);

        return project;
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
