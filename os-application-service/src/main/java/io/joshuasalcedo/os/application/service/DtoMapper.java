package io.joshuasalcedo.os.application.service;

import io.joshuasalcedo.os.application.dto.*;
import io.joshuasalcedo.os.domain.SystemSnapshot;
import io.joshuasalcedo.os.domain.hardware.*;

import java.util.List;

public final class DtoMapper {

    private DtoMapper() {}

    public static SystemSnapshotDto toDto(SystemSnapshot s) {
        return new SystemSnapshotDto(
                s.capturedAt(),
                s.machineId().value(),
                s.hostname(),
                toDto(s.operatingSystem()),
                toDto(s.computerSystem()),
                toDto(s.processor()),
                toDto(s.memory()),
                s.disks().stream().map(DtoMapper::toDto).toList(),
                s.graphicsCards().stream().map(DtoMapper::toDto).toList(),
                s.networkInterfaces().stream().map(DtoMapper::toDto).toList(),
                s.soundCards().stream().map(DtoMapper::toDto).toList(),
                s.displays().stream().map(DtoMapper::toDto).toList(),
                s.powerSources().stream().map(DtoMapper::toDto).toList()
        );
    }

    public static ProcessorDto toDto(ProcessorInfo p) {
        return new ProcessorDto(
                p.name(),
                p.manufacturer().name(),
                p.identifier(),
                p.processorId(),
                p.microarchitecture(),
                p.physicalPackages(),
                p.physicalCores(),
                p.logicalProcessors(),
                p.vendorFrequencyFormatted(),
                p.maxFrequencyFormatted()
        );
    }

    public static MemoryDto toDto(MemoryInfo m) {
        return new MemoryDto(
                m.totalPhysical(),
                m.availablePhysical(),
                m.usedPhysical(),
                m.usagePercent(),
                m.totalFormatted(),
                m.availableFormatted(),
                m.swapTotal(),
                m.swapUsed()
        );
    }

    public static ComputerSystemDto toDto(ComputerSystemInfo c) {
        return new ComputerSystemDto(
                c.manufacturer().name(),
                c.model(),
                c.serialNumber().value(),
                c.uuid(),
                c.firmware().manufacturer().name(),
                c.firmware().name(),
                c.firmware().version(),
                c.baseboard().manufacturer().name(),
                c.baseboard().model(),
                c.baseboard().version()
        );
    }

    public static OperatingSystemInfoDto toDto(OperatingSystemInfo o) {
        return new OperatingSystemInfoDto(
                o.family(),
                o.manufacturer().name(),
                o.versionInfo(),
                o.buildNumber(),
                o.bitness(),
                o.processCount(),
                o.threadCount(),
                o.bootTime(),
                o.uptimeFormatted(),
                o.elevated()
        );
    }

    public static DiskDto toDto(DiskInfo d) {
        List<String> partitions = d.partitions().stream()
                .map(p -> p.identification() + " " + p.name() + " " + p.sizeFormatted() + " " + p.mountPoint())
                .toList();
        return new DiskDto(
                d.name(),
                d.model(),
                d.serialNumber().value(),
                d.sizeFormatted(),
                d.reads(),
                d.writes(),
                d.readBytes(),
                d.writeBytes(),
                partitions
        );
    }

    public static GraphicsCardDto toDto(GraphicsCardInfo g) {
        return new GraphicsCardDto(
                g.name(),
                g.deviceId(),
                g.manufacturer().name(),
                g.versionInfo(),
                g.vRamFormatted()
        );
    }

    public static NetworkInterfaceDto toDto(NetworkInterfaceInfo n) {
        return new NetworkInterfaceDto(
                n.name(),
                n.displayName(),
                n.macAddress(),
                n.speedFormatted(),
                n.ipv4Addresses(),
                n.ipv6Addresses(),
                n.bytesReceived(),
                n.bytesSent()
        );
    }

    public static SoundCardDto toDto(SoundCardInfo s) {
        return new SoundCardDto(
                s.name(),
                s.codec(),
                s.driverVersion()
        );
    }

    public static DisplayDto toDto(DisplayInfo d) {
        return new DisplayDto(
                d.index(),
                d.edid()
        );
    }

    public static PowerSourceDto toDto(PowerSourceInfo p) {
        return new PowerSourceDto(
                p.name(),
                p.manufacturer().name(),
                p.chemistry(),
                p.remainingFormatted(),
                p.timeRemainingFormatted(),
                p.charging(),
                p.powerOnLine()
        );
    }
}
