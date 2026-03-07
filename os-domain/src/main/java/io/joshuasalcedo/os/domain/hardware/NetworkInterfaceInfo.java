package io.joshuasalcedo.os.domain.hardware;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Value object representing a network interface.
 */
public record NetworkInterfaceInfo(
        String name,
        String displayName,
        String macAddress,
        long speed,
        long mtu,
        List<String> ipv4Addresses,
        List<String> ipv6Addresses,
        long bytesReceived,
        long bytesSent,
        long packetsReceived,
        long packetsSent,
        long inErrors,
        long outErrors
) implements OSHardwareObject {

    public NetworkInterfaceInfo {
        Objects.requireNonNull(name, "Interface name must not be null");
        ipv4Addresses = ipv4Addresses != null ? Collections.unmodifiableList(ipv4Addresses) : List.of();
        ipv6Addresses = ipv6Addresses != null ? Collections.unmodifiableList(ipv6Addresses) : List.of();
    }

    public String speedFormatted() {
        if (speed <= 0) return "N/A";
        if (speed >= 1_000_000_000L) {
            return (speed / 1_000_000_000L) + " Gbps";
        } else if (speed >= 1_000_000L) {
            return (speed / 1_000_000L) + " Mbps";
        }
        return speed + " bps";
    }

    @Override
    public String toString() {
        return String.format("%s (%s) MAC=%s %s",
                name, displayName, macAddress, speedFormatted());
    }
}