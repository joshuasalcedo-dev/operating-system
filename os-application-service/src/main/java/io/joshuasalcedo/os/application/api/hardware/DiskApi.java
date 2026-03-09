package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.hardware.DiskInfo;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for querying disk/storage device information.
 */
interface DiskAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns all physical disks detected on the system. */
    List<DiskInfo> getDisks();

    // ── Disk lookup ───────────────────────────────────────────────────────────

    /** Find a disk by its OS name (e.g. "/dev/sda", "\\\\.\\PHYSICALDRIVE0"). */
    default Optional<DiskInfo> findByName(String name) {
        return getDisks().stream()
                .filter(d -> d.name().equalsIgnoreCase(name))
                .findFirst();
    }

    /** Find a disk by serial number string. */
    default Optional<DiskInfo> findBySerialNumber(String serial) {
        return getDisks().stream()
                .filter(d -> d.serialNumber() != null &&
                             d.serialNumber().toString().equalsIgnoreCase(serial))
                .findFirst();
    }

    /** Find a disk by model name (partial, case-insensitive). */
    default List<DiskInfo> findByModel(String model) {
        return getDisks().stream()
                .filter(d -> d.model().toLowerCase().contains(model.toLowerCase()))
                .toList();
    }

    // ── Capacity ──────────────────────────────────────────────────────────────

    /** Total combined storage capacity of all disks in bytes. */
    default long totalCapacityBytes() {
        return getDisks().stream()
                .mapToLong(DiskInfo::size)
                .sum();
    }

    /** The largest disk by capacity. */
    default Optional<DiskInfo> largestDisk() {
        return getDisks().stream()
                .max(java.util.Comparator.comparingLong(DiskInfo::size));
    }

    /** All disks larger than the given size in bytes. */
    default List<DiskInfo> disksLargerThan(long bytes) {
        return getDisks().stream()
                .filter(d -> d.size() > bytes)
                .toList();
    }

    // ── Partitions ────────────────────────────────────────────────────────────

    /** All partitions across all disks. */
    default List<DiskInfo.PartitionInfo> allPartitions() {
        return getDisks().stream()
                .flatMap(d -> d.partitions().stream())
                .toList();
    }

    /** Find a partition by its mount point (e.g. "/", "C:\\", "/home"). */
    default Optional<DiskInfo.PartitionInfo> findPartitionByMountPoint(String mountPoint) {
        return allPartitions().stream()
                .filter(p -> p.mountPoint().equalsIgnoreCase(mountPoint))
                .findFirst();
    }

    /** Find a partition by UUID. */
    default Optional<DiskInfo.PartitionInfo> findPartitionByUuid(String uuid) {
        return allPartitions().stream()
                .filter(p -> p.uuid().equalsIgnoreCase(uuid))
                .findFirst();
    }

    /** All partitions of a given filesystem type (e.g. "ext4", "NTFS", "apfs"). */
    default List<DiskInfo.PartitionInfo> partitionsByType(String type) {
        return allPartitions().stream()
                .filter(p -> p.type().equalsIgnoreCase(type))
                .toList();
    }

    /** All partitions on a specific disk. */
    default List<DiskInfo.PartitionInfo> partitionsOf(DiskInfo disk) {
        return disk.partitions();
    }

    // ── I/O stats ─────────────────────────────────────────────────────────────

    /** Total bytes read across all disks since boot. */
    default long totalBytesRead() {
        return getDisks().stream()
                .mapToLong(DiskInfo::readBytes)
                .sum();
    }

    /** Total bytes written across all disks since boot. */
    default long totalBytesWritten() {
        return getDisks().stream()
                .mapToLong(DiskInfo::writeBytes)
                .sum();
    }

    /** Disk with the highest total read activity. */
    default Optional<DiskInfo> mostReadDisk() {
        return getDisks().stream()
                .max(java.util.Comparator.comparingLong(DiskInfo::readBytes));
    }

    /** Disk with the highest total write activity. */
    default Optional<DiskInfo> mostWrittenDisk() {
        return getDisks().stream()
                .max(java.util.Comparator.comparingLong(DiskInfo::writeBytes));
    }

    static DiskAPI oshi(oshi.SystemInfo systemInfo) {
        return new OshiDiskAPI(systemInfo);
    }
}