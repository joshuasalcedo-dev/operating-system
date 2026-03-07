package io.joshuasalcedo.os.domain.network;

import java.util.Objects;

/**
 * Value object representing a port that is actively listening for connections.
 */
public record ListeningPort(
        TransportProtocol protocol,
        SocketAddress bindAddress,
        long pid,
        String processName
) implements OSNetworkObject {

    public ListeningPort {
        Objects.requireNonNull(protocol, "Protocol must not be null");
        Objects.requireNonNull(bindAddress, "Bind address must not be null");
    }

    public int port() {
        return bindAddress.port();
    }

    public boolean isBindAll() {
        return bindAddress.isAny();
    }

    @Override
    public String toString() {
        return String.format("%s %s (pid=%d %s)",
                protocol, bindAddress, pid, processName != null ? processName : "");
    }
}
