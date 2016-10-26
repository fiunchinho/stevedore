package stevedore.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.SyncMessageBus;
import net.engio.mbassy.bus.common.PubSubSupport;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import stevedore.infrastructure.read.projector.MongoProjection;
import stevedore.messagebus.Message;

public class MessageBusModule extends AbstractModule {
//    PubSubSupport<Message> bus;

    @Override
    protected void configure() {
    }

//    @Provides
//    @Singleton
//    PubSubSupport providePubSubSupport() {
//        Boolean useAsync = false;
//        IBusConfiguration config = new BusConfiguration()
//                .addFeature(Feature.SyncPubSub.Default())
//                .addFeature(Feature.AsynchronousHandlerInvocation.Default())
//                .addFeature(Feature.AsynchronousMessageDispatch.Default())
//                .addPublicationErrorHandler(new IPublicationErrorHandler.ConsoleLogger(true));
//
//        bus = (useAsync) ? new MBassador<>(config) : new SyncMessageBus<>(config);
//        bus.subscribe(new MongoProjection());
//
//        return bus;
//    }

    @Provides
    @Singleton
    EventBus providePubSubSupport(Datastore datastore, ObjectMapper mapper, Injector injector) {
        EventBus bus = new EventBus();
        bus.register(injector.getInstance(MongoProjection.class));

        return bus;
    }

//    @Provides
//    @Singleton
//    Datastore provideDatastore() {
//        final Morphia morphia = new Morphia();
//
//        morphia.mapPackage("stevedore.infrastructure.read");
//
//        final Datastore datastore = morphia.createDatastore(new MongoClient(), "stevedore");
//        datastore.ensureIndexes();
//
//        return datastore;
//    }
}
