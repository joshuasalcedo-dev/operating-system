package io.joshuasalcedo.os.domain.ssh;

import java.util.Objects;

/**
 * Domain exception for SSH operations.
 *
 * @author JoshuaSalcedo
 */
public class SshException extends RuntimeException {

    private final Reason reason;
    private final SshHost host;

    public SshException(Reason reason, String s, Throwable cause) {
        super(s, cause);
        this.reason = reason;
        this.host = null;
    }

    public enum Reason {

        // ── Key management ────────────────────────────────────────────────────
        KEY_ALREADY_AUTHORIZED      ("Public key is already present in authorized_keys"),
        KEY_NOT_FOUND               ("Public key was not found in authorized_keys"),
        KEY_GENERATION_FAILED       ("Failed to generate SSH key pair"),
        INVALID_KEY_FORMAT          ("The provided string is not a valid SSH public key"),
        WEAK_KEY                    ("Key does not meet minimum security requirements"),
        DUPLICATE_IDENTITY_NAME     ("An SSH identity with this name already exists"),

        // ── Host management ───────────────────────────────────────────────────
        HOST_ALREADY_AUTHORIZED     ("Host is already in the authorized list for this identity"),
        HOST_NOT_AUTHORIZED         ("Host is not in the authorized list for this identity"),
        HOST_UNREACHABLE            ("Could not reach the remote SSH host"),
        HOST_KEY_MISMATCH           ("Remote host key does not match known_hosts entry — possible MITM"),
        HOST_KEY_UNKNOWN            ("Remote host key is not in known_hosts and strict checking is enabled"),

        // ── Authentication ────────────────────────────────────────────────────
        AUTHENTICATION_FAILED       ("SSH authentication was rejected by the remote host"),
        PASSPHRASE_REQUIRED         ("Private key is encrypted but no passphrase was provided"),
        WRONG_PASSPHRASE            ("The provided passphrase did not decrypt the private key"),
        PRIVATE_KEY_NOT_FOUND       ("Private key file does not exist at the specified path"),
        PRIVATE_KEY_UNREADABLE      ("Private key file exists but could not be read"),

        // ── Identity ──────────────────────────────────────────────────────────
        IDENTITY_NOT_FOUND          ("No SSH identity found matching the given criteria"),
        KEY_ROTATION_FAILED         ("Failed to rotate SSH key pair for the given identity"),

        // ── General ───────────────────────────────────────────────────────────
        UNKNOWN                     ("An unexpected SSH error occurred");

        private final String defaultMessage;

        Reason(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }

        public String defaultMessage() {
            return defaultMessage;
        }
    }

    // ── Constructors ──────────────────────────────────────────────────────────

    public SshException(Reason reason) {
        super(reason.defaultMessage());
        this.reason = Objects.requireNonNull(reason);
        this.host = null;
    }

    public SshException(Reason reason, SshHost host) {
        super(messageWithHost(reason.defaultMessage(), host));
        this.reason = Objects.requireNonNull(reason);
        this.host = host;
    }

    public SshException(Reason reason, String detail) {
        super(reason.defaultMessage() + ": " + detail);
        this.reason = Objects.requireNonNull(reason);
        this.host = null;
    }

    public SshException(Reason reason, SshHost host, String detail) {
        super(messageWithHost(reason.defaultMessage() + ": " + detail, host));
        this.reason = Objects.requireNonNull(reason);
        this.host = host;
    }

    public SshException(Reason reason, Throwable cause) {
        super(reason.defaultMessage(), cause);
        this.reason = Objects.requireNonNull(reason);
        this.host = null;
    }

    public SshException(Reason reason, SshHost host, Throwable cause) {
        super(messageWithHost(reason.defaultMessage(), host), cause);
        this.reason = Objects.requireNonNull(reason);
        this.host = host;
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public Reason reason() {
        return reason;
    }

    /** The host involved in the failure, if applicable. */
    public java.util.Optional<SshHost> host() {
        return java.util.Optional.ofNullable(host);
    }

    // ── Static factories (readability at call sites) ──────────────────────────

    public static SshException keyAlreadyAuthorized(SshPublicKey key) {
        return new SshException(Reason.KEY_ALREADY_AUTHORIZED, key.fingerprint());
    }

    public static SshException keyNotFound(SshPublicKey key) {
        return new SshException(Reason.KEY_NOT_FOUND, key.fingerprint());
    }

    public static SshException invalidKeyFormat(String raw) {
        return new SshException(Reason.INVALID_KEY_FORMAT, raw);
    }

    public static SshException weakKey(SshKeyAlgorithm algorithm, int keySize) {
        return new SshException(Reason.WEAK_KEY,
                algorithm.identifier() + " with " + keySize + " bits is below minimum strength");
    }

    public static SshException hostAlreadyAuthorized(SshIdentity identity, SshHost host) {
        return new SshException(Reason.HOST_ALREADY_AUTHORIZED, host,
                "identity='" + identity.name() + "'");
    }

    public static SshException hostNotAuthorized(SshIdentity identity, SshHost host) {
        return new SshException(Reason.HOST_NOT_AUTHORIZED, host,
                "identity='" + identity.name() + "'");
    }

    public static SshException authenticationFailed(SshHost host) {
        return new SshException(Reason.AUTHENTICATION_FAILED, host);
    }

    public static SshException passphraseRequired(SshHost host) {
        return new SshException(Reason.PASSPHRASE_REQUIRED, host);
    }

    public static SshException privateKeyNotFound(java.nio.file.Path path) {
        return new SshException(Reason.PRIVATE_KEY_NOT_FOUND, path.toString());
    }

    public static SshException keyGenerationFailed(SshKeyGenerationRequest request, Throwable cause) {
        return new SshException(Reason.KEY_GENERATION_FAILED,
                request.algorithm().identifier() + " " + request.keySize() + " bits", cause);
    }

    public static SshException identityNotFound(String name) {
        return new SshException(Reason.IDENTITY_NOT_FOUND, "name='" + name + "'");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String messageWithHost(String message, SshHost host) {
        return host != null ? "[" + host + "] " + message : message;
    }
}