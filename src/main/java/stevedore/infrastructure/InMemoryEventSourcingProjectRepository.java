package stevedore.infrastructure;

import stevedore.Project;
import stevedore.ProjectRepository;
import stevedore.messagebus.Message;

import java.util.List;
import java.util.Optional;

public class InMemoryEventSourcingProjectRepository implements ProjectRepository {

    private final EventStore eventStore;

    public InMemoryEventSourcingProjectRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void save(Project project) {
        eventStore.add(project.id(), project.recordedEvents());
    }

    @Override
    public Optional<Project> load(String projectId) {
        List<Message> events = eventStore.load(projectId);
        Project project = new Project();
        events.stream().forEach(event -> project.apply(event));

        return Optional.of(project);
    }

}
