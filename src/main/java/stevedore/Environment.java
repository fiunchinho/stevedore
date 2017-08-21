package stevedore;

import stevedore.events.*;
import stevedore.messagebus.Message;

import java.util.*;

public class Environment {
    /**
     * Name of the environment. Must be unique within the project.
     */
    private String name;

    /**
     * Auto-increment version for every event applied.
     */
    private Integer version = 0;

    /**
     * Current release in this environment.
     */
    private Release currentRelease = null;

    /**
     * Releases available in this environment.
     */
    private List<Release> releases = new ArrayList<>();

    /**
     * Deployments made to this environment.
     */
    private TreeMap<String, Deploy> deploys = new TreeMap<>();

    /**
     * Configuration for this environment.
     */
    private HashMap<String, String> options = new HashMap<>();

    /**
     * Events related to this project.
     */
    private List<Message> recordedEvents = new ArrayList<>();

    public Environment() {
    }

    public Environment(String environmentName) {
        this.name = environmentName;
    }

    public static Environment create(String environmentName) {
        Environment environment = new Environment();
        Message event = new EnvironmentWasCreated(environmentName);
        environment.apply(event);
        environment.recordedEvents.add(event);

        return environment;
    }

    public String name() {
        return name;
    }

    public Integer version() {
        return version;
    }

    public Release currentRelease() {
        return currentRelease;
    }

    public List<Release> releases() {
        return releases;
    }

    public Optional<Release> getRelease(String releaseName) {
        for (Release release : releases) {
            if (release.equals(releaseName)) {
                return Optional.of(release);
            }
        }

        return Optional.empty();
    }

    public Deploy getDeploy(String releaseName) {
        return deploys.get(releaseName);
    }

    public TreeMap<String, Deploy> deploys() {
        return deploys;
    }

    public void release(Version version) {
        ReleaseWasPushed event = new ReleaseWasPushed(this.name, version.version());
        apply(event);
        recordedEvents.add(event);
    }

    public void startDeploy() {
        if (releases.isEmpty()) {
            throw new ReleaseNotFoundException();
        }

        startDeploy(releases.get(releases.size() - 1).version());
    }

    public void startDeploy(Version versionToDeploy) throws ReleaseNotFoundException {
        DeployWasStarted event = new DeployWasStarted(this.name, versionToDeploy.version());
        apply(event);
        recordedEvents.add(event);
    }

    public void deploy(Version versionToDeploy) {
        DeployWasMade event = new DeployWasMade(this.name, versionToDeploy.version());
        apply(event);
        recordedEvents.add(event);
    }

    public void deploy() {
        if (deploys.isEmpty()) {
            throw new DeployNotFoundException();
        }

        deploy(deploys.lastEntry().getValue().release().version());
    }

    public void apply(Message event) {
        if(event instanceof EnvironmentWasCreated){
            applyEnvironmentWasCreated((EnvironmentWasCreated) event);
        } else if(event instanceof ReleaseWasPushed){
            applyReleaseWasPushed((ReleaseWasPushed) event);
        } else if(event instanceof DeployWasStarted){
            applyDeployWasStarted((DeployWasStarted) event);
        } else if(event instanceof DeployWasMade){
            applyDeployWasMade((DeployWasMade) event);
        }
        version++;
    }

    private void applyEnvironmentWasCreated(EnvironmentWasCreated event) {
        name = event.data().get("environmentName");
    }

    private void applyReleaseWasPushed(ReleaseWasPushed event) {
        releases.add(new Release(new Version(event.data().get("version"))));
    }

    private void applyDeployWasStarted(DeployWasStarted event) {
        Release release = getRelease(event.data().get("version"))
                .orElseThrow(ReleaseNotFoundException::new);

        deploys.put(event.data().get("version"), new Deploy(release));
    }

    private void applyDeployWasMade(DeployWasMade event) {
        Deploy deploy = findDeploy(event.data().get("version"))
                .orElseThrow(DeployNotFoundException::new);
        currentRelease = deploy.release();
        deploy.setStatus(DeployStatus.Status.SUCCESSFUL);
    }

    private Optional<Release> findRelease(Version version) {
        for (Release release : releases) {
            if (release.equals(version)) {
                return Optional.of(release);
            }
        }

        return Optional.empty();
    }

    private Optional<Deploy> findDeploy(String version) {
        return Optional.ofNullable(deploys.get(version));
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> environment = new HashMap<>();
        environment.put("name", name);
        options.entrySet().forEach(option -> environment.put(option.getKey(), option.getValue()));

        return environment;
    }

    public List<Message> recordedEvents() {
        return recordedEvents;
    }
}
