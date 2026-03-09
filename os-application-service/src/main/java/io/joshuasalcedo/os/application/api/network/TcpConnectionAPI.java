package io.joshuasalcedo.os.application.api.network;

import io.joshuasalcedo.os.domain.network.ConnectionState;
import io.joshuasalcedo.os.domain.network.TcpConnection;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service interface for querying TCP connections.
 */
interface TcpConnectionAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns all current TCP connections on the system. */
    List<TcpConnection> getConnections();

    /** Returns all TCP connections in the given state. */
    List<TcpConnection> getConnections(ConnectionState state);

    // ── By state ──────────────────────────────────────────────────────────────

    /** Fully established connections (data flowing). */
    default List<TcpConnection> established() {
        return getConnections(ConnectionState.ESTABLISHED);
    }

    /** Connections waiting for remote FIN (server closed its side, client still open). */
    default List<TcpConnection> closeWait() {
        return getConnections(ConnectionState.CLOSE_WAIT);
    }

    /** Connections in TIME_WAIT (local side closed, waiting for delayed packets). */
    default List<TcpConnection> timeWait() {
        return getConnections(ConnectionState.TIME_WAIT);
    }

    /** Connections in any handshake state (SYN_SENT or SYN_RECEIVED). */
    default List<TcpConnection> handshaking() {
        return Stream.of(ConnectionState.SYN_SENT, ConnectionState.SYN_RECEIVED)
                .flatMap(s -> getConnections(s).stream())
                .toList();
    }

    /** Connections in any teardown/closing state. */
    default List<TcpConnection> closing() {
        return Stream.of(
                ConnectionState.CLOSE_WAIT, ConnectionState.TIME_WAIT,
                ConnectionState.FIN_WAIT_1, ConnectionState.FIN_WAIT_2,
                ConnectionState.LAST_ACK, ConnectionState.CLOSING)
                .flatMap(s -> getConnections(s).stream())
                .toList();
    }

    // ── By process ────────────────────────────────────────────────────────────

    /** All connections owned by the given PID. */
    default List<TcpConnection> byPid(long pid) {
        return getConnections().stream()
                .filter(c -> c.pid() == pid)
                .toList();
    }

    /** All connections owned by a process name (partial, case-insensitive). */
    default List<TcpConnection> byProcess(String processName) {
        return getConnections().stream()
                .filter(c -> c.processName() != null &&
                             c.processName().toLowerCase().contains(processName.toLowerCase()))
                .toList();
    }

    // ── By address ────────────────────────────────────────────────────────────

    /** All connections on a specific local port. */
    default List<TcpConnection> onLocalPort(int port) {
        return getConnections().stream()
                .filter(c -> c.localAddress().port() == port)
                .toList();
    }

    /** All connections to a specific remote IP. */
    default List<TcpConnection> toRemoteIp(String ip) {
        return getConnections().stream()
                .filter(c -> c.remoteAddress().ip().equals(ip))
                .toList();
    }

    /** All connections to a specific remote port (e.g. 443 for outbound HTTPS). */
    default List<TcpConnection> toRemotePort(int port) {
        return getConnections().stream()
                .filter(c -> c.remoteAddress().port() == port)
                .toList();
    }

    /** All connections from/to loopback addresses. */
    default List<TcpConnection> loopback() {
        return getConnections().stream()
                .filter(c -> c.localAddress().isLoopback() || c.remoteAddress().isLoopback())
                .toList();
    }

    /** All connections where both sides are external (non-loopback). */
    default List<TcpConnection> external() {
        return getConnections().stream()
                .filter(c -> !c.localAddress().isLoopback() && !c.remoteAddress().isLoopback())
                .toList();
    }

    // ── IP version ────────────────────────────────────────────────────────────

    /** Connections using IPv6 addresses. */
    default List<TcpConnection> ipv6Connections() {
        return getConnections().stream()
                .filter(c -> c.localAddress().isIpv6())
                .toList();
    }

    /** Connections using IPv4 addresses. */
    default List<TcpConnection> ipv4Connections() {
        return getConnections().stream()
                .filter(c -> !c.localAddress().isIpv6())
                .toList();
    }

    // ── Aggregates ────────────────────────────────────────────────────────────

    /** All distinct remote IPs that are currently established. */
    default Set<String> connectedRemoteIps() {
        return established().stream()
                .map(c -> c.remoteAddress().ip())
                .collect(Collectors.toSet());
    }

    /** All distinct process names that have at least one established connection. */
    default Set<String> connectedProcessNames() {
        return established().stream()
                .filter(c -> c.processName() != null)
                .map(TcpConnection::processName)
                .collect(Collectors.toSet());
    }

    /** True if any established connection targets the given remote IP. */
    default boolean isConnectedTo(String remoteIp) {
        return established().stream()
                .anyMatch(c -> c.remoteAddress().ip().equals(remoteIp));
    }

}
