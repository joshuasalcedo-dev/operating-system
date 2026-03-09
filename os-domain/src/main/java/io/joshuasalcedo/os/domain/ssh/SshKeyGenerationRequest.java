package io.joshuasalcedo.os.domain.ssh;

import java.nio.file.Path;

/**
 * Value object representing the parameters for generating a new SSH key pair.
 */
public record SshKeyGenerationRequest(
        SshKeyAlgorithm algorithm,
        int keySize,
        String comment,
        String passphrase,
        Path outputDirectory
) {
    public SshKeyGenerationRequest {
        if (algorithm == null)
            throw new SshException(SshException.Reason.KEY_GENERATION_FAILED, "Algorithm must not be null");
        if (keySize <= 0)
            throw SshException.weakKey(algorithm, keySize);
    }

    /** Use algorithm defaults for key size, no passphrase. */
    public static SshKeyGenerationRequest of(SshKeyAlgorithm algorithm, String comment) {
        return new SshKeyGenerationRequest(algorithm, algorithm.defaultKeySize(), comment, null, null);
    }

    /** ED25519 with a comment — the recommended default for new keys. */
    public static SshKeyGenerationRequest defaults(String comment) {
        return of(SshKeyAlgorithm.ED25519, comment);
    }

    public boolean hasPassphrase() {
        return passphrase != null && !passphrase.isBlank();
    }
}