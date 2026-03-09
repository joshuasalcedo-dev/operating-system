package io.joshuasalcedo.os.domain.ssh;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// ═══════════════════════════════════════════════════════════════════════════════
// VALUE OBJECTS
// ═══════════════════════════════════════════════════════════════════════════════

// ── SshKeyAlgorithm ───────────────────────────────────────────────────────────

// ── SshPublicKey ──────────────────────────────────────────────────────────────

// ── SshKeyPair ────────────────────────────────────────────────────────────────

// ── SshKeyGenerationRequest ───────────────────────────────────────────────────

// ── SshAuthorizedKeys ─────────────────────────────────────────────────────────

// ── SshHost ───────────────────────────────────────────────────────────────────

// ═══════════════════════════════════════════════════════════════════════════════
// AGGREGATE
// ═══════════════════════════════════════════════════════════════════════════════

// ═══════════════════════════════════════════════════════════════════════════════
// DOMAIN SERVICE
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Domain service for SSH identity and key management operations.
 * No repository calls, no library calls — pure domain logic.
 */
public interface SshDomainAPI {

    // ── Key generation ────────────────────────────────────────────────────────

    /** Generates a new SSH key pair from the given request parameters. */
    SshKeyPair generateKeyPair(SshKeyGenerationRequest request);

    /** Generates a new identity (key pair + name) with no authorized hosts yet. */
    SshIdentity createIdentity(String name, SshKeyGenerationRequest request);

    // ── Authorized keys management ────────────────────────────────────────────

    /** Adds a public key to an authorized_keys set, returning the updated set. */
    SshAuthorizedKeys addKey(SshAuthorizedKeys authorizedKeys, SshPublicKey keyToAdd);

    /** Removes a public key from an authorized_keys set, returning the updated set. */
    SshAuthorizedKeys removeKey(SshAuthorizedKeys authorizedKeys, SshPublicKey keyToRemove);

    /** Revokes all keys matching the given comment (e.g. revoking a user by their hostname comment). */
    SshAuthorizedKeys revokeByComment(SshAuthorizedKeys authorizedKeys, String comment);

    // ── Identity ──────────────────────────────────────────────────────────────

    /** Registers a host as authorized for the given identity, returning the updated identity. */
    SshIdentity authorizeHost(SshIdentity identity, SshHost host);

    /** Deauthorizes a host from the given identity, returning the updated identity. */
    SshIdentity deauthorizeHost(SshIdentity identity, SshHost host);

    /** Rotates the key pair of an identity, preserving its name and authorized hosts. */
    SshIdentity rotateKey(SshIdentity identity, SshKeyGenerationRequest request);

    // ── Validation ────────────────────────────────────────────────────────────

    /** True if the given public key line is a valid authorized_keys entry. */
    boolean isValidPublicKey(String rawPublicKeyLine);

    /** True if the given authorized_keys set already contains the key. */
    default boolean isDuplicate(SshAuthorizedKeys authorizedKeys, SshPublicKey key) {
        return authorizedKeys.contains(key);
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    static SshDomainAPI createDefault() {
        return new DefaultSshDomainAPI();
    }
}

// ── DefaultSshDomainAPI ───────────────────────────────────────────────────────

/**
 * Package-private default implementation of {@link SshDomainAPI}.
 * Pure domain logic only — no library or I/O calls.
 */
class DefaultSshDomainAPI implements SshDomainAPI {

    @Override
    public SshKeyPair generateKeyPair(SshKeyGenerationRequest request) {
        Objects.requireNonNull(request, "Request must not be null");

        // Domain validation
        if (request.algorithm() == SshKeyAlgorithm.RSA && request.keySize() < 2048)
            throw new IllegalArgumentException(
                    "RSA keys must be at least 2048 bits. Requested: " + request.keySize());

        // The actual crypto is delegated to infrastructure — here we define WHAT a valid
        // key pair looks like and enforce domain invariants before it reaches the infra layer.
        // A real implementation would call a KeyPairGeneratorPort (infra) injected via constructor.
        throw new UnsupportedOperationException(
                "Key generation requires an infrastructure implementation. " +
                "Wire in a KeyPairGeneratorPort and delegate to it.");
    }

    @Override
    public SshIdentity createIdentity(String name, SshKeyGenerationRequest request) {
        Objects.requireNonNull(name, "Name must not be null");
        if (name.isBlank()) throw new IllegalArgumentException("Identity name must not be blank");

        SshKeyPair keyPair = generateKeyPair(request);
        return SshIdentity.of(keyPair, name);
    }

    @Override
    public SshAuthorizedKeys addKey(SshAuthorizedKeys authorizedKeys, SshPublicKey keyToAdd) {
        Objects.requireNonNull(authorizedKeys, "AuthorizedKeys must not be null");
        Objects.requireNonNull(keyToAdd,       "Key to add must not be null");

        if (authorizedKeys.contains(keyToAdd))
            throw new IllegalStateException(
                    "Public key already exists in authorized_keys: " + keyToAdd.fingerprint());

        List<SshPublicKey> updated = new java.util.ArrayList<>(authorizedKeys.keys());
        updated.add(keyToAdd);
        return SshAuthorizedKeys.of(Collections.unmodifiableList(updated));
    }

    @Override
    public SshAuthorizedKeys removeKey(SshAuthorizedKeys authorizedKeys, SshPublicKey keyToRemove) {
        Objects.requireNonNull(authorizedKeys, "AuthorizedKeys must not be null");
        Objects.requireNonNull(keyToRemove,    "Key to remove must not be null");

        if (!authorizedKeys.contains(keyToRemove))
            throw new IllegalStateException(
                    "Public key not found in authorized_keys: " + keyToRemove.fingerprint());

        List<SshPublicKey> updated = authorizedKeys.keys().stream()
                .filter(k -> !k.base64Key().equals(keyToRemove.base64Key()))
                .toList();
        return SshAuthorizedKeys.of(updated);
    }

    @Override
    public SshAuthorizedKeys revokeByComment(SshAuthorizedKeys authorizedKeys, String comment) {
        Objects.requireNonNull(authorizedKeys, "AuthorizedKeys must not be null");
        Objects.requireNonNull(comment,        "Comment must not be null");
        if (comment.isBlank()) throw new IllegalArgumentException("Comment must not be blank");

        List<SshPublicKey> updated = authorizedKeys.keys().stream()
                .filter(k -> !k.comment().equalsIgnoreCase(comment))
                .toList();
        return SshAuthorizedKeys.of(updated);
    }

    @Override
    public SshIdentity authorizeHost(SshIdentity identity, SshHost host) {
        Objects.requireNonNull(identity, "Identity must not be null");
        Objects.requireNonNull(host,     "Host must not be null");

        if (identity.isAuthorizedOn(host))
            throw new IllegalStateException(
                    "Identity '" + identity.name() + "' is already authorized on " + host);

        return identity.withAuthorizedHost(host);
    }

    @Override
    public SshIdentity deauthorizeHost(SshIdentity identity, SshHost host) {
        Objects.requireNonNull(identity, "Identity must not be null");
        Objects.requireNonNull(host,     "Host must not be null");

        if (!identity.isAuthorizedOn(host))
            throw new IllegalStateException(
                    "Identity '" + identity.name() + "' is not authorized on " + host);

        return identity.withoutAuthorizedHost(host);
    }

    @Override
    public SshIdentity rotateKey(SshIdentity identity, SshKeyGenerationRequest request) {
        Objects.requireNonNull(identity, "Identity must not be null");
        Objects.requireNonNull(request,  "Request must not be null");

        // Generate new key pair, preserve name and authorized hosts
        SshKeyPair newKeyPair = generateKeyPair(request);
        return SshIdentity.of(newKeyPair, identity.name(), identity.authorizedHosts());
    }

    @Override
    public boolean isValidPublicKey(String rawPublicKeyLine) {
        if (rawPublicKeyLine == null || rawPublicKeyLine.isBlank()) return false;
        try {
            SshPublicKey.parse(rawPublicKeyLine);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
