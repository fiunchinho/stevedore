package stevedore.infrastructure.read;

import stevedore.AwsIdentity;
import stevedore.Deploy;
import stevedore.Version;
import stevedore.messagebus.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

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

    public Environment(String id, String name, String region, String vpcId, String keypair, AwsIdentity awsIdentity) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.vpcId = vpcId;
        this.keypair = keypair;
        this.awsIdentity = awsIdentity;
    }

    public Environment() {
    }

    public String getId() {
        return id;
    }

    public String getProjectId() {
        return projectId;
    }

    public Integer getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public Release getCurrentRelease() {
        return currentRelease;
    }

    public String getRegion() {
        return region;
    }

    public String getVpcId() {
        return vpcId;
    }

    public String getKeypair() {
        return keypair;
    }

    public AwsIdentity getAwsIdentity() {
        return awsIdentity;
    }

    public ArrayList<Release> getReleases() {
        return releases;
    }

    public void addRelease(String version) {
        releases.add(new Release(new Version(version)));
    }

    public TreeMap<String, Deploy> getDeploys() {
        return deploys;
    }

    public HashMap<String, String> getOptions() {
        return options;
    }
}
