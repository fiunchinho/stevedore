package stevedore.events;

import stevedore.messagebus.Message;

import java.util.Date;
import java.util.UUID;

public class DeployWasStarted extends Message {

    public DeployWasStarted(String environmentId, String version) {
        eventId = UUID.randomUUID().toString();
        eventType = DeployWasStarted.class.toString();
        data.put("environmentId", environmentId);
        data.put("version", version);
        createdAt = new Date();
    }
}
