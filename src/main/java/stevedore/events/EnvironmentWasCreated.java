package stevedore.events;

import stevedore.messagebus.Message;

import java.util.Date;

public class EnvironmentWasCreated implements Message {
    private final String id;
    public final String environmentName;
    public final String region;
    public final String vpcId;
    public final String keypair;
    public final String accessKey;
    public final String secretKey;
    private final Date createAt;

    public EnvironmentWasCreated(String id, String environmentName, String region, String vpcId, String keypair, String accessKey, String secretKey) {
        this.id = id;
        this.environmentName = environmentName;
        this.region = region;
        this.vpcId = vpcId;
        this.keypair = keypair;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.createAt = new Date();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Date getCreatedAt() {
        return createAt;
    }
}
