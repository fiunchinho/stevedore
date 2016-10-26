package stevedore.infrastructure.read.projector;

import net.engio.mbassy.listener.Handler;
import stevedore.events.ProjectWasCreated;
import stevedore.messagebus.Message;

public class TestProjection {
    @Handler
    public void handle(Object message) {
        System.out.println("IS THIS REAL LIFE");
    }

    @Handler
    public void handleProjectWasCreated(ProjectWasCreated message) {
        System.out.println("WATAFAAAAAACK EN EL TEST PROJECTION TRONCOOOO");
    }
}
