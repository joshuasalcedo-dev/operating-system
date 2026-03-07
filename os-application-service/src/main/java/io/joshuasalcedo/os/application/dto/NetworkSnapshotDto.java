package io.joshuasalcedo.os.application.dto;

import java.time.Instant;
import java.util.List;

public record NetworkSnapshotDto(
    Instant capturedAt,
    List<TcpConnectionDto> tcpConnections,
    List<UdpConnectionDto> udpEndpoints,
    List<ListeningPortDto> listeningPorts,
    List<NetworkRouteDto> routes,
    DnsConfigurationDto dns
) {}
