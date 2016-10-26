package stevedore.usecase;

import com.google.common.eventbus.EventBus;
import stevedore.Project;
import stevedore.ProjectRepository;

public class CreateProject {
    private final ProjectRepository projectRepository;
    private final EventBus messageBus;

    public CreateProject(ProjectRepository projectRepository, EventBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void create(String repositoryName) {
        Project project = Project.create(repositoryName);
        projectRepository.save(project);
        project.recordedEvents().forEach(System.out::println);
        project.recordedEvents().forEach(messageBus::post);
    }
}
