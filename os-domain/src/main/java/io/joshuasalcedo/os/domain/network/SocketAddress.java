package io.joshuasalcedo.os.domain.network;

import java.util.Objects;

/**
 * Value object representing an IP address and port combination.
 */
public record SocketAddress(
        String ip,
        int port
) implements OSNetworkObject {

    public static final SocketAddress ANY_IPV4 = new SocketAddress("0.0.0.0", 0);
    public static final SocketAddress ANY_IPV6 = new SocketAddress("::", 0);

    public SocketAddress {
        Objects.requireNonNull(ip, "IP address must not be null");
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 0 and 65535, got: " + port);
        }
    }

    public boolean isAny() {
        return "0.0.0.0".equals(ip) || "::".equals(ip);
    }

    public boolean isLoopback() {
        return "127.0.0.1".equals(ip) || "::1".equals(ip);
    }

    public boolean isIpv6() {
        return ip.contains(":");
    }

    @Override
    public String toString() {
        return isIpv6() ? "[" + ip + "]:" + port : ip + ":" + port;
    }
}
