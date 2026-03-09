package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.hardware.UsbDeviceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for querying USB device information.
 * USB devices form a tree — root devices may have connected children.
 */
interface UsbDeviceAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns all root-level USB devices (hubs and top-level devices). */
    List<UsbDeviceInfo> getRootDevices();

    // ── Tree traversal ────────────────────────────────────────────────────────

    /** Returns every USB device in the tree, flattened (depth-first). */
    default List<UsbDeviceInfo> allDevices() {
        List<UsbDeviceInfo> result = new ArrayList<>();
        getRootDevices().forEach(d -> collectAll(d, result));
        return result;
    }

    private static void collectAll(UsbDeviceInfo device, List<UsbDeviceInfo> accumulator) {
        accumulator.add(device);
        device.connectedDevices().forEach(child -> collectAll(child, accumulator));
    }

    /** Total count of all USB devices in the tree (including nested). */
    default int totalDeviceCount() {
        return allDevices().size();
    }

    // ── Lookup ────────────────────────────────────────────────────────────────

    /** Find a device by its name (case-insensitive, partial match). */
    default Optional<UsbDeviceInfo> findByName(String name) {
        return allDevices().stream()
                .filter(d -> d.name().toLowerCase().contains(name.toLowerCase()))
                .findFirst();
    }

    /** Find a device by vendor ID. */
    default List<UsbDeviceInfo> findByVendorId(String vendorId) {
        return allDevices().stream()
                .filter(d -> vendorId.equalsIgnoreCase(d.vendorId()))
                .toList();
    }

    /** Find a device by product ID. */
    default List<UsbDeviceInfo> findByProductId(String productId) {
        return allDevices().stream()
                .filter(d -> productId.equalsIgnoreCase(d.productId()))
                .toList();
    }

    /** Find a device by both vendor ID and product ID (uniquely identifies a device model). */
    default Optional<UsbDeviceInfo> findByVidPid(String vendorId, String productId) {
        return allDevices().stream()
                .filter(d -> vendorId.equalsIgnoreCase(d.vendorId()) &&
                             productId.equalsIgnoreCase(d.productId()))
                .findFirst();
    }

    /** Find a device by its unique device ID. */
    default Optional<UsbDeviceInfo> findByUniqueId(String uniqueDeviceId) {
        return allDevices().stream()
                .filter(d -> uniqueDeviceId.equalsIgnoreCase(d.uniqueDeviceId()))
                .findFirst();
    }

    /** Find a device by serial number string. */
    default Optional<UsbDeviceInfo> findBySerialNumber(String serial) {
        return allDevices().stream()
                .filter(d -> d.serialNumber() != null &&
                             d.serialNumber().toString().equalsIgnoreCase(serial))
                .findFirst();
    }

    // ── Filtering ─────────────────────────────────────────────────────────────

    /** All devices from a specific manufacturer. */
    default List<UsbDeviceInfo> byManufacturer(String manufacturer) {
        return allDevices().stream()
                .filter(d -> d.manufacturer() != null &&
                             d.manufacturer().toString().toLowerCase()
                                            .contains(manufacturer.toLowerCase()))
                .toList();
    }

    /** Devices that have at least one child device connected (i.e. hubs). */
    default List<UsbDeviceInfo> hubs() {
        return allDevices().stream()
                .filter(UsbDeviceInfo::isHub)
                .toList();
    }

    /** Leaf devices — those with no children (actual peripherals, not hubs). */
    default List<UsbDeviceInfo> leafDevices() {
        return allDevices().stream()
                .filter(UsbDeviceInfo::isLeafDevice)
                .toList();
    }

    // ── Presence check ────────────────────────────────────────────────────────

    /** True if any device with the given vendor ID is currently connected. */
    default boolean isVendorConnected(String vendorId) {
        return allDevices().stream().anyMatch(d -> vendorId.equalsIgnoreCase(d.vendorId()));
    }

    /** True if any device with the given vendor + product ID combo is connected. */
    default boolean isDeviceConnected(String vendorId, String productId) {
        return findByVidPid(vendorId, productId).isPresent();
    }

    static UsbDeviceAPI oshi(oshi.SystemInfo systemInfo) {
        return new OshiUsbDeviceAPI(systemInfo);
    }
}