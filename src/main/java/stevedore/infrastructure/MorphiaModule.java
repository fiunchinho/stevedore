package stevedore.infrastructure;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import stevedore.infrastructure.read.EnvironmentCodec;
import stevedore.infrastructure.read.ProjectCodec;
import stevedore.infrastructure.read.projector.MongoProjection;

public class MorphiaModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    Datastore providePubSubSupport(MongoClient client) {
        final Morphia morphia = new Morphia();

        morphia.mapPackage("stevedore.infrastructure.read");

        final Datastore datastore = morphia.createDatastore(client, "stevedore");
        datastore.ensureIndexes();

        return datastore;
    }

//    MongoClient getMongoClient() {
//        CodecRegistry codecRegistry =
//                CodecRegistries.fromRegistries(
//                        MongoClient.getDefaultCodecRegistry(),
//                        CodecRegistries.fromCodecs(new ProjectCodec(), new EnvironmentCodec())
//                );
//
//        MongoClientOptions options = MongoClientOptions.builder()
//                .codecRegistry(codecRegistry).build();
//
//        return new MongoClient("192.168.99.100:27017", options);
//    }
}
