package stevedore.infrastructure;

import stevedore.Project;
import stevedore.ProjectRepository;
import stevedore.messagebus.Message;

import java.util.List;

public class InMemoryEventSourcingProjectRepository implements ProjectRepository {

    private final EventStore eventStore;

    public InMemoryEventSourcingProjectRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void save(Project project) {
        eventStore.add(project.name(), project.recordedEvents());
    }

    @Override
    public Project load(String projectName) {
        List<Message> events = eventStore.load(projectName);
        Project project = new Project();
        events.stream().forEach(event -> project.apply(event));

        return project;
    }

}
