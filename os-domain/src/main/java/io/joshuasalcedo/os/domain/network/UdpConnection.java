package io.joshuasalcedo.os.domain.network;

import java.util.Objects;

/**
 * Value object representing a UDP listener/endpoint.
 */
public record UdpConnection(
        SocketAddress localAddress,
        long pid,
        String processName
) implements OSNetworkObject {

    public UdpConnection {
        Objects.requireNonNull(localAddress, "Local address must not be null");
    }

    @Override
    public String toString() {
        return String.format("UDP %s (pid=%d %s)",
                localAddress, pid, processName != null ? processName : "");
    }
}
