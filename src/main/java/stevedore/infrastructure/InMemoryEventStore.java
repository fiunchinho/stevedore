package stevedore.infrastructure;

import stevedore.messagebus.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InMemoryEventStore implements EventStore{
    private final HashMap<String, ArrayList<Message>> events = new HashMap<>();

    public InMemoryEventStore() {
    }

    @Override
    public void add(String id, List<Message> events) {
        if (!this.events.containsKey(id)) {
            this.events.put(id, new ArrayList<>());
        }

        this.events.get(id).addAll(events);
    }

    @Override
    public void add(UUID id, Message event) {
        this.events.get(id).add(event);
    }

    @Override
    public ArrayList<Message> load(String id) throws ConnectionException {
        if (!events.containsKey(id)) {
            throw new RuntimeException();
        }

        return events.get(id);
    }
}
