package stevedore;

public class AwsIdentity {
    private String accessKey;
    private String secretKey;

    public AwsIdentity(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public AwsIdentity() {
    }

    public String accessKey() {
        return accessKey;
    }

    public String secretKey() {
        return secretKey;
    }
}
