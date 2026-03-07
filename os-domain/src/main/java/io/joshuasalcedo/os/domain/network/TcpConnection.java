package io.joshuasalcedo.os.domain.network;

import java.util.Objects;

/**
 * Value object representing an active TCP connection.
 */
public record TcpConnection(
        SocketAddress localAddress,
        SocketAddress remoteAddress,
        ConnectionState state,
        long pid,
        String processName
) implements OSNetworkObject {

    public TcpConnection {
        Objects.requireNonNull(localAddress, "Local address must not be null");
        Objects.requireNonNull(remoteAddress, "Remote address must not be null");
        Objects.requireNonNull(state, "Connection state must not be null");
    }

    public boolean isListening() {
        return state == ConnectionState.LISTEN;
    }

    public boolean isEstablished() {
        return state == ConnectionState.ESTABLISHED;
    }

    @Override
    public String toString() {
        return String.format("TCP %s -> %s %s (pid=%d %s)",
                localAddress, remoteAddress, state, pid, processName != null ? processName : "");
    }
}
