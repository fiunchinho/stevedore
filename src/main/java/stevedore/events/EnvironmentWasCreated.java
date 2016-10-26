package stevedore.events;

import stevedore.messagebus.Message;

import java.util.Date;
import java.util.UUID;

public class EnvironmentWasCreated extends Message {

    public EnvironmentWasCreated(String projectId, String environmentId, String environmentName, String region, String vpcId, String keypair, String accessKey, String secretKey) {
        eventId = UUID.randomUUID();
        eventType = EnvironmentWasCreated.class.toString();
        data.put("projectId", projectId);
        data.put("environmentId", environmentId);
        data.put("environmentName", environmentName);
        data.put("region", region);
        data.put("vpcId", vpcId);
        data.put("keypair", keypair);
        data.put("accessKey", accessKey);
        data.put("secretKey", secretKey);
        createdAt = new Date();
    }
}
