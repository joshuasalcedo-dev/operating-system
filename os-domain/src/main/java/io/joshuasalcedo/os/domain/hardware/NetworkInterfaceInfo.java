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

    /** True if this interface appears active (speed > 0 and has an IP address). */
    public boolean isActive() {
        return speed > 0 && !ipv4Addresses.isEmpty();
    }

    /** True if this interface is a loopback interface. */
    public boolean isLoopback() {
        return name.startsWith("lo") ||
               ipv4Addresses.stream().anyMatch(ip -> ip.startsWith("127."));
    }

    /** True if this interface is running at gigabit (1 Gbps) or faster. */
    public boolean isGigabit() {
        return speed >= 1_000_000_000L;
    }

    /** True if this interface has any inbound or outbound errors. */
    public boolean hasErrors() {
        return inErrors > 0 || outErrors > 0;
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