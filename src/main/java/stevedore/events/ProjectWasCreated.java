package stevedore.events;

import stevedore.messagebus.Message;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class ProjectWasCreated extends Message {

    public ProjectWasCreated(String projectId, String projectRepository) {
        eventId = UUID.randomUUID();
        eventType = ProjectWasCreated.class.toString();
        data.put("projectId", projectId);
        data.put("projectName", extractProjectNameFrom(projectRepository));
        data.put("vendor", extractVendorNameFrom(projectRepository));
        data.put("repository", projectRepository);
        createdAt = new Date();
    }

    private String extractProjectNameFrom(String repository) {
        return repository.split("/")[1];
    }

    private String extractVendorNameFrom(String repository) {
        return repository.split("/")[1];
    }
}
