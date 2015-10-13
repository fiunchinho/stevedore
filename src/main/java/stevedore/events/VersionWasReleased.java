package stevedore.events;

import stevedore.messagebus.Message;

import java.util.Date;

public class VersionWasReleased implements Message {
    public final String project;
    public final String environment;
    public final String version;
    public final Date createdAt;

    public VersionWasReleased(String projectName, String environmentName, String releaseName) {
        project = projectName;
        environment = environmentName;
        version = releaseName;
        createdAt = new Date();
    }
}
