package io.joshuasalcedo.os.domain.network;

/**
 * TCP connection states as defined by RFC 793.
 */
public enum ConnectionState implements OSNetworkObject {
    LISTEN,
    SYN_SENT,
    SYN_RECEIVED,
    ESTABLISHED,
    FIN_WAIT_1,
    FIN_WAIT_2,
    CLOSE_WAIT,
    CLOSING,
    LAST_ACK,
    TIME_WAIT,
    CLOSED,
    UNKNOWN
}
