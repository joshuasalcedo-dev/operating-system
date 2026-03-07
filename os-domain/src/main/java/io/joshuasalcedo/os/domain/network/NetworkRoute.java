package io.joshuasalcedo.os.domain.network;

import java.util.Objects;

/**
 * Value object representing a network routing table entry.
 */
public record NetworkRoute(
        String destination,
        String gateway,
        String netmask,
        String interfaceName,
        int metric
) implements OSNetworkObject {

    public NetworkRoute {
        Objects.requireNonNull(destination, "Destination must not be null");
    }

    public boolean isDefaultRoute() {
        return "0.0.0.0".equals(destination) || "default".equals(destination) || "::".equals(destination);
    }

    @Override
    public String toString() {
        return String.format("%s via %s dev %s metric %d",
                destination, gateway != null ? gateway : "*", interfaceName, metric);
    }
}
