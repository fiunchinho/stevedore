package stevedore.events;

import stevedore.messagebus.Message;

import java.util.Date;

public class ProjectWasCreated implements Message {
    private final String id;
    public final String repository;
    private final Date createAt;

    public ProjectWasCreated(String id, String repository) {
        this.id = id;
        this.repository = repository;
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
