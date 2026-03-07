package io.joshuasalcedo.os.domain.network;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Value object representing DNS resolver configuration.
 */
public record DnsConfiguration(
        String hostname,
        String domainName,
        List<String> nameServers,
        List<String> searchDomains
) implements OSNetworkObject {

    public DnsConfiguration {
        Objects.requireNonNull(hostname, "Hostname must not be null");
        nameServers = nameServers != null ? Collections.unmodifiableList(nameServers) : List.of();
        searchDomains = searchDomains != null ? Collections.unmodifiableList(searchDomains) : List.of();
    }

    @Override
    public String toString() {
        return String.format("DNS[host=%s, servers=%s]", hostname, nameServers);
    }
}
