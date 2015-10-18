package stevedore.events;

import stevedore.Deploy;
import stevedore.Release;
import stevedore.messagebus.Message;

import java.util.Date;

public class DeployWasStarted implements Message {

    private final String environmentId;
    public final String version;
    private final Date createAt;

    public DeployWasStarted(String environmentId, String version) {
        this.environmentId = environmentId;
        this.version = version;
        this.createAt = new Date();
    }

    @Override
    public String getId() {
        return environmentId;
    }

    @Override
    public Date getCreatedAt() {
        return createAt;
    }
}
