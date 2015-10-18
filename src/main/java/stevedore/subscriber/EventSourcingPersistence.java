package stevedore.subscriber;

import net.engio.mbassy.listener.Handler;
import stevedore.EnvironmentRepository;
import stevedore.ProjectRepository;
import stevedore.events.*;
import stevedore.infrastructure.EventStore;
import stevedore.messagebus.Message;

import java.util.ArrayList;


public class EventSourcingPersistence {
    private final EventStore eventStore;
    private final ProjectRepository projectRepository;
    private final EnvironmentRepository environmentRepository;

    public EventSourcingPersistence(EventStore eventStore, ProjectRepository projectRepository, EnvironmentRepository environmentRepository) {
        this.eventStore = eventStore;
        this.projectRepository = projectRepository;
        this.environmentRepository = environmentRepository;
    }

    @Handler
    public void handle(Message message) {
        eventStore.add(message.getId(), message);
    }
}
