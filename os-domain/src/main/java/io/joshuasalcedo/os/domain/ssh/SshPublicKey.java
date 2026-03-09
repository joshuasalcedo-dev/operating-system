package io.joshuasalcedo.os.domain.ssh;

/**
 * Value object representing an SSH public key.
 * Corresponds to a single line in an authorized_keys file.
 */
public record SshPublicKey(
        SshKeyAlgorithm algorithm,
        String base64Key,
        String comment
) {
    public SshPublicKey {
        if (algorithm == null)
            throw new SshException(SshException.Reason.INVALID_KEY_FORMAT, "Algorithm must not be null");
        if (base64Key == null || base64Key.isBlank())
            throw new SshException(SshException.Reason.INVALID_KEY_FORMAT, "Base64 key must not be blank");
        comment = comment != null ? comment : "";
    }

    /**
     * Parses a single authorized_keys line.
     * Format: {@code algorithm base64key [comment]}
     */
    public static SshPublicKey parse(String authorizedKeysLine) {
        if (authorizedKeysLine == null)
            throw new SshException(SshException.Reason.INVALID_KEY_FORMAT, "Line must not be null");
        String trimmed = authorizedKeysLine.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#"))
            throw new SshException(SshException.Reason.INVALID_KEY_FORMAT,
                    "Not a valid authorized_keys line: " + trimmed);

        String[] parts = trimmed.split(" ", 3);
        if (parts.length < 2)
            throw new SshException(SshException.Reason.INVALID_KEY_FORMAT,
                    "Invalid authorized_keys line: " + trimmed);

        return new SshPublicKey(
                SshKeyAlgorithm.fromIdentifier(parts[0]),
                parts[1],
                parts.length == 3 ? parts[2] : ""
        );
    }

    /** Formats back to a valid authorized_keys line. */
    public String toAuthorizedKeysLine() {
        return comment.isBlank()
                ? algorithm.identifier() + " " + base64Key
                : algorithm.identifier() + " " + base64Key + " " + comment;
    }

    /** Short preview of the key for display (first 16 chars of the base64 blob). */
    public String fingerprint() {
        return algorithm.identifier() + " [" +
               base64Key.substring(0, Math.min(16, base64Key.length())) + "...]" +
               (comment.isBlank() ? "" : " " + comment);
    }

    @Override
    public String toString() {
        return fingerprint();
    }
}