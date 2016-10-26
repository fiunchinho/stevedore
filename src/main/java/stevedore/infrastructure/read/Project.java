package stevedore.infrastructure.read;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import stevedore.AwsIdentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Entity("projects")
@Indexes(
        @Index(value = "id", fields = @Field("id"))
)
public class Project {
    @Id
    private ObjectId _id;
    private String id;
    private Integer version = 0;
    private String name;
    private String repository;
    private ArrayList<Environment> environments = new ArrayList<>();
    private String vendor;

    public Project(String projectId, String projectName, String vendor, String repository) {
        this.id = projectId;
        this.name = projectName;
        this.vendor = vendor;
        this.repository = repository;
    }

    public Project() {
    }

    public String getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getRepository() {
        return repository;
    }

    public ArrayList getEnvironments() {
        return environments;
    }

    public Optional<Environment> getEnvironment(String environmentId) {
        for (Environment environment : environments) {
            if (environment.getId().equals(environmentId)) {
                return Optional.of(environment);
            }
        }

        return Optional.empty();
    }

    public void addEnvironment(String id, String name, String region, String vpcId, String keypair, String accessKey, String secretKey) {
        environments.add(new Environment(id, name, region, vpcId, keypair, new AwsIdentity(accessKey, secretKey)));
    }

    public String getVendor() {
        return vendor;
    }
}
