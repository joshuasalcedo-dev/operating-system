package io.joshuasalcedo.os.application.service;

import io.joshuasalcedo.os.application.dto.*;
import io.joshuasalcedo.os.domain.DomainPort;
import io.joshuasalcedo.os.domain.MachineId;
import io.joshuasalcedo.os.domain.SystemSnapshot;
import io.joshuasalcedo.os.domain.hardware.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemSnapshotService {

    private final DomainPort.SystemSnapshotProvider snapshotProvider;
    private final DomainPort.MachineIdProvider machineIdProvider;
    private final HardwarePort.ProcessorProvider processorProvider;
    private final HardwarePort.MemoryProvider memoryProvider;
    private final HardwarePort.DiskProvider diskProvider;
    private final HardwarePort.GraphicsCardProvider graphicsCardProvider;
    private final HardwarePort.OperatingSystemInfoProvider osInfoProvider;

    public SystemSnapshotService(
            DomainPort.SystemSnapshotProvider snapshotProvider,
            DomainPort.MachineIdProvider machineIdProvider,
            HardwarePort.ProcessorProvider processorProvider,
            HardwarePort.MemoryProvider memoryProvider,
            HardwarePort.DiskProvider diskProvider,
            HardwarePort.GraphicsCardProvider graphicsCardProvider,
            HardwarePort.OperatingSystemInfoProvider osInfoProvider
    ) {
        this.snapshotProvider = snapshotProvider;
        this.machineIdProvider = machineIdProvider;
        this.processorProvider = processorProvider;
        this.memoryProvider = memoryProvider;
        this.diskProvider = diskProvider;
        this.graphicsCardProvider = graphicsCardProvider;
        this.osInfoProvider = osInfoProvider;
    }

    public SystemSnapshotDto getFullSnapshot() {
        SystemSnapshot snapshot = snapshotProvider.provide();
        return DtoMapper.toDto(snapshot);
    }

    public ProcessorDto getProcessor() {
        return DtoMapper.toDto(processorProvider.provide());
    }

    public MemoryDto getMemory() {
        return DtoMapper.toDto(memoryProvider.provide());
    }

    public List<DiskDto> getDisks() {
        return diskProvider.provide().stream()
                .map(DtoMapper::toDto)
                .toList();
    }

    public List<GraphicsCardDto> getGraphicsCards() {
        return graphicsCardProvider.provide().stream()
                .map(DtoMapper::toDto)
                .toList();
    }

    public OperatingSystemInfoDto getOperatingSystemInfo() {
        return DtoMapper.toDto(osInfoProvider.provide());
    }

    public String getMachineId() {
        return machineIdProvider.provide().value();
    }
}
