package stevedore.messagebus;

import java.util.Date;
import java.util.HashMap;

public abstract class Message {
    protected String eventId;
    protected String eventType;
    protected HashMap<String, String> data = new HashMap<>();
    protected Date createdAt;

    public String getId() {
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
