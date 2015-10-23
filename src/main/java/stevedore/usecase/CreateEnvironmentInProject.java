package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.*;

import java.util.Optional;

public class CreateEnvironmentInProject {
    private final ProjectRepository projectRepository;
    private final IMessageBus messageBus;

    public CreateEnvironmentInProject(ProjectRepository projectRepository, IMessageBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void create(String projectId, String environmentName, String region, String vpcId, String keypair, String accessKey, String secretKey) throws ProjectNotFoundException {
        Project project = projectRepository
                .load(projectId)
                .orElseThrow(() -> new ProjectNotFoundException());

        project.addEnvironment(environmentName, region, vpcId, keypair, new AwsIdentity(accessKey, secretKey));

        project.recordedEvents().forEach(messageBus::publish);
    }

}
