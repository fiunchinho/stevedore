package stevedore;

import java.util.Date;

public class Deploy {
    private final Release release;
    private final Date createdAt;

    public Deploy(Release release) {
        this.release = release;
        this.createdAt = new Date();
    }

    public Release release() {
        return release;
    }

    public Date createdAt() {
        return createdAt;
    }
}
