package io.joshuasalcedo.os.domain.network;

/**
 * Transport-layer protocol type.
 */
public enum TransportProtocol implements OSNetworkObject {
    TCP,
    TCP6,
    UDP,
    UDP6
}
