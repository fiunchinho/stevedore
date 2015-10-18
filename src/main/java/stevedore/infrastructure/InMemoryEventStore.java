package stevedore.infrastructure;

import stevedore.messagebus.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryEventStore<E> implements EventStore{
    private final HashMap<String, ArrayList<Message>> events = new HashMap<>();

    public InMemoryEventStore() {
    }

    @Override
    public void add(String id, List events) {
        if (!this.events.containsKey(id)) {
            this.events.put(id, new ArrayList<>());
        }

        this.events.get(id).addAll(events);
    }

    @Override
    public void add(String id, Message event) {
        this.events.get(id).add(event);
    }

    @Override
    public ArrayList<Message> load(String id) {
        if (!events.containsKey(id)) {
            throw new RuntimeException();
        }

        return events.get(id);
    }
}
