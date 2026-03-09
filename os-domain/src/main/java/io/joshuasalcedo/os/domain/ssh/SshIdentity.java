package io.joshuasalcedo.os.domain.ssh;

import java.util.Collections;
import java.util.List;
import java.util.Objects; /**
 * Aggregate interface representing an SSH identity — a named key pair and
 * the set of hosts it is authorized on.
 * <p>
 * No aggregate ID is carried here; identity is established by the public key itself.
 */
public interface SshIdentity {

    /** The key pair this identity is built around. */
    SshKeyPair keyPair();

    /** Human-readable name for this identity (e.g. "work-laptop", "deploy-key"). */
    String name();

    /** The hosts this identity's public key has been installed on. */
    List<SshHost> authorizedHosts();

    /** True if this identity's public key has been installed on the given host. */
    default boolean isAuthorizedOn(SshHost host) {
        return authorizedHosts().stream()
                .anyMatch(h -> h.hostname().equalsIgnoreCase(host.hostname())
                            && h.username().equalsIgnoreCase(host.username()));
    }

    /** The public key of this identity. */
    default SshPublicKey publicKey() {
        return keyPair().publicKey();
    }

    /** True if the private key is passphrase-protected. */
    default boolean isProtected() {
        return keyPair().isEncrypted();
    }

    /** Returns a new SshIdentity with the given host added to the authorized list. */
    default SshIdentity withAuthorizedHost(SshHost host) {
        List<SshHost> updated = new java.util.ArrayList<>(authorizedHosts());
        if (!isAuthorizedOn(host)) updated.add(host);
        return SshIdentity.of(keyPair(), name(), Collections.unmodifiableList(updated));
    }

    /** Returns a new SshIdentity with the given host removed from the authorized list. */
    default SshIdentity withoutAuthorizedHost(SshHost host) {
        List<SshHost> updated = authorizedHosts().stream()
                .filter(h -> !(h.hostname().equalsIgnoreCase(host.hostname())
                             && h.username().equalsIgnoreCase(host.username())))
                .toList();
        return SshIdentity.of(keyPair(), name(), updated);
    }

    /** Factory: create a new SshIdentity from a key pair with no authorized hosts yet. */
    static SshIdentity of(SshKeyPair keyPair, String name) {
        return of(keyPair, name, List.of());
    }

    static SshIdentity of(SshKeyPair keyPair, String name, List<SshHost> authorizedHosts) {
        Objects.requireNonNull(keyPair, "KeyPair must not be null");
        Objects.requireNonNull(name,    "Name must not be null");
        List<SshHost> hosts = Collections.unmodifiableList(
                authorizedHosts != null ? new java.util.ArrayList<>(authorizedHosts) : List.of());
        return new SshIdentity() {
            @Override public SshKeyPair keyPair()             { return keyPair; }
            @Override public String name()                    { return name; }
            @Override public List<SshHost> authorizedHosts()  { return hosts; }
            @Override public String toString() {
                return name + " [" + keyPair.algorithm().identifier() + "] hosts=" + hosts.size();
            }
        };
    }
}
