package stevedore.infrastructure;

import stevedore.messagebus.Message;

import java.util.ArrayList;
import java.util.List;

public interface EventStore {
    void add(String id, List events);

    void add(String id, Message event);

    ArrayList<Message> load(String id);
}
