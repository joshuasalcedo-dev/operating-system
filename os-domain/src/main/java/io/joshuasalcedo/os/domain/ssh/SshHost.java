package io.joshuasalcedo.os.domain.ssh;

/**
 * Value object representing an SSH remote host target.
 */
public record SshHost(
        String hostname,
        String username
) {
    public static SshHost of(String hostname, String username) {
        return new SshHost(hostname, username);
    }

    @Override
    public String toString() {
        return username + "@" + hostname;
    }
}