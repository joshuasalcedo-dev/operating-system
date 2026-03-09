package io.joshuasalcedo.os.application.api.network;

import io.joshuasalcedo.os.domain.network.InternetProtocolStats;
import oshi.SystemInfo;

final class OshiInternetProtocolStatsAPI implements InternetProtocolStatsAPI {

    private final SystemInfo systemInfo;

    OshiInternetProtocolStatsAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public InternetProtocolStats getStats() {
        oshi.software.os.InternetProtocolStats ips = systemInfo.getOperatingSystem().getInternetProtocolStats();
        oshi.software.os.InternetProtocolStats.TcpStats tcp4 = ips.getTCPv4Stats();
        oshi.software.os.InternetProtocolStats.TcpStats tcp6 = ips.getTCPv6Stats();
        oshi.software.os.InternetProtocolStats.UdpStats udp4 = ips.getUDPv4Stats();
        oshi.software.os.InternetProtocolStats.UdpStats udp6 = ips.getUDPv6Stats();

        return new InternetProtocolStats(
                tcp4.getConnectionsEstablished() + tcp6.getConnectionsEstablished(),
                tcp4.getConnectionsActive() + tcp6.getConnectionsActive(),
                tcp4.getConnectionsPassive() + tcp6.getConnectionsPassive(),
                tcp4.getConnectionFailures() + tcp6.getConnectionFailures(),
                tcp4.getConnectionsReset() + tcp6.getConnectionsReset(),
                tcp4.getSegmentsSent() + tcp6.getSegmentsSent(),
                tcp4.getSegmentsReceived() + tcp6.getSegmentsReceived(),
                tcp4.getSegmentsRetransmitted() + tcp6.getSegmentsRetransmitted(),
                tcp4.getInErrors() + tcp6.getInErrors(),
                tcp4.getOutResets() + tcp6.getOutResets(),
                udp4.getDatagramsSent() + udp6.getDatagramsSent(),
                udp4.getDatagramsReceived() + udp6.getDatagramsReceived(),
                udp4.getDatagramsNoPort() + udp6.getDatagramsNoPort(),
                udp4.getDatagramsReceivedErrors() + udp6.getDatagramsReceivedErrors()
        );
    }
}
