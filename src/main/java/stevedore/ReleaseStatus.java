package stevedore;

public class ReleaseStatus {
    public enum Status {
        IN_PROGRESS("In Progress"),
        READY("Ready"),
        FAILED("Failed");

        Status(String status) {
        }
    }

    public static Status inProgress() {
        return Status.IN_PROGRESS;
    }

    public static Status ready() {
        return Status.READY;
    }

    public static Status failed() {
        return Status.FAILED;
    }
}
