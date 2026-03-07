package io.joshuasalcedo.os.domain.network;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate value object representing a point-in-time network state capture.
 */
public record NetworkSnapshot(
        Instant capturedAt,
        List<TcpConnection> tcpConnections,
        List<UdpConnection> udpEndpoints,
        List<ListeningPort> listeningPorts,
        List<NetworkRoute> routes,
        DnsConfiguration dns,
        InternetProtocolStats protocolStats
) implements OSNetworkObject {

    public NetworkSnapshot {
        Objects.requireNonNull(capturedAt, "Capture timestamp must not be null");
        tcpConnections = tcpConnections != null ? Collections.unmodifiableList(tcpConnections) : List.of();
        udpEndpoints = udpEndpoints != null ? Collections.unmodifiableList(udpEndpoints) : List.of();
        listeningPorts = listeningPorts != null ? Collections.unmodifiableList(listeningPorts) : List.of();
        routes = routes != null ? Collections.unmodifiableList(routes) : List.of();
    }

    public List<TcpConnection> establishedConnections() {
        return tcpConnections.stream()
                .filter(TcpConnection::isEstablished)
                .toList();
    }

    @Override
    public String toString() {
        return String.format("NetworkSnapshot[%s] tcp=%d, udp=%d, listening=%d, routes=%d",
                capturedAt, tcpConnections.size(), udpEndpoints.size(),
                listeningPorts.size(), routes.size());
    }
}
