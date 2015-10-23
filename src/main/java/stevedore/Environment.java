package stevedore;

import stevedore.events.*;
import stevedore.messagebus.Message;

import java.util.*;

public class Environment {
    private String id;
    private String name;
    private Release currentRelease = null;
    private String region;
    private String vpcId;
    private String keypair;
    private AwsIdentity awsIdentity;
    private TreeMap<String, Release> releases = new TreeMap<String, Release>();
    private TreeMap<String, Deploy> deploys = new TreeMap();
    private HashMap<String, String> options = new HashMap<>();
    private List<Message> recordedEvents = new ArrayList<>();

    public Environment() {
    }

    public static Environment create(String projectId, String environmentName, String region, String vpcId, String keypair, AwsIdentity awsIdentity) {
        Environment environment = new Environment();
        Message event = new EnvironmentWasCreated(projectId, UUID.randomUUID().toString(), environmentName, region, vpcId, keypair, awsIdentity.accessKey(), awsIdentity.secretKey());
        environment.apply(event);
        environment.recordedEvents.add(event);

        return environment;
    }

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

    public TreeMap releases() {
        return releases;
    }

    public Release getRelease(String releaseName) {
        return releases.get(releaseName);
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

        startDeploy(releases.lastEntry().getValue().version());
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
    }

    private void applyEnvironmentWasCreated(EnvironmentWasCreated event) {
        name = event.environmentName;
        region = event.region;
        vpcId = event.vpcId;
        keypair = event.keypair;
        awsIdentity = new AwsIdentity(event.accessKey, event.secretKey);
    }

    private void applyReleaseWasTagged(ReleaseWasTagged event) {
        releases.put(event.version, new Release(new Version(event.version)));
    }

    private void applyReleaseWasPushed(ReleaseWasPushed event) {
        Release release = findRelease(new Version(event.version))
                .orElseThrow(() -> new ReleaseNotFoundException());
        release.setStatus(ReleaseStatus.ready());
    }

    private void applyDeployWasStarted(DeployWasStarted event) {
        Release release = getRelease(event.version);
        if (release == null) {
            throw new ReleaseNotFoundException();
        }

        deploys.put(event.version, new Deploy(release));
    }

    private void applyDeployWasMade(DeployWasMade event) {
        Deploy deploy = findDeploy(event.version)
                .orElseThrow(() -> new DeployNotFoundException());
        currentRelease = deploy.release();
        deploy.setStatus(DeployStatus.Status.SUCCESSFUL);
    }

    private Optional<Release> findRelease(Version version) {
        return Optional.ofNullable(releases.get(version.version()));
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
