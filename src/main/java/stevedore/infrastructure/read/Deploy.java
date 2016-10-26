package stevedore.infrastructure.read;

import stevedore.DeployStatus;
import stevedore.Release;

import java.util.Date;

public class Deploy {
    private stevedore.Release release;
    private Date createdAt;
    private DeployStatus.Status status;

    public Deploy() {
    }

    public Deploy(stevedore.Release release) {
        this.release = release;
        this.createdAt = new Date();
        this.status = DeployStatus.inProgress();
    }

    public Release release() {
        return release;
    }

    public Date createdAt() {
        return createdAt;
    }

    public DeployStatus.Status status() {
        return status;
    }

    public void setStatus(DeployStatus.Status status) {
        this.status = status;
    }
}
