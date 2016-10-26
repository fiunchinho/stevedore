package stevedore.infrastructure.read;

//import com.mongodb.BasicDBObject;
//import com.mongodb.MongoClient;
//import com.mongodb.client.FindIterable;
//import com.mongodb.client.MongoDatabase;
//import org.bson.Document;
//import org.bson.types.ObjectId;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import stevedore.Project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class MongoProjectRepository {

    private final MongoClient client;
    private final ObjectMapper mapper;

    public MongoProjectRepository() {
        CodecRegistry codecRegistry =
                CodecRegistries.fromRegistries(
                        MongoClient.getDefaultCodecRegistry(),
                        CodecRegistries.fromCodecs(new ProjectCodec())
                );

        MongoClientOptions options = MongoClientOptions.builder()
                .codecRegistry(codecRegistry).build();
        client = new MongoClient("192.168.99.100:27017", options);
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        client = new MongoClient();
//        client = MongoClients.create(MongoClientSettings.builder(MongoClients.create().getSettings()).codecRegistry(codecRegistry).build());
    }

    public Optional<stevedore.infrastructure.read.Project> load(String projectId) {

//        MongoClient mongoClient = new MongoClient();
//        MongoDatabase db = mongoClient.getDatabase("stevedore");
//        FindIterable<Document> project = db.getCollection("restaurants").find(eq("_id", new ObjectId("560ea3f205240f065a3e9d19")));
//        project.first().get()

        Document projectJson = client
//        Project projectJson = client
                .getDatabase("stevedore")
                .getCollection("projects")
//                .find(eq("name", projectId), Project.class)
                .find(eq("id", projectId))
                .first();
        try {
            return Optional.of(mapper.readValue(projectJson.toJson(), stevedore.infrastructure.read.Project.class));
//            return Optional.of(projectJson);
//            return Optional.of(mapper.readValue(projectJson.toJson(), Project.class));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
//        mongoClient
//                .getDatabase("stevedore")
//                .getCollection("projects")
//                .find(eq("_id", new ObjectId("560ea3f205240f065a3e9d19")))
//                .first((projectDocument, throwable) -> {
//                    project = Optional.of(mapper.readValue(projectDocument.toJson(), Project.class));
//                });
    }

    public ArrayList<stevedore.infrastructure.read.Project> findAll() {
        ArrayList<stevedore.infrastructure.read.Project> projects = new ArrayList<>();
        client
                .getDatabase("stevedore")
                .getCollection("projects")
                .find().forEach((Block<Document>) document -> {
            try {
                projects.add(
                        mapper.readValue(document.toJson(), stevedore.infrastructure.read.Project.class)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return projects;
    }
}
