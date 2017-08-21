package stevedore.events;

import stevedore.messagebus.Message;

import java.util.Date;
import java.util.UUID;

public class EnvironmentWasCreated extends Message {

    public EnvironmentWasCreated(String environmentName) {
        eventId = UUID.randomUUID();
        eventType = EnvironmentWasCreated.class.toString();
        data.put("environmentName", environmentName);
        createdAt = new Date();
    }
}
