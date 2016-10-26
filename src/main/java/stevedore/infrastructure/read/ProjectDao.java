package stevedore.infrastructure.read;

import com.google.inject.Inject;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;

public class ProjectDao extends BasicDAO<Project, ObjectId> {
    @Inject
    public ProjectDao(Morphia morphia, MongoClient mongo) {
        super(mongo, morphia, "stevedore");
    }
}
