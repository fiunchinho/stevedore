package stevedore.infrastructure.read.projector;

import net.engio.mbassy.listener.Handler;
import stevedore.events.*;
import stevedore.infrastructure.read.InMemoryProjectRepository;

public class InMemoryProjection {
    InMemoryProjectRepository projectRepository;

    public InMemoryProjection(InMemoryProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Handler
    public void handle(ProjectWasCreated message) {

//        projectRepository.save(message.project);
    }

    @Handler
    public void handle(DeployWasMade message) {
//        projectRepository.save(message.project);
    }

    @Handler
    public void handle(ReleaseWasPushed message) {
//        projectRepository.save(message.project);
    }
}
