package io.joshuasalcedo.os.domain.hardware;

import io.joshuasalcedo.os.domain.SerialNumber;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Value object representing a physical disk/storage device.
 */
public record DiskInfo(
        String name,
        String model,
        SerialNumber serialNumber,
        long size,
        long reads,
        long writes,
        long readBytes,
        long writeBytes,
        long transferTime,
        List<PartitionInfo> partitions
) implements OSHardwareObject {

    public DiskInfo {
        Objects.requireNonNull(name, "Disk name must not be null");
        partitions = partitions != null ? Collections.unmodifiableList(partitions) : List.of();
    }

    public String sizeFormatted() {
        return formatBytes(size);
    }

    /**
     * Value object for disk partitions.
     */
    public record PartitionInfo(
            String identification,
            String name,
            String type,
            String uuid,
            long size,
            int major,
            int minor,
            String mountPoint
    ) {
        public String sizeFormatted() {
            return DiskInfo.formatBytes(size);
        }

        @Override
        public String toString() {
            return String.format("%s: %s (%s) %s mounted at %s",
                    identification, name, type, sizeFormatted(), mountPoint);
        }
    }

    private static String formatBytes(long bytes) {
        if (bytes >= 1_099_511_627_776L) {
            return String.format(Locale.ROOT, "%.1f TB", bytes / 1_099_511_627_776.0);
        } else if (bytes >= 1_073_741_824L) {
            return String.format(Locale.ROOT, "%.1f GB", bytes / 1_073_741_824.0);
        } else if (bytes >= 1_048_576L) {
            return String.format(Locale.ROOT, "%.1f MB", bytes / 1_048_576.0);
        }
        return String.format(Locale.ROOT, "%d KB", bytes / 1024);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) %s [%d partitions]",
                model, name, sizeFormatted(), partitions.size());
    }
}