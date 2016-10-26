package stevedore.infrastructure;

import stevedore.Environment;
import stevedore.EnvironmentRepository;
import stevedore.messagebus.Message;

import java.util.List;

public class EventSourcingEnvironmentRepository implements EnvironmentRepository {

    private final EventStore eventStore;

    public EventSourcingEnvironmentRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void save(Environment environment) {
        eventStore.add(environment.id(), environment.recordedEvents());
    }

    @Override
    public Environment load(String environmentName) throws ConnectionException {
        List<Message> events = eventStore.load(environmentName);
        Environment environment = new Environment();
        events.stream().forEach(event -> environment.apply(event));

        return environment;
    }

}
