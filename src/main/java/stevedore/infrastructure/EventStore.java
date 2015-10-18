package stevedore.infrastructure;

import stevedore.messagebus.Message;

import java.util.List;

public interface EventStore {
    void add(String id, List events);

    void add(String id, Message event);

    List<Message> load(String id);
}
