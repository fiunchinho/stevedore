package stevedore.infrastructure;

import stevedore.messagebus.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface EventStore {
    void add(String id, List<Message> events);

    void add(UUID id, Message event);

    ArrayList<Message> load(String id) throws ConnectionException;
}
