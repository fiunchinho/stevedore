package stevedore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;

public class Environment {
    private String name;
    private Release currentRelease = null;
    private String region;
    private String vpcId;
    private String keypair;
    private AwsIdentity awsIdentity;
    private TreeMap<String, Release> releases = new TreeMap<String, Release>();
    private ArrayList<Deploy> deploys = new ArrayList();
    private HashMap<String, String> options = new HashMap<>();

    public Environment(String name, String region, String vpcId, String keypair, AwsIdentity awsIdentity) {
        this.name = name;
        this.region = region;
        this.vpcId = vpcId;
        this.keypair = keypair;
        this.awsIdentity = awsIdentity;
    }

    public Environment(String name, String region, String vpcId, String keypair, AwsIdentity awsIdentity, HashMap options) {
        this.name = name;
        this.region = region;
        this.vpcId = vpcId;
        this.keypair = keypair;
        this.awsIdentity = awsIdentity;
        this.options = options;
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

    public ArrayList deploys() {
        return deploys;
    }

    public Release release(Release release) {
        releases.put(release.version(), release);

        return release;
    }

    public Deploy deploy() {
        return doDeployRelease(releases.pollLastEntry().getValue());
    }

    public Deploy deploy(Release releaseToDeploy) throws ReleaseNotFoundException {
        Release release = getRelease(releaseToDeploy.version());
        if (release == null) {
            throw new ReleaseNotFoundException();
        }

        return doDeployRelease(release);
    }

    private Deploy doDeployRelease(Release release) {
        Deploy deploy = new Deploy(release);
        deploys.add(deploy);
        currentRelease = release;

        return deploy;
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
}
