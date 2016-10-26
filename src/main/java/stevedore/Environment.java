package stevedore;

import stevedore.events.*;
import stevedore.messagebus.Message;

import java.util.*;

public class Environment {
    private String id;
    private String projectId;
    private Integer version = 0;
    private String name;
    private Release currentRelease = null;
    private String region;
    private String vpcId;
    private String keypair;
    private AwsIdentity awsIdentity;
    private ArrayList<Release> releases = new ArrayList();
    private TreeMap<String, Deploy> deploys = new TreeMap();
    private HashMap<String, String> options = new HashMap();
    private List<Message> recordedEvents = new ArrayList();

    public Environment() {
    }

    public Environment(String environmentId, String projectId, String environmentName, String region, String vpcId, String keypair, AwsIdentity awsIdentity) {
        this.id = environmentId;
        this.projectId = projectId;
        this.name = environmentName;
        this.region = region;
        this.vpcId = vpcId;
        this.keypair = keypair;
        this.awsIdentity = awsIdentity;
    }

    public static Environment create(String projectId, String environmentName, String region, String vpcId, String keypair, AwsIdentity awsIdentity) {
        Environment environment = new Environment();
        Message event = new EnvironmentWasCreated(projectId, UUID.randomUUID().toString(), environmentName, region, vpcId, keypair, awsIdentity.accessKey(), awsIdentity.secretKey());
        environment.apply(event);
        environment.recordedEvents.add(event);

        return environment;
    }

    /**
     * Idempotent factory method.
     *
     * @param environmentId
     * @param projectId
     * @param environmentName
     * @param region
     * @param vpcId
     * @param keypair
     * @param awsIdentity
     * @return
     */
    public static Environment create(String environmentId, String projectId, String environmentName, String region, String vpcId, String keypair, AwsIdentity awsIdentity) {
        Environment environment = new Environment();
        Message event = new EnvironmentWasCreated(projectId, environmentId, environmentName, region, vpcId, keypair, awsIdentity.accessKey(), awsIdentity.secretKey());
        environment.apply(event);
        environment.recordedEvents.add(event);

        return environment;
    }

    public String id() { return id; }

    public String name() {
        return name;
    }

    public String region() {
        return region;
    }

    public String vpcId() {
        return vpcId;
    }

    public String keypair() {
        return keypair;
    }

    public AwsIdentity awsIdentity() {
        return awsIdentity;
    }

    public HashMap<String, String> options() {
        return options;
    }

    public Release currentRelease() {
        return currentRelease;
    }

    public ArrayList<Release> releases() {
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

    public void tagRelease(Version version) {
        ReleaseWasTagged event = new ReleaseWasTagged(this.id, version.version());
        apply(event);
        recordedEvents.add(event);
    }

    public void release(Version version) {
        ReleaseWasPushed event = new ReleaseWasPushed(this.id, version.version());
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
        DeployWasStarted event = new DeployWasStarted(this.id, versionToDeploy.version());
        apply(event);
        recordedEvents.add(event);
    }

    public void deploy(Version versionToDeploy) {
        DeployWasMade event = new DeployWasMade(this.id, versionToDeploy.version());
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
        } else if(event instanceof ReleaseWasTagged){
            applyReleaseWasTagged((ReleaseWasTagged) event);
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
        id = event.data().get("environmentId");
        projectId = event.data().get("projectId");
        name = event.data().get("environmentName");
        region = event.data().get("region");
        vpcId = event.data().get("vpcId");
        keypair = event.data().get("keypair");
        awsIdentity = new AwsIdentity(event.data().get("accessKey"), event.data().get("secretKey"));
    }

    private void applyReleaseWasTagged(ReleaseWasTagged event) {
        releases.add(new Release(new Version(event.data().get("version"))));
//        releases.put(event.data().get("version"), new Release(new Version(event.data().get("version"))));
    }

    private void applyReleaseWasPushed(ReleaseWasPushed event) {
        Release release = findRelease(new Version(event.data().get("version")))
                .orElseThrow(() -> new ReleaseNotFoundException());
        release.setStatus(ReleaseStatus.ready());
    }

    private void applyDeployWasStarted(DeployWasStarted event) {
        Release release = getRelease(event.data().get("version"))
                .orElseThrow(() -> new ReleaseNotFoundException());

        deploys.put(event.data().get("version"), new Deploy(release));
    }

    private void applyDeployWasMade(DeployWasMade event) {
        Deploy deploy = findDeploy(event.data().get("version"))
                .orElseThrow(() -> new DeployNotFoundException());
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
        environment.put("region", region);
        environment.put("vpcId", vpcId);
        environment.put("keypair", keypair);
        environment.put("awsIdentity", awsIdentity);
        options.entrySet().forEach(option -> environment.put(option.getKey(), option.getValue()));

        return environment;
    }

    public List<Message> recordedEvents() {
        return recordedEvents;
    }
}
