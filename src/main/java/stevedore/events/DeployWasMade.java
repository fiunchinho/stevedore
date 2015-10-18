package stevedore.events;

import stevedore.messagebus.Message;

import java.util.Date;

public class DeployWasMade implements Message {
    private final String environmentId;
    public final String version;
    private final Date createdAt;

    public DeployWasMade(String environmentId, String version) {
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
