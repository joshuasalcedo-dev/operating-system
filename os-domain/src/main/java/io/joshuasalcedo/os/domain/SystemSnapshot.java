package io.joshuasalcedo.os.domain;

import io.joshuasalcedo.os.domain.hardware.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate value object representing a complete system hardware snapshot.
 * This is the root of the hardware value object graph — a point-in-time capture.
 */
public record SystemSnapshot(
        Instant capturedAt,
        MachineId machineId,
        String hostname,
        OperatingSystemInfo operatingSystem,
        ComputerSystemInfo computerSystem,
        ProcessorInfo processor,
        MemoryInfo memory,
        List<DiskInfo> disks,
        List<GraphicsCardInfo> graphicsCards,
        List<NetworkInterfaceInfo> networkInterfaces,
        List<SoundCardInfo> soundCards,
        List<UsbDeviceInfo> usbDevices,
        List<DisplayInfo> displays,
        List<PowerSourceInfo> powerSources
) implements OperatingSystemObject {

    public SystemSnapshot {
        Objects.requireNonNull(capturedAt, "Capture timestamp must not be null");
        Objects.requireNonNull(machineId, "Machine ID must not be null");
        Objects.requireNonNull(processor, "Processor info must not be null");
        Objects.requireNonNull(memory, "Memory info must not be null");
        disks = disks != null ? Collections.unmodifiableList(disks) : List.of();
        graphicsCards = graphicsCards != null ? Collections.unmodifiableList(graphicsCards) : List.of();
        networkInterfaces = networkInterfaces != null ? Collections.unmodifiableList(networkInterfaces) : List.of();
        soundCards = soundCards != null ? Collections.unmodifiableList(soundCards) : List.of();
        usbDevices = usbDevices != null ? Collections.unmodifiableList(usbDevices) : List.of();
        displays = displays != null ? Collections.unmodifiableList(displays) : List.of();
        powerSources = powerSources != null ? Collections.unmodifiableList(powerSources) : List.of();
    }

    @Override
    public String toString() {
        return String.format("SystemSnapshot[%s / %s @ %s] CPU=%s, RAM=%s, %d disks, %d GPUs",
                machineId, hostname, capturedAt, processor.name(), memory.totalFormatted(),
                disks.size(), graphicsCards.size());
    }
}