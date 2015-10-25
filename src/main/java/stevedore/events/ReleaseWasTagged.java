package stevedore.events;

import stevedore.messagebus.Message;

import java.util.Date;
import java.util.UUID;

public class ReleaseWasTagged extends Message {

    public ReleaseWasTagged(String environmentId, String version) {
        eventId = UUID.randomUUID().toString();
        eventType = ReleaseWasTagged.class.toString();
        data.put("environmentId", environmentId);
        data.put("version", version);
        createdAt = new Date();
    }
}
