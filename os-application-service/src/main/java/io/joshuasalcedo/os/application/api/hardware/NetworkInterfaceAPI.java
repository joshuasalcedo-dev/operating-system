package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.hardware.NetworkInterfaceInfo;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for querying network interface information.
 */
interface NetworkInterfaceAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns all network interfaces detected on the system. */
    List<NetworkInterfaceInfo> getInterfaces();

    // ── Lookup ────────────────────────────────────────────────────────────────

    /** Find an interface by its OS name (e.g. "eth0", "en0", "Ethernet"). */
    default Optional<NetworkInterfaceInfo> findByName(String name) {
        return getInterfaces().stream()
                .filter(i -> i.name().equalsIgnoreCase(name))
                .findFirst();
    }

    /** Find an interface by MAC address (case-insensitive, any separator style). */
    default Optional<NetworkInterfaceInfo> findByMac(String mac) {
        String normalized = mac.replaceAll("[:\\-]", "").toLowerCase();
        return getInterfaces().stream()
                .filter(i -> i.macAddress() != null &&
                             i.macAddress().replaceAll("[:\\-]", "").equalsIgnoreCase(normalized))
                .findFirst();
    }

    /** All interfaces that have at least one assigned IPv4 address. */
    default List<NetworkInterfaceInfo> withIPv4() {
        return getInterfaces().stream()
                .filter(i -> !i.ipv4Addresses().isEmpty())
                .toList();
    }

    /** All interfaces that have at least one assigned IPv6 address. */
    default List<NetworkInterfaceInfo> withIPv6() {
        return getInterfaces().stream()
                .filter(i -> !i.ipv6Addresses().isEmpty())
                .toList();
    }

    // ── Connectivity ──────────────────────────────────────────────────────────

    /** Interfaces that appear to be active (speed > 0 and have an IP address). */
    default List<NetworkInterfaceInfo> activeInterfaces() {
        return getInterfaces().stream()
                .filter(NetworkInterfaceInfo::isActive)
                .toList();
    }

    /** Interfaces that appear to be loopback (name starts with "lo" or IP is 127.x). */
    default List<NetworkInterfaceInfo> loopbackInterfaces() {
        return getInterfaces().stream()
                .filter(NetworkInterfaceInfo::isLoopback)
                .toList();
    }

    /** Non-loopback interfaces only. */
    default List<NetworkInterfaceInfo> physicalInterfaces() {
        return getInterfaces().stream()
                .filter(i -> !i.isLoopback())
                .toList();
    }

    // ── Speed ─────────────────────────────────────────────────────────────────

    /** Interfaces running at or above a given speed in bits per second. */
    default List<NetworkInterfaceInfo> interfacesAtLeast(long bitsPerSecond) {
        return getInterfaces().stream()
                .filter(i -> i.speed() >= bitsPerSecond)
                .toList();
    }

    /** Interfaces running at gigabit (1 Gbps) or faster. */
    default List<NetworkInterfaceInfo> gigabitInterfaces() {
        return getInterfaces().stream()
                .filter(NetworkInterfaceInfo::isGigabit)
                .toList();
    }

    /** The fastest interface by advertised speed. */
    default Optional<NetworkInterfaceInfo> fastestInterface() {
        return getInterfaces().stream()
                .max(java.util.Comparator.comparingLong(NetworkInterfaceInfo::speed));
    }

    // ── Traffic ───────────────────────────────────────────────────────────────

    /** Total bytes received across all interfaces since boot. */
    default long totalBytesReceived() {
        return getInterfaces().stream()
                .mapToLong(NetworkInterfaceInfo::bytesReceived)
                .sum();
    }

    /** Total bytes sent across all interfaces since boot. */
    default long totalBytesSent() {
        return getInterfaces().stream()
                .mapToLong(NetworkInterfaceInfo::bytesSent)
                .sum();
    }

    /** Total inbound errors across all interfaces. */
    default long totalInErrors() {
        return getInterfaces().stream()
                .mapToLong(NetworkInterfaceInfo::inErrors)
                .sum();
    }

    /** Total outbound errors across all interfaces. */
    default long totalOutErrors() {
        return getInterfaces().stream()
                .mapToLong(NetworkInterfaceInfo::outErrors)
                .sum();
    }

    /** True if any interface is reporting errors (useful for health checks). */
    default boolean hasErrors() {
        return getInterfaces().stream().anyMatch(NetworkInterfaceInfo::hasErrors);
    }

    /** Interface with the most received traffic. */
    default Optional<NetworkInterfaceInfo> busiestReceiver() {
        return getInterfaces().stream()
                .max(java.util.Comparator.comparingLong(NetworkInterfaceInfo::bytesReceived));
    }

    /** Interface with the most sent traffic. */
    default Optional<NetworkInterfaceInfo> busiestSender() {
        return getInterfaces().stream()
                .max(java.util.Comparator.comparingLong(NetworkInterfaceInfo::bytesSent));
    }

    static NetworkInterfaceAPI oshi(oshi.SystemInfo systemInfo) {
        return new OshiNetworkInterfaceAPI(systemInfo);
    }
}