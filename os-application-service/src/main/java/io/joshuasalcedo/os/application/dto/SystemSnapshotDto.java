package io.joshuasalcedo.os.application.dto;

import java.time.Instant;
import java.util.List;

public record SystemSnapshotDto(
    Instant capturedAt,
    String machineId,
    String hostname,
    OperatingSystemInfoDto operatingSystem,
    ComputerSystemDto computerSystem,
    ProcessorDto processor,
    MemoryDto memory,
    List<DiskDto> disks,
    List<GraphicsCardDto> graphicsCards,
    List<NetworkInterfaceDto> networkInterfaces,
    List<SoundCardDto> soundCards,
    List<DisplayDto> displays,
    List<PowerSourceDto> powerSources
) {}
