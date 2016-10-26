package stevedore.infrastructure.read.projector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Invoke;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import stevedore.events.EnvironmentWasCreated;
import stevedore.events.ProjectWasCreated;
import stevedore.events.ReleaseWasTagged;
import stevedore.infrastructure.read.*;
import stevedore.messagebus.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.mongodb.client.model.Filters.eq;

public class MongoProjection {

//    private final MongoClient client;
    private final Datastore datastore;
    private final ObjectMapper mapper;
    private final ProjectDao projectDao;

    @Inject
    public MongoProjection(Datastore datastore, ObjectMapper objectMapper, ProjectDao projectDao) {
        this.projectDao = projectDao;
        mapper = objectMapper;
        mapper.setVisibility(FIELD, ANY);
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        mapper.getSerializerProvider().setNullKeySerializer(new NullKeySerializer());

        this.datastore = datastore;
    }

    @Handler(delivery = Invoke.Synchronously)
    @Subscribe
    public void handleProjectWasCreated(ProjectWasCreated message) {
        Project project = new Project(
                message.data().get("projectId"),
                message.data().get("projectName"),
                message.data().get("vendor"),
                message.data().get("repository")
        );
        datastore.save(project);
    }

    @Handler(delivery = Invoke.Synchronously)
    @Subscribe
    public void handleEnvironmentWasCreated(EnvironmentWasCreated message) {
        try {
            Project project = datastore
                    .createQuery(Project.class)
                    .field("id")
                    .equal(message.data().get("projectId"))
                    .get();

            project.addEnvironment(message.data().get("environmentId"), message.data().get("environmentName"), message.data().get("region"), message.data().get("vpcId"), message.data().get("keypair"), message.data().get("accessKey"), message.data().get("secretKey"));
            datastore.save(project);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Handler(delivery = Invoke.Synchronously)
    @Subscribe
    public void handleReleaseWasTagged(ReleaseWasTagged message) {
        try {
            System.out.println("PROYECTANDO LA RELEEEASE EN EL PROJECT: " + message.data().get("projectId") + " Y EL ENV: " + message.data().get("environmentId"));
//            Project project = datastore
//                    .createQuery(Project.class)
//                    .field("environments.id")
//                    .equal(message.data().get("environmentId"))
//                    .get();
//            Project project = datastore.find(Project.class, "environments.id", message.data().get("environmentId")).get();
//            Project project = datastore.createQuery(Project.class).filter("environments.id", message.data().get("environmentId")).get();
            Query<Project> projectQuery = datastore.createQuery(Project.class);
            projectQuery.and(projectQuery.criteria("environments.id").equal(message.data().get("environmentId")));

            Project project = projectDao.find(projectQuery).get();

//            Environment environment = project.getEnvironment(message.data().get("environmentId")).get();
            project
                    .getEnvironment(message.data().get("environmentId"))
                    .get()
                    .addRelease(message.data().get("version"))
            ;

            datastore.save(project);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getClass().toString());
            System.out.println(e.getCause());
            System.out.println(e.getStackTrace());
        }
    }
}
