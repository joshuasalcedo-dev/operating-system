package io.joshuasalcedo.os.application.api.network;

import io.joshuasalcedo.os.domain.network.ConnectionState;
import io.joshuasalcedo.os.domain.network.SocketAddress;
import io.joshuasalcedo.os.domain.network.TcpConnection;
import oshi.SystemInfo;
import oshi.software.os.InternetProtocolStats.IPConnection;

import java.util.List;

final class OshiTcpConnectionAPI implements TcpConnectionAPI {

    private final SystemInfo systemInfo;

    OshiTcpConnectionAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<TcpConnection> getConnections() {
        return systemInfo.getOperatingSystem().getInternetProtocolStats().getConnections().stream()
                .filter(c -> "tcp".equalsIgnoreCase(c.getType()) || "tcp4".equalsIgnoreCase(c.getType())
                        || "tcp6".equalsIgnoreCase(c.getType()))
                .map(this::mapConnection)
                .toList();
    }

    @Override
    public List<TcpConnection> getConnections(ConnectionState state) {
        return getConnections().stream()
                .filter(c -> c.state() == state)
                .toList();
    }

    private TcpConnection mapConnection(IPConnection c) {
        return new TcpConnection(
                new SocketAddress(arrayToIp(c.getLocalAddress()), c.getLocalPort()),
                new SocketAddress(arrayToIp(c.getForeignAddress()), c.getForeignPort()),
                mapState(c.getState().name()),
                c.getowningProcessId(),
                ""
        );
    }

    private static ConnectionState mapState(String state) {
        if (state == null) return ConnectionState.UNKNOWN;
        return switch (state.toUpperCase().replace("-", "_")) {
            case "LISTEN" -> ConnectionState.LISTEN;
            case "SYN_SENT" -> ConnectionState.SYN_SENT;
            case "SYN_RECV", "SYN_RECEIVED" -> ConnectionState.SYN_RECEIVED;
            case "ESTABLISHED" -> ConnectionState.ESTABLISHED;
            case "FIN_WAIT1", "FIN_WAIT_1" -> ConnectionState.FIN_WAIT_1;
            case "FIN_WAIT2", "FIN_WAIT_2" -> ConnectionState.FIN_WAIT_2;
            case "CLOSE_WAIT" -> ConnectionState.CLOSE_WAIT;
            case "CLOSING" -> ConnectionState.CLOSING;
            case "LAST_ACK" -> ConnectionState.LAST_ACK;
            case "TIME_WAIT" -> ConnectionState.TIME_WAIT;
            case "CLOSED" -> ConnectionState.CLOSED;
            default -> ConnectionState.UNKNOWN;
        };
    }

    private static String arrayToIp(byte[] addr) {
        if (addr == null || addr.length == 0) return "0.0.0.0";
        if (addr.length == 4) {
            return (addr[0] & 0xFF) + "." + (addr[1] & 0xFF) + "." + (addr[2] & 0xFF) + "." + (addr[3] & 0xFF);
        }
        // IPv6
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < addr.length; i += 2) {
            if (i > 0) sb.append(':');
            sb.append(String.format("%02x%02x", addr[i] & 0xFF, i + 1 < addr.length ? addr[i + 1] & 0xFF : 0));
        }
        return sb.toString();
    }
}
