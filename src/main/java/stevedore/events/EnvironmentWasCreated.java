package stevedore.events;

import stevedore.messagebus.Message;

import java.util.Date;

public class EnvironmentWasCreated implements Message {
    private final String environmentId;
    public final String projectId;
    public final String environmentName;
    public final String region;
    public final String vpcId;
    public final String keypair;
    public final String accessKey;
    public final String secretKey;
    private final Date createdAt;

    public EnvironmentWasCreated(String projectId, String environmentId, String environmentName, String region, String vpcId, String keypair, String accessKey, String secretKey) {
        this.environmentId = environmentId;
        this.projectId = projectId;
        this.environmentName = environmentName;
        this.region = region;
        this.vpcId = vpcId;
        this.keypair = keypair;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.createdAt = new Date();
    }

    @Override
    public String getId() {
        return projectId;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }
}
