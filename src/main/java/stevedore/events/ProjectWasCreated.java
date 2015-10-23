package stevedore.events;

import stevedore.messagebus.Message;

import java.util.Date;

public class ProjectWasCreated implements Message {
    private final String projectId;
    public final String repository;
    private final Date createdAt;

    public ProjectWasCreated(String id, String repository) {
        this.projectId = id;
        this.repository = repository;
        this.createdAt = new Date();
    }

    @Override
    public String getId() {
        return projectId;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }
}
