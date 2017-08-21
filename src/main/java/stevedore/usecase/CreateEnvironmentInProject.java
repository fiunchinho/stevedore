package stevedore.usecase;

import com.google.common.eventbus.EventBus;
import stevedore.*;
import stevedore.infrastructure.ConnectionException;

public class CreateEnvironmentInProject {
    private final ProjectRepository projectRepository;
    private final EventBus messageBus;

    public CreateEnvironmentInProject(ProjectRepository projectRepository, EventBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void create(String projectId, String environmentName, String region, String vpcId, String keypair, String accessKey, String secretKey) throws ProjectNotFoundException, ConnectionException {
        Project project = projectRepository
                .load(projectId)
                .orElseThrow(ProjectNotFoundException::new);

        project.addEnvironment(environmentName, region, vpcId, keypair, new AwsIdentity(accessKey, secretKey));
        projectRepository.save(project);

        project.recordedEvents().forEach(messageBus::post);
    }

}
