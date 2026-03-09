package io.joshuasalcedo.os.application.api.network;

import io.joshuasalcedo.os.domain.network.NetworkRoute;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for querying the system routing table.
 */
interface NetworkRouteAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns all entries in the system routing table. */
    List<NetworkRoute> getRoutes();

    // ── Default route ─────────────────────────────────────────────────────────

    /** The default gateway route (destination 0.0.0.0, "::", or "default"). */
    default Optional<NetworkRoute> defaultRoute() {
        return getRoutes().stream()
                .filter(NetworkRoute::isDefaultRoute)
                .findFirst();
    }

    /** True if a default gateway is configured. */
    default boolean hasDefaultRoute() {
        return defaultRoute().isPresent();
    }

    /** The default gateway IP, if one is configured. */
    default Optional<String> defaultGateway() {
        return defaultRoute().map(NetworkRoute::gateway);
    }

    // ── By interface ──────────────────────────────────────────────────────────

    /** All routes assigned to a specific network interface. */
    default List<NetworkRoute> byInterface(String interfaceName) {
        return getRoutes().stream()
                .filter(r -> interfaceName.equalsIgnoreCase(r.interfaceName()))
                .toList();
    }

    /** All distinct interface names that appear in the routing table. */
    default List<String> routedInterfaces() {
        return getRoutes().stream()
                .map(NetworkRoute::interfaceName)
                .filter(n -> n != null && !n.isBlank())
                .distinct()
                .toList();
    }

    // ── By destination ────────────────────────────────────────────────────────

    /** Find a route by exact destination string (e.g. "192.168.1.0"). */
    default Optional<NetworkRoute> findByDestination(String destination) {
        return getRoutes().stream()
                .filter(r -> r.destination().equalsIgnoreCase(destination))
                .findFirst();
    }

    // ── By gateway ────────────────────────────────────────────────────────────

    /** All routes that pass through a specific gateway IP. */
    default List<NetworkRoute> viaGateway(String gateway) {
        return getRoutes().stream()
                .filter(r -> gateway.equals(r.gateway()))
                .toList();
    }

    // ── Metric / preference ───────────────────────────────────────────────────

    /** All routes sorted by metric ascending (lowest = most preferred). */
    default List<NetworkRoute> byMetric() {
        return getRoutes().stream()
                .sorted(java.util.Comparator.comparingInt(NetworkRoute::metric))
                .toList();
    }

    /** The most preferred non-default route (lowest metric, excluding default). */
    default Optional<NetworkRoute> mostPreferredRoute() {
        return getRoutes().stream()
                .filter(r -> !r.isDefaultRoute())
                .min(java.util.Comparator.comparingInt(NetworkRoute::metric));
    }

}
