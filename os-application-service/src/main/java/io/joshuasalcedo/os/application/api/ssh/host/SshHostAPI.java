package io.joshuasalcedo.os.application.api.ssh.host;

import io.joshuasalcedo.os.application.api.ssh.client.SshClientAPI;
import io.joshuasalcedo.os.domain.ssh.SshAuthorizedKeys;
import io.joshuasalcedo.os.domain.ssh.SshException;
import io.joshuasalcedo.os.domain.ssh.SshHost;
import io.joshuasalcedo.os.domain.ssh.SshPublicKey;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing SSH access on a remote host —
 * reading and writing authorized_keys, checking key presence, etc.
 */
public interface SshHostAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Reads the authorized_keys file from the remote host. */
    SshAuthorizedKeys readAuthorizedKeys(SshHost host);

    /** Writes the given authorized_keys to the remote host, replacing the existing file. */
    void writeAuthorizedKeys(SshHost host, SshAuthorizedKeys authorizedKeys);

    // ── Key operations ────────────────────────────────────────────────────────

    /** Appends a public key to the remote host's authorized_keys. */
    default void addKey(SshHost host, SshPublicKey key) {
        SshAuthorizedKeys current = readAuthorizedKeys(host);
        if (current.contains(key))
            throw SshException.keyAlreadyAuthorized(key);
        writeAuthorizedKeys(host, current.withKey(key));
    }

    /** Removes a public key from the remote host's authorized_keys. */
    default void removeKey(SshHost host, SshPublicKey key) {
        SshAuthorizedKeys current = readAuthorizedKeys(host);
        if (!current.contains(key))
            throw SshException.keyNotFound(key);
        writeAuthorizedKeys(host, current.withoutKey(key));
    }

    /** Removes all keys matching the given comment. */
    default void revokeByComment(SshHost host, String comment) {
        writeAuthorizedKeys(host, readAuthorizedKeys(host).withoutComment(comment));
    }

    /** Atomically replaces an old key with a new one (key rotation). */
    default void rotateKey(SshHost host, SshPublicKey oldKey, SshPublicKey newKey) {
        SshAuthorizedKeys current = readAuthorizedKeys(host);
        if (!current.contains(oldKey))
            throw SshException.keyNotFound(oldKey);
        if (current.contains(newKey))
            throw SshException.keyAlreadyAuthorized(newKey);
        writeAuthorizedKeys(host, current.withoutKey(oldKey).withKey(newKey));
    }

    /** Removes all keys and replaces with only the given key (emergency lockout recovery). */
    default void replaceAllKeys(SshHost host, SshPublicKey key) {
        writeAuthorizedKeys(host, SshAuthorizedKeys.of(List.of(key)));
    }

    /** Removes all authorized keys — locks out all key-based access. */
    default void revokeAllKeys(SshHost host) {
        writeAuthorizedKeys(host, SshAuthorizedKeys.empty());
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    /** True if the given key is already authorized on the host. */
    default boolean isAuthorized(SshHost host, SshPublicKey key) {
        return readAuthorizedKeys(host).contains(key);
    }

    /** All authorized keys on the remote host. */
    default List<SshPublicKey> listKeys(SshHost host) {
        return readAuthorizedKeys(host).keys();
    }

    /** Number of authorized keys on the remote host. */
    default int keyCount(SshHost host) {
        return readAuthorizedKeys(host).size();
    }

    /** True if the remote host has no authorized keys at all. */
    default boolean hasNoKeys(SshHost host) {
        return readAuthorizedKeys(host).isEmpty();
    }

    /** Find a key by its comment (e.g. "joshua@WORK"). */
    default Optional<SshPublicKey> findKeyByComment(SshHost host, String comment) {
        return readAuthorizedKeys(host).keys().stream()
                .filter(k -> k.comment().equalsIgnoreCase(comment))
                .findFirst();
    }

    /** All keys on the remote host using a specific algorithm. */
    default List<SshPublicKey> keysByAlgorithm(SshHost host,
            io.joshuasalcedo.os.domain.ssh.SshKeyAlgorithm algorithm) {
        return readAuthorizedKeys(host).keys().stream()
                .filter(k -> k.algorithm() == algorithm)
                .toList();
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    static SshHostAPI createDefault(SshClientAPI.SshConnectionConfig config) {
        return new SshjHostAPI(config);
    }
}

// ── SSHJ Implementation ───────────────────────────────────────────────────────

/**
 * Package-private SSHJ-backed implementation of {@link SshHostAPI}.
 * Reads and writes ~/.ssh/authorized_keys via remote commands through {@link SshClientAPI}.
 */
class SshjHostAPI implements SshHostAPI {

    private static final String AUTHORIZED_KEYS_PATH = "~/.ssh/authorized_keys";

    private final SshClientAPI client;

    SshjHostAPI(SshClientAPI.SshConnectionConfig config) {
        this.client = SshClientAPI.createDefault(config);
    }

    @Override
    public SshAuthorizedKeys readAuthorizedKeys(SshHost host) {
        client.execute(host, "mkdir -p ~/.ssh && chmod 700 ~/.ssh && touch " + AUTHORIZED_KEYS_PATH);

        SshClientAPI.SshCommandResult result = client.execute(host, "cat " + AUTHORIZED_KEYS_PATH);

        if (result.isFailed())
            throw new SshException(SshException.Reason.PRIVATE_KEY_UNREADABLE, host,
                    "Could not read authorized_keys: " + result.stderr());

        return SshAuthorizedKeys.parse(result.stdout());
    }

    @Override
    public void writeAuthorizedKeys(SshHost host, SshAuthorizedKeys authorizedKeys) {
        // Escape single quotes for shell safety, write atomically via tmp file
        String content = authorizedKeys.toFileContent().replace("'", "'\\''");

        String command = String.format(
                "mkdir -p ~/.ssh && chmod 700 ~/.ssh && " +
                "printf '%%s\\n' '%s' > ~/.ssh/authorized_keys.tmp && " +
                "mv ~/.ssh/authorized_keys.tmp ~/.ssh/authorized_keys && " +
                "chmod 600 ~/.ssh/authorized_keys",
                content
        );

        SshClientAPI.SshCommandResult result = client.execute(host, command);

        if (result.isFailed())
            throw new SshException(SshException.Reason.PRIVATE_KEY_UNREADABLE, host,
                    "Could not write authorized_keys: " + result.stderr());
    }
}
