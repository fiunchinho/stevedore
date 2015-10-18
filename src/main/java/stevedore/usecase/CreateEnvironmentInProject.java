package stevedore.usecase;

import net.engio.mbassy.bus.common.IMessageBus;
import stevedore.*;

public class CreateEnvironmentInProject {
    private final ProjectRepository projectRepository;
    private final IMessageBus messageBus;

    public CreateEnvironmentInProject(ProjectRepository projectRepository, IMessageBus messageBus) {
        this.projectRepository = projectRepository;
        this.messageBus = messageBus;
    }

    public void create(String projectName, String environmentName, String region, String vpcId, String keypair, String accessKey, String secretKey) {
        Project project = projectRepository.load(projectName);
        project.addEnvironment(new Environment(environmentName, region, vpcId, keypair, new AwsIdentity(accessKey, secretKey)));
        project.recordedEvents().forEach(messageBus::publish);
    }

}
