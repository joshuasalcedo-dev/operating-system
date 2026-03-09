package io.joshuasalcedo.os.application.api.network;

import io.joshuasalcedo.os.domain.network.SocketAddress;
import io.joshuasalcedo.os.domain.network.UdpConnection;
import oshi.SystemInfo;
import oshi.software.os.InternetProtocolStats.IPConnection;

import java.util.List;

final class OshiUdpConnectionAPI implements UdpConnectionAPI {

    private final SystemInfo systemInfo;

    OshiUdpConnectionAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<UdpConnection> getEndpoints() {
        return systemInfo.getOperatingSystem().getInternetProtocolStats().getConnections().stream()
                .filter(c -> "udp".equalsIgnoreCase(c.getType()) || "udp4".equalsIgnoreCase(c.getType())
                        || "udp6".equalsIgnoreCase(c.getType()))
                .map(this::mapEndpoint)
                .toList();
    }

    private UdpConnection mapEndpoint(IPConnection c) {
        return new UdpConnection(
                new SocketAddress(arrayToIp(c.getLocalAddress()), c.getLocalPort()),
                c.getowningProcessId(),
                ""
        );
    }

    private static String arrayToIp(byte[] addr) {
        if (addr == null || addr.length == 0) return "0.0.0.0";
        if (addr.length == 4) {
            return (addr[0] & 0xFF) + "." + (addr[1] & 0xFF) + "." + (addr[2] & 0xFF) + "." + (addr[3] & 0xFF);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < addr.length; i += 2) {
            if (i > 0) sb.append(':');
            sb.append(String.format("%02x%02x", addr[i] & 0xFF, i + 1 < addr.length ? addr[i + 1] & 0xFF : 0));
        }
        return sb.toString();
    }
}
