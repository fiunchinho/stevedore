package stevedore.events;

import stevedore.messagebus.Message;

import java.util.Date;
import java.util.UUID;

public class DeployWasMade extends Message {

    public DeployWasMade(String environmentId, String version) {
        eventId = UUID.randomUUID().toString();
        eventType = DeployWasMade.class.toString();
        data.put("environmentId", environmentId);
        data.put("version", version);
        createdAt = new Date();
    }
}
