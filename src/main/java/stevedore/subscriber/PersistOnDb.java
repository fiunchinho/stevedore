package stevedore.subscriber;

import net.engio.mbassy.listener.Handler;
import stevedore.ProjectRepository;
import stevedore.events.DeployWasMade;
import stevedore.events.DeployWasStarted;
import stevedore.events.ReleaseWasPushed;
import stevedore.events.ReleaseWasTagged;

public class PersistOnDb {
    private final ProjectRepository projectRepository;

    public PersistOnDb(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Handler
    public void handle(DeployWasStarted message) {
//        projectRepository.save(message.project);
    }

    @Handler
    public void handle(DeployWasMade message) {
//        projectRepository.save(message.project);
    }

    @Handler
    public void handle(ReleaseWasTagged message) {
//        projectRepository.save(message.project);
    }

    @Handler
    public void handle(ReleaseWasPushed message) {
//        projectRepository.save(message.project);
    }
}
