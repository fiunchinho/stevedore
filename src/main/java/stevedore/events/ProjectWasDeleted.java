package stevedore.events;

import stevedore.messagebus.Message;

import java.util.Date;
import java.util.UUID;

public class ProjectWasDeleted extends Message {

    public ProjectWasDeleted(String projectId) {
        eventId = UUID.randomUUID().toString();
        eventType = ProjectWasDeleted.class.toString();
        data.put("projectId", projectId);
        createdAt = new Date();
    }
}
