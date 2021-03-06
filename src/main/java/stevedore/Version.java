package stevedore;

import java.util.Date;

public class Version {
    private String version;
    private Date createdAt;

    public Version() {
    }

    public Version(String version) {
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

    public boolean equalsTo(Version version) {
        return this.version == version.version;
    }
}
