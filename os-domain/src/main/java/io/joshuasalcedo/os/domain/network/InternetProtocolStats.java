package io.joshuasalcedo.os.domain.network;

/**
 * Value object representing aggregate TCP/UDP protocol statistics.
 */
public record InternetProtocolStats(
        long tcpConnectionsEstablished,
        long tcpConnectionsActive,
        long tcpConnectionsPassive,
        long tcpConnectionFailures,
        long tcpConnectionsReset,
        long tcpSegmentsSent,
        long tcpSegmentsReceived,
        long tcpSegmentsRetransmitted,
        long tcpInErrors,
        long tcpOutResets,
        long udpDatagramsSent,
        long udpDatagramsReceived,
        long udpDatagramsNoPort,
        long udpDatagramsReceivedErrors
) implements OSNetworkObject {

    @Override
    public String toString() {
        return String.format("TCP[est=%d active=%d passive=%d fail=%d] UDP[sent=%d recv=%d]",
                tcpConnectionsEstablished, tcpConnectionsActive, tcpConnectionsPassive,
                tcpConnectionFailures, udpDatagramsSent, udpDatagramsReceived);
    }
}
