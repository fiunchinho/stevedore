package stevedore;

public class DeployStatus {
    public enum Status {
        IN_PROGRESS("In Progress"),
        SUCCESSFUL("Successful"),
        FAILED("Failed");

        Status(String status) {
        }
    }

    public static Status inProgress() {
        return Status.IN_PROGRESS;
    }

    public static Status successful() {
        return Status.SUCCESSFUL;
    }

    public static Status failed() {
        return Status.FAILED;
    }
}
