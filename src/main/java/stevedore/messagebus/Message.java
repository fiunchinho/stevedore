package stevedore.messagebus;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public abstract class Message {
    protected UUID eventId;
    protected String eventType;
    protected HashMap<String, String> data = new HashMap<>();
    protected Date createdAt;

    public UUID getId() {
        return eventId;
    }

    public String eventType() {
        return eventType;
    }

    public HashMap<String, String> data() {
        return data;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
