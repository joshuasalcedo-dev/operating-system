package io.joshuasalcedo.os.domain.ssh;


/**
 * Supported SSH key algorithms.
 */
public enum SshKeyAlgorithm {
    RSA("ssh-rsa", 4096),
    ED25519("ssh-ed25519", 256),
    ECDSA_256("ecdsa-sha2-nistp256", 256),
    ECDSA_384("ecdsa-sha2-nistp384", 384),
    ECDSA_521("ecdsa-sha2-nistp521", 521);

    private final String identifier;
    private final int defaultKeySize;

    SshKeyAlgorithm(String identifier, int defaultKeySize) {
        this.identifier = identifier;
        this.defaultKeySize = defaultKeySize;
    }

    public String identifier() { return identifier; }
    public int defaultKeySize() { return defaultKeySize; }

    public static SshKeyAlgorithm fromIdentifier(String identifier) {
        for (SshKeyAlgorithm algo : values()) {
            if (algo.identifier.equalsIgnoreCase(identifier)) return algo;
        }
        throw new SshException(SshException.Reason.INVALID_KEY_FORMAT);
    }
}