package io.joshuasalcedo.os.application.api.network;

import io.joshuasalcedo.os.domain.network.DnsConfiguration;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for querying DNS resolver configuration.
 */
interface DnsConfigurationAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns the current DNS resolver configuration of the system. */
    DnsConfiguration getDnsConfiguration();

    // ── Identity ──────────────────────────────────────────────────────────────

    /** The system hostname. */
    default String hostname() {
        return getDnsConfiguration().hostname();
    }

    /** The system domain name (may be empty on standalone machines). */
    default Optional<String> domainName() {
        String domain = getDnsConfiguration().domainName();
        return (domain != null && !domain.isBlank()) ? Optional.of(domain) : Optional.empty();
    }

    /** The fully qualified domain name (hostname + domain, if domain is present). */
    default String fqdn() {
        return domainName()
                .map(d -> hostname() + "." + d)
                .orElse(hostname());
    }

    // ── Name servers ──────────────────────────────────────────────────────────

    /** All configured DNS name server addresses. */
    default List<String> nameServers() {
        return getDnsConfiguration().nameServers();
    }

    /** The primary (first) DNS name server, if any. */
    default Optional<String> primaryNameServer() {
        List<String> servers = nameServers();
        return servers.isEmpty() ? Optional.empty() : Optional.of(servers.get(0));
    }

    /** True if the system is using a local DNS resolver (127.x or ::1). */
    default boolean usesLocalResolver() {
        return nameServers().stream()
                .anyMatch(s -> s.startsWith("127.") || s.equals("::1"));
    }

    /** True if any name server is a well-known public DNS (8.8.8.8, 1.1.1.1, 9.9.9.9). */
    default boolean usesPublicDns() {
        return nameServers().stream()
                .anyMatch(s -> s.equals("8.8.8.8") || s.equals("8.8.4.4")   // Google
                            || s.equals("1.1.1.1") || s.equals("1.0.0.1")   // Cloudflare
                            || s.equals("9.9.9.9") || s.equals("149.112.112.112")); // Quad9
    }

    /** Number of configured name servers. */
    default int nameServerCount() {
        return nameServers().size();
    }

    /** True if no name servers are configured (DNS is broken). */
    default boolean hasNoDnsServers() {
        return nameServers().isEmpty();
    }

    // ── Search domains ────────────────────────────────────────────────────────

    /** DNS search domain suffixes (used to resolve short hostnames). */
    default List<String> searchDomains() {
        return getDnsConfiguration().searchDomains();
    }

    /** True if a specific domain is in the search list. */
    default boolean hasSearchDomain(String domain) {
        return searchDomains().stream()
                .anyMatch(d -> d.equalsIgnoreCase(domain));
    }

    static DnsConfigurationAPI oshi(oshi.SystemInfo systemInfo) {
        return new OshiDnsConfigurationAPI(systemInfo);
    }
}
