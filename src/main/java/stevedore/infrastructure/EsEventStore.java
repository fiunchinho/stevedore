package stevedore.infrastructure;

import akka.actor.*;
import akka.actor.Status.Failure;
import akka.util.Timeout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import eventstore.*;
import eventstore.j.*;
import eventstore.tcp.ConnectionActor;
import scala.Function1;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import stevedore.events.EnvironmentWasCreated;
import stevedore.events.ProjectWasCreated;
import stevedore.messagebus.Message;

import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class EsEventStore implements EventStore{
    public static final String EVENT_STORE_HOST = "192.168.99.100";
    public static final int EVENT_STORE_PORT = 1113;
    public static final String EVENT_STORE_USER = "admin";
    public static final String EVENT_STORE_PASS = "changeit";

    private final Gson serializer;

    public EsEventStore() {
        serializer = new GsonBuilder()
//                .excludeFieldsWithoutExposeAnnotation()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }

    @Inject
    public EsEventStore(Gson serializer) {
        this.serializer = serializer;
    }

    @Override
    public void add(String id, List events) {

    }

    @Override
    public void add(String id, Message message) {
        final ActorSystem system = ActorSystem.create();
        final Settings settings = getSettings();
        final ActorRef connection = system.actorOf(ConnectionActor.getProps(settings));

        final EventData event = new EventDataBuilder(message.getClass().getName())
                .eventId(UUID.randomUUID())
                .data(serializer.toJson(message))
                .metadata("my first event")
                .build();

        final WriteEvents writeEvents = new WriteEventsBuilder(message.getId())
                .addEvent(event)
                .expectAnyVersion()
                .build();

        connection.tell(writeEvents, null);
    }

    @Override
    public ArrayList<Message> load(String id) {
        ArrayList<Message> events = new ArrayList<>();
        final ActorSystem system = ActorSystem.create();
        final Settings settings = getSettings();
//        final ActorRef connection = system.actorOf(ConnectionActor.getProps(settings));
//        final ActorRef readResult = system.actorOf(Props.create(ReadResult.class));
//
//        final ReadEvent readEvent = new ReadEventBuilder(id)
//                .first()
//                .resolveLinkTos(false)
//                .requireMaster(true)
//                .build();
//
//        connection.tell(readEvent, readResult);
        final eventstore.j.EsConnection connection2 = EsConnectionFactory.create(system, settings);
        try {
//            String singleEvent = Await.result(
//                    connection2.readEvent("my-project", new EventNumber.Exact(0), false, null),
//                    new Timeout(Duration.create(5, "seconds")).duration()
//            )
//                    .data()
//                    .data()
//                    .value()
//                    .decodeString(Charset.defaultCharset().toString());
//            System.out.println("SINGLEEEE");
//            System.out.println(singleEvent);

            Await.result(
//                    connection2.readAllEventsForward(new Position.Exact(0L, 0L), 10, false, null),

                    connection2.readStreamEventsForward("my-project", new EventNumber.Exact(0), 10, false, null),
                    new Timeout(Duration.create(5, "seconds")).duration()
            ).eventsJava().forEach(event -> {
                Class clazz = null;
                try {
                    clazz = Class.forName("stevedore.events." + event.data().eventType());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                String msg = event.data().data().value().decodeString(Charset.defaultCharset().toString());
//                System.out.println("FUERAAAA +++++++++");
//                System.out.println(msg);
//                System.out.println(event.data().eventType());
                if (event.data().eventType().equals("ProjectWasCreated")) {
//                    ProjectWasCreated[] evnts = serializer.fromJson(msg, ProjectWasCreated[].class);
//                    System.out.println(Arrays.toString(evnts));
                    events.addAll(Arrays.asList(serializer.fromJson(msg, ProjectWasCreated[].class)));
                } else if (event.data().eventType().equals("EnvironmentWasCreated")) {
//                    EnvironmentWasCreated[] evnts = serializer.fromJson(msg, EnvironmentWasCreated[].class);
//                    System.out.println(Arrays.toString(evnts));
                    events.addAll(Arrays.asList(serializer.fromJson(msg, EnvironmentWasCreated[].class)));
                }

//                events.addAll(new ArrayList<>(Arrays.asList(evnts)));
//                events.add((Message) serializer.fromJson(msg, clazz));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
//        connection2.readStreamEventsForward("my-project", new EventNumber.Exact(0), 10, false, null)
//                .onComplete()
//                .value().g
//                .get()
//                .get()
//                .eventsJava().forEach(event -> {
//            Class clazz = null;
//            try {
//                clazz = Class.forName("stevedore.events." + event.data().eventType());
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//            String msg = event.data().data().value().decodeString(Charset.defaultCharset().toString());
//            System.out.println("FUERAAAA +++++++++");
//            System.out.println(msg);
//            events.add((Message) serializer.fromJson(msg, clazz));
//        });
//        ReadResult.events.forEach(event -> {
//            try {
////                Type eventType = Class.forName("stevedore.events." + event.data().eventType()).getComponentType();
//                Class clazz = Class.forName("stevedore.events." + event.data().eventType());
//                String msg = event.data().data().value().decodeString(Charset.defaultCharset().toString());
//                System.out.println("FUERAAAA +++++++++");
//                System.out.println(msg);
//                events.add((Message) serializer.fromJson(msg, clazz));
//            } catch (Exception e) {
//                e.printStackTrace();
////                System.out.println("ADIOSSSSS+++++++++++++++++++++++++++++++++++");
////                System.out.println(event.data().data().value().toString());
//            }
//        });

        return events;
    }

    private Settings getSettings() {
        return new SettingsBuilder()
                .address(new InetSocketAddress(EVENT_STORE_HOST, EVENT_STORE_PORT))
                .defaultCredentials(EVENT_STORE_USER, EVENT_STORE_PASS)
                .build();
    }

    public static class ReadResult extends UntypedActor {
        public static final List<EventRecord> events = new ArrayList<>();

        public void onReceive(Object message) throws Exception {
            if (message instanceof ReadEventCompleted) {
                final ReadEventCompleted completed = (ReadEventCompleted) message;
                final Event event = completed.event();
                String msg = event.data().data().value().decodeString(Charset.defaultCharset().toString());
                System.out.println("DENTROOOOOOOOOO +++++++++");
                System.out.println(msg);
                events.add(event.record());
            } else if (message instanceof Failure) {
                final Failure failure = ((Failure) message);
                final EsException exception = (EsException) failure.cause();
                throw new RuntimeException(exception);
            } else
                unhandled(message);

            context().system().shutdown();
        }
    }
}
