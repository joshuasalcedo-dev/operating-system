package io.joshuasalcedo.os.application.api.network;

import io.joshuasalcedo.os.domain.network.UdpConnection;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service interface for querying UDP endpoints.
 */
interface UdpConnectionAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns all current UDP endpoints on the system. */
    List<UdpConnection> getEndpoints();

    // ── By port ───────────────────────────────────────────────────────────────

    /** Find a UDP endpoint bound to a specific port number. */
    default Optional<UdpConnection> onPort(int port) {
        return getEndpoints().stream()
                .filter(u -> u.localAddress().port() == port)
                .findFirst();
    }

    /** True if a UDP endpoint is bound to the given port. */
    default boolean isPortInUse(int port) {
        return onPort(port).isPresent();
    }

    // ── By process ────────────────────────────────────────────────────────────

    /** All UDP endpoints owned by the given PID. */
    default List<UdpConnection> byPid(long pid) {
        return getEndpoints().stream()
                .filter(u -> u.pid() == pid)
                .toList();
    }

    /** All UDP endpoints owned by a process name (partial, case-insensitive). */
    default List<UdpConnection> byProcess(String processName) {
        return getEndpoints().stream()
                .filter(u -> u.processName() != null &&
                             u.processName().toLowerCase().contains(processName.toLowerCase()))
                .toList();
    }

    // ── By bind address ───────────────────────────────────────────────────────

    /** Endpoints bound to all interfaces (0.0.0.0 or ::). */
    default List<UdpConnection> bindAll() {
        return getEndpoints().stream()
                .filter(u -> u.localAddress().isAny())
                .toList();
    }

    /** Endpoints bound only to loopback (127.0.0.1 or ::1). */
    default List<UdpConnection> loopback() {
        return getEndpoints().stream()
                .filter(u -> u.localAddress().isLoopback())
                .toList();
    }

    /** Endpoints using IPv6 addresses. */
    default List<UdpConnection> ipv6Endpoints() {
        return getEndpoints().stream()
                .filter(u -> u.localAddress().isIpv6())
                .toList();
    }

    // ── Aggregates ────────────────────────────────────────────────────────────

    /** All distinct process names that have at least one UDP endpoint. */
    default Set<String> listeningProcessNames() {
		Set<String> set = new HashSet<>();
		for (UdpConnection udpConnection : getEndpoints()) {
			String s = udpConnection.processName();
			if (s != null) {
				set.add(s);
			}
		}
		return set;
    }

    /** All distinct ports currently in use by UDP endpoints. */
    default Set<Integer> usedPorts() {
        return getEndpoints().stream()
                .map(u -> u.localAddress().port())
                .collect(Collectors.toSet());
    }

    /** Total number of UDP endpoints. */
    default int count() {
        return getEndpoints().size();
    }

}
