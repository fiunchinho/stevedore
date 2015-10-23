package stevedore.events;

import stevedore.Environment;
import stevedore.Release;
import stevedore.messagebus.Message;

import java.util.Date;

public class ReleaseWasPushed implements Message {
    private final String environmentId;
    public final String version;
    private final Date createdAt;

    public ReleaseWasPushed(String environmentId, String version) {
        this.environmentId = environmentId;
        this.version = version;
        this.createdAt = new Date();
    }

    @Override
    public String getId() {
        return environmentId;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }
}
