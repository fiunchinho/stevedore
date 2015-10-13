package stevedore;

import java.util.Date;

public class Release {
    private final Version version;
    private final Date createdAt;
    private ReleaseStatus.Status status;

    public Release(Version version) {
        this.version = version;
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

    public void isReady() {
        this.status = ReleaseStatus.ready();
    }

    public void isFailed() {
        this.status = ReleaseStatus.failed();
    }

    public boolean equalsTo(String version) {
        return this.version.version() == version;
    }

    public boolean equalsTo(Version version) {
        return this.version == version;
    }

    public boolean equalsTo(Release release) {
        return release.version == version;
    }
}
