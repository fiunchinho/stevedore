package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.Project;
import stevedore.ProjectNotFoundException;
import stevedore.ProjectRepository;
import stevedore.infrastructure.ConnectionException;

public class DeleteProject {
    private final ProjectRepository projectRepository;
    private final IMessageBus messageBus;

    public DeleteProject(ProjectRepository projectRepository, IMessageBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void delete(String projectId) throws ProjectNotFoundException, ConnectionException {
        Project project = projectRepository
                .load(projectId)
                .orElseThrow(ProjectNotFoundException::new);

        project.delete();
        project.recordedEvents().forEach(messageBus::publish);
    }
}
