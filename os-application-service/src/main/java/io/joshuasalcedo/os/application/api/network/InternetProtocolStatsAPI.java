package io.joshuasalcedo.os.application.api.network;

import io.joshuasalcedo.os.domain.network.InternetProtocolStats;

/**
 * Service interface for querying aggregate TCP/UDP protocol statistics.
 */
interface InternetProtocolStatsAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns the current aggregate IP protocol statistics. */
    InternetProtocolStats getStats();

    // ── TCP connections ───────────────────────────────────────────────────────

    /** Number of currently established TCP connections. */
    default long establishedCount() {
        return getStats().tcpConnectionsEstablished();
    }

    /** Number of TCP connections actively opened by this host (outbound initiated). */
    default long activeOpenCount() {
        return getStats().tcpConnectionsActive();
    }

    /** Number of TCP connections passively opened (inbound / server-side accepts). */
    default long passiveOpenCount() {
        return getStats().tcpConnectionsPassive();
    }

    /** Number of TCP connection attempts that failed. */
    default long connectionFailures() {
        return getStats().tcpConnectionFailures();
    }

    /** Number of TCP connections reset (RST sent or received). */
    default long connectionsReset() {
        return getStats().tcpConnectionsReset();
    }

    // ── TCP segments ──────────────────────────────────────────────────────────

    /** Total TCP segments sent since boot. */
    default long segmentsSent() {
        return getStats().tcpSegmentsSent();
    }

    /** Total TCP segments received since boot. */
    default long segmentsReceived() {
        return getStats().tcpSegmentsReceived();
    }

    /** Total TCP segments retransmitted (indicator of network quality). */
    default long retransmits() {
        return getStats().tcpSegmentsRetransmitted();
    }

    /** Retransmission rate as a percentage of total sent segments (0–100). */
    default double retransmitRate() {
        long sent = segmentsSent();
        return sent > 0 ? 100.0 * retransmits() / sent : 0.0;
    }

    /** True if retransmit rate exceeds the given threshold percent. */
    default boolean isRetransmitHigh(double thresholdPercent) {
        return retransmitRate() >= thresholdPercent;
    }

    /** Total inbound TCP errors (bad checksums, out-of-window, etc.). */
    default long tcpInErrors() {
        return getStats().tcpInErrors();
    }

    /** Total outbound TCP resets sent. */
    default long tcpOutResets() {
        return getStats().tcpOutResets();
    }

    // ── UDP ───────────────────────────────────────────────────────────────────

    /** Total UDP datagrams sent since boot. */
    default long udpSent() {
        return getStats().udpDatagramsSent();
    }

    /** Total UDP datagrams received since boot. */
    default long udpReceived() {
        return getStats().udpDatagramsReceived();
    }

    /** UDP datagrams dropped because no application was listening on the destination port. */
    default long udpNoPort() {
        return getStats().udpDatagramsNoPort();
    }

    /** UDP datagrams dropped due to receive errors (buffer overflow, bad checksum, etc.). */
    default long udpReceiveErrors() {
        return getStats().udpDatagramsReceivedErrors();
    }

    /** True if there are any TCP or UDP errors worth investigating. */
    default boolean hasErrors() {
        return tcpInErrors() > 0 || udpReceiveErrors() > 0 || connectionFailures() > 0;
    }

    static InternetProtocolStatsAPI oshi(oshi.SystemInfo systemInfo) {
        return new OshiInternetProtocolStatsAPI(systemInfo);
    }
}
