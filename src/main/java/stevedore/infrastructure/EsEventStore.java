package stevedore.infrastructure;

import akka.actor.*;
import akka.util.Timeout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import eventstore.*;
import eventstore.j.*;
import eventstore.tcp.ConnectionActor;
import scala.concurrent.Await;
import scala.concurrent.AwaitPermission;
import scala.concurrent.CanAwait;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import stevedore.events.*;
import stevedore.messagebus.Message;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    public void add(String id, List<Message> events) {
        final ActorSystem system = ActorSystem.create();
        final Settings settings = getSettings();
        final eventstore.j.EsConnection connection2 = EsConnectionFactory.create(system, settings);

        ArrayList<EventData> eventStoreEvents = new ArrayList<>();
        events.forEach(message -> eventStoreEvents.add(
                new EventDataBuilder(message.getClass().getName())
                        .eventId(message.getId())
                        .data(serializer.toJson(message))
                        .metadata("my first event")
                        .build()
                )
        );

        try {
            Await.result(
                    connection2.writeEvents(id.toString(), null, eventStoreEvents, null),
                    new Timeout(Duration.create(5, "seconds")).duration()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(UUID id, Message message) {
        System.out.println("Introduciendo evento en el stream: " + id.toString());


        final EventData event = new EventDataBuilder(message.getClass().getName())
                .eventId(message.getId())
                .data(serializer.toJson(message))
                .metadata("my first event")
                .build();

        ArrayList<EventData> events = new ArrayList<>();
        events.add(event);
        final ActorSystem system = ActorSystem.create();
        final Settings settings = getSettings();
        final eventstore.j.EsConnection connection2 = EsConnectionFactory.create(system, settings);

        try {
            Await.result(
                    connection2.writeEvents(id.toString(), null, events, null),
                    new Timeout(Duration.create(5, "seconds")).duration()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }


//        final ActorSystem system = ActorSystem.create();
//        final Settings settings = getSettings();
//        final ActorRef connection = system.actorOf(ConnectionActor.getProps(settings));
//
//        final EventData event = new EventDataBuilder(message.getClass().getName())
//                .eventId(message.getId())
//                .data(serializer.toJson(message))
//                .metadata("my first event")
//                .build();
//
//        final WriteEvents writeEvents = new WriteEventsBuilder(id.toString())
//                .addEvent(event)
//                .expectAnyVersion()
//                .build();
//
//        connection.tell(writeEvents, null);
    }

    @Override
    public ArrayList<Message> load(String id) throws ConnectionException {
        ArrayList<Message> events = new ArrayList<>();
        final ActorSystem system = ActorSystem.create();
        final Settings settings = getSettings();
        final eventstore.j.EsConnection connection2 = EsConnectionFactory.create(system, settings);
        try {
            Await.result(
                    connection2.readStreamEventsForward(id, null, 10, false, null),
                    new Timeout(Duration.create(5, "seconds")).duration()
            ).eventsJava().forEach(event -> {
//                Class clazz = null;
//                try {
//                    clazz = Class.forName(event.data().eventType());
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
                String msg = event.data().data().value().decodeString(Charset.defaultCharset().toString());
                if (event.data().eventType().equals("stevedore.events.ProjectWasCreated")) {
                    System.out.println("REPLAYEANDO PROJECTWAS CREAEETED");
                    events.addAll(Arrays.asList(serializer.fromJson(msg, ProjectWasCreated.class)));
                } else if (event.data().eventType().equals("stevedore.events.EnvironmentWasCreated")) {
                    System.out.println("REPLAYEANDO ADDING ENVIRONMENEENTNENN");
                    events.addAll(Arrays.asList(serializer.fromJson(msg, EnvironmentWasCreated.class)));
                } else if (event.data().eventType().equals("stevedore.events.ReleaseWasTagged")) {
                    System.out.println("REPLAYEANDO TAGGING RELEASE");
                    events.addAll(Arrays.asList(serializer.fromJson(msg, ReleaseWasTagged.class)));
                } else if (event.data().eventType().equals("stevedore.events.ReleaseWasPushed")) {
                    System.out.println("REPLAYEANDO PUSHING RELEASE");
                    events.addAll(Arrays.asList(serializer.fromJson(msg, ReleaseWasPushed.class)));
                } else if (event.data().eventType().equals("stevedore.events.DeployWasStarted")) {
                    System.out.println("REPLAYEANDO STARTING DEPLOY");
                    events.addAll(Arrays.asList(serializer.fromJson(msg, DeployWasStarted.class)));
                } else if (event.data().eventType().equals("stevedore.events.DeployWasMade")) {
                    System.out.println("REPLAYEANDO DEPLOY MADE");
                    events.addAll(Arrays.asList(serializer.fromJson(msg, DeployWasMade.class)));
                }

//                events.addAll(new ArrayList<>(Arrays.asList(evnts)));
//                events.add((Message) serializer.fromJson(msg, clazz));
            });
        } catch (Exception e) {
            throw new ConnectionException(e);
        }

        return events;
    }

    private Settings getSettings() {
        return new SettingsBuilder()
                .address(new InetSocketAddress(EVENT_STORE_HOST, EVENT_STORE_PORT))
                .defaultCredentials(EVENT_STORE_USER, EVENT_STORE_PASS)
                .build();
    }
}
