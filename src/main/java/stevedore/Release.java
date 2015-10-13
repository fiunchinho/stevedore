package stevedore;

import java.util.Date;

public class Release {
    private final String version;
    private final Date createdAt;

    public Release(String version) {
        this.version = version;
        this.createdAt = new Date();
    }

    public String version() {
        return version;
    }

    public Date createdAt() {
        return createdAt;
    }

    public boolean equalsTo(String version) {
        return this.version == version;
    }

    public boolean equalsTo(Release release) {
        return release.version == version;
    }
}
