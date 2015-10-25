package stevedore.events;

import stevedore.messagebus.Message;

import java.util.Date;
import java.util.UUID;

public class ReleaseWasPushed extends Message {

    public ReleaseWasPushed(String environmentId, String version) {
        eventId = UUID.randomUUID().toString();
        eventType = ReleaseWasPushed.class.toString();
        data.put("environmentId", environmentId);
        data.put("version", version);
        createdAt = new Date();
    }
}
