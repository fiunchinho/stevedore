package stevedore;

public class AwsIdentity {
    private final String accessKey;
    private final String secretKey;

    public AwsIdentity(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String accessKey() {
        return accessKey;
    }

    public String secretKey() {
        return secretKey;
    }
}
