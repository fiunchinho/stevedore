package stevedore.infrastructure.read;

import stevedore.ReleaseStatus;
import stevedore.Version;

import java.util.Date;

public class Release {
    private Version version;
    private Date createdAt;
    private ReleaseStatus.Status status;

    public Release(Version version) {
        System.out.println("CREATING VERSION: " + version.toString());
        this.version = version;
        this.createdAt = new Date();
        this.status = ReleaseStatus.inProgress();
    }

    public Release() {
        this.createdAt = new Date();
        this.status = ReleaseStatus.inProgress();
    }

    public Version version() {
        return version;
    }

    public Date createdAt() {
        return createdAt;
    }

    public ReleaseStatus.Status status() {
        return status;
    }

    public void setStatus(ReleaseStatus.Status status) {
        this.status = status;
    }

    public void isFailed() {
        this.status = ReleaseStatus.failed();
    }

    public boolean equals(String version) {
        return this.version.version() == version;
    }

    public boolean equals(Version version) {
        return this.version == version;
    }

    public boolean equals(Release release) {
        return release.version == version;
    }
}
