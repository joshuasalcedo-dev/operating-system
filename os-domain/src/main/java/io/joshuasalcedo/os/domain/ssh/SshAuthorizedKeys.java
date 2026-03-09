package io.joshuasalcedo.os.domain.ssh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Value object representing the contents of an authorized_keys file.
 */
public record SshAuthorizedKeys(
        List<SshPublicKey> keys
) {
    public SshAuthorizedKeys {
        keys = keys != null ? Collections.unmodifiableList(new ArrayList<>(keys)) : List.of();
    }

    // ── Factories ─────────────────────────────────────────────────────────────

    public static SshAuthorizedKeys empty() {
        return new SshAuthorizedKeys(List.of());
    }

    public static SshAuthorizedKeys of(List<SshPublicKey> keys) {
        return new SshAuthorizedKeys(keys);
    }

    public static SshAuthorizedKeys parse(String fileContent) {
        if (fileContent == null || fileContent.isBlank()) return empty();
        List<SshPublicKey> parsed = fileContent.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .map(SshPublicKey::parse)
                .toList();
        return new SshAuthorizedKeys(parsed);
    }

    // ── Withers ───────────────────────────────────────────────────────────────

    /** Returns a new instance with the given key appended. */
    public SshAuthorizedKeys withKey(SshPublicKey key) {
        List<SshPublicKey> updated = new ArrayList<>(keys);
        updated.add(key);
        return new SshAuthorizedKeys(updated);
    }

    /** Returns a new instance with the given key removed (matched by base64 blob). */
    public SshAuthorizedKeys withoutKey(SshPublicKey key) {
        return new SshAuthorizedKeys(
                keys.stream()
                    .filter(k -> !k.base64Key().equals(key.base64Key()))
                    .toList()
        );
    }

    /** Returns a new instance with all keys matching the given comment removed. */
    public SshAuthorizedKeys withoutComment(String comment) {
        return new SshAuthorizedKeys(
                keys.stream()
                    .filter(k -> !k.comment().equalsIgnoreCase(comment))
                    .toList()
        );
    }

    /** Returns a new instance with all keys of the given algorithm removed. */
    public SshAuthorizedKeys withoutAlgorithm(SshKeyAlgorithm algorithm) {
        return new SshAuthorizedKeys(
                keys.stream()
                    .filter(k -> k.algorithm() != algorithm)
                    .toList()
        );
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public boolean contains(SshPublicKey key) {
        return keys.stream().anyMatch(k -> k.base64Key().equals(key.base64Key()));
    }

    public int size()        { return keys.size(); }
    public boolean isEmpty() { return keys.isEmpty(); }

    /** Find a key by comment (e.g. "joshua@WORK"). */
    public Optional<SshPublicKey> findByComment(String comment) {
        return keys.stream()
                .filter(k -> k.comment().equalsIgnoreCase(comment))
                .findFirst();
    }

    /** All keys using the given algorithm. */
    public List<SshPublicKey> byAlgorithm(SshKeyAlgorithm algorithm) {
        return keys.stream()
                .filter(k -> k.algorithm() == algorithm)
                .toList();
    }

    /** True if all keys use the same algorithm. */
    public boolean isUniformAlgorithm() {
        return keys.stream().map(SshPublicKey::algorithm).distinct().count() <= 1;
    }

    /** True if any key has no comment set. */
    public boolean hasUnlabelledKeys() {
        return keys.stream().anyMatch(k -> k.comment().isBlank());
    }

    // ── Serialization ─────────────────────────────────────────────────────────

    /** Renders back to authorized_keys file format. */
    public String toFileContent() {
        return keys.stream()
                .map(SshPublicKey::toAuthorizedKeysLine)
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "\n" + b);
    }
}