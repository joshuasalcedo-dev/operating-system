package io.joshuasalcedo.os.domain.ssh;

import java.time.Instant;

/**
 * Value object representing a generated SSH key pair (public + private key material).
 */
public record SshKeyPair(
        SshKeyAlgorithm algorithm,
        int keySize,
        SshPublicKey publicKey,
        String privateKeyPem,
        Instant generatedAt,
        String comment
) {
    public SshKeyPair {
        if (algorithm == null)
            throw new SshException(SshException.Reason.KEY_GENERATION_FAILED, "Algorithm must not be null");
        if (publicKey == null)
            throw new SshException(SshException.Reason.INVALID_KEY_FORMAT, "Public key must not be null");
        if (privateKeyPem == null)
            throw new SshException(SshException.Reason.PRIVATE_KEY_UNREADABLE, "Private key PEM must not be null");
        if (generatedAt == null)
            throw new SshException(SshException.Reason.KEY_GENERATION_FAILED, "GeneratedAt must not be null");
        comment = comment != null ? comment : "";
    }

    /** True if this key pair was generated with a passphrase-encrypted private key. */
    public boolean isEncrypted() {
        return privateKeyPem.contains("ENCRYPTED");
    }
}