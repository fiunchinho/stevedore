package stevedore.subscriber;

import net.engio.mbassy.listener.Handler;
import stevedore.Deploy;
import stevedore.DeployStatus;
import stevedore.Deployer;
import stevedore.events.DeployWasMade;

public class ExecuteDeployOnServer {
    private final Deployer deployer;

    public ExecuteDeployOnServer(Deployer deployer) {
        this.deployer = deployer;
    }

    @Handler
    public void handle(DeployWasMade message) {
        try {
//            deployer.deploy(message.project, message.environment, message.version);
        } catch (Exception e) {
//            message.deploy.setStatus(DeployStatus.failed());
//            ArrayList<Deploy> deploys = message.environment.deploys();
//            Deploy currentDeploy = deploys.stream().filter(startDeploy -> startDeploy.release().version() == message.version).findFirst().get();
//            currentDeploy.isFailed();
        }
    }
}
