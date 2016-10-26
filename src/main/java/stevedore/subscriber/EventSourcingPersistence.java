package stevedore.subscriber;

import net.engio.mbassy.listener.Handler;
import stevedore.infrastructure.EventStore;
import stevedore.messagebus.Message;


public class EventSourcingPersistence {
    private final EventStore eventStore;

    public EventSourcingPersistence(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Handler
    public void handle(Message message) {
        eventStore.add(message.getId(), message);
    }
}
