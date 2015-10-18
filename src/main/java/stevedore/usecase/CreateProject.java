package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.Project;
import stevedore.ProjectRepository;

public class CreateProject {
    private final ProjectRepository projectRepository;
    private final IMessageBus messageBus;

    public CreateProject(ProjectRepository projectRepository, IMessageBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void create(String projectRepository) {
        Project project = Project.create(projectRepository);
        project.recordedEvents().forEach(messageBus::publish);
    }
}
