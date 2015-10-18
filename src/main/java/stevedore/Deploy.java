package stevedore;

import java.util.Date;

public class Deploy {
    private final Release release;
    private final Date createdAt;
    private DeployStatus.Status status;

    public Deploy(Release release) {
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
