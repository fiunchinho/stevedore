package stevedore;

import stevedore.events.EnvironmentWasCreated;
import stevedore.events.ProjectWasCreated;
import stevedore.events.ProjectWasDeleted;
import stevedore.messagebus.Message;

import java.util.*;

public class Project {
    /**
     * Id of the project, composed by vendor and project name from Github.
     */
    private String id;

    /**
     * Auto-increment version for every event applied.
     */
    private Integer version = 0;

    /**
     * Environments for this project.
     */
    private ArrayList<Environment> environments = new ArrayList();

    /**
     * Events related to this project.
     */
    private List<Message> recordedEvents = new ArrayList<>();

    public Project() {}

    public static Project create(String projectRepository) {
        Project project = new Project();
        Message event = new ProjectWasCreated(projectRepository);
        project.apply(event);
        project.recordedEvents.add(event);

        return project;
    }

    public String id() {
        return id;
    }

    public String name() {
        return extractProjectNameFrom(id);
    }

    public String vendor() {
        return extractVendorNameFrom(id);
    }

    public Integer version() {
        return version;
    }

    public ArrayList getEnvironments() {
        return environments;
    }

    public List<Message> recordedEvents() {
        return recordedEvents;
    }

    public void addEnvironment(Environment environment) {
        environments.add(environment);
    }

    public void addEnvironment(String environmentName, String region, String vpcId, String keypair, AwsIdentity awsIdentity) {
        Message event = new EnvironmentWasCreated(environmentName);
        apply(event);
        recordedEvents.add(event);
    }

    public Optional<Environment> getEnvironment(String environmentName) {
        for (Environment environment : environments) {
            if (environment.name().equals(environmentName)) {
                return Optional.of(environment);
            }
        }

        return Optional.empty();
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
        id = event.data().get("repository");
    }

    public void applyEnvironmentWasCreated(EnvironmentWasCreated event) {
        addEnvironment(
            new Environment(event.data().get("environmentName"))
        );
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> project = new HashMap<>();
        project.put("id", id);
        project.put("vendor", vendor());
        project.put("name", name());

        return project;
    }

    private String extractProjectNameFrom(String repository) {
        return repository.split("/")[1];
    }

    private String extractVendorNameFrom(String repository) {
        return repository.split("/")[0];
    }
}
