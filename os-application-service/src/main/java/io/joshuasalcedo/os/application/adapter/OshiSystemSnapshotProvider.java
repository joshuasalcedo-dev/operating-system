package io.joshuasalcedo.os.application.adapter;

import io.joshuasalcedo.os.domain.DomainPort;
import io.joshuasalcedo.os.domain.SystemSnapshot;
import io.joshuasalcedo.os.domain.hardware.HardwarePort;
import oshi.SystemInfo;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OshiSystemSnapshotProvider implements DomainPort.SystemSnapshotProvider {

    private final HardwarePort.ComputerSystemProvider computerSystemProvider;
    private final HardwarePort.ProcessorProvider processorProvider;
    private final HardwarePort.MemoryProvider memoryProvider;
    private final HardwarePort.DiskProvider diskProvider;
    private final HardwarePort.GraphicsCardProvider graphicsCardProvider;
    private final HardwarePort.NetworkInterfaceProvider networkInterfaceProvider;
    private final HardwarePort.SoundCardProvider soundCardProvider;
    private final HardwarePort.DisplayProvider displayProvider;
    private final HardwarePort.PowerSourceProvider powerSourceProvider;
    private final HardwarePort.UsbDeviceProvider usbDeviceProvider;
    private final HardwarePort.OperatingSystemInfoProvider operatingSystemInfoProvider;
    private final DomainPort.MachineIdProvider machineIdProvider;
    private final SystemInfo systemInfo;

    public OshiSystemSnapshotProvider(
            HardwarePort.ComputerSystemProvider computerSystemProvider,
            HardwarePort.ProcessorProvider processorProvider,
            HardwarePort.MemoryProvider memoryProvider,
            HardwarePort.DiskProvider diskProvider,
            HardwarePort.GraphicsCardProvider graphicsCardProvider,
            HardwarePort.NetworkInterfaceProvider networkInterfaceProvider,
            HardwarePort.SoundCardProvider soundCardProvider,
            HardwarePort.DisplayProvider displayProvider,
            HardwarePort.PowerSourceProvider powerSourceProvider,
            HardwarePort.UsbDeviceProvider usbDeviceProvider,
            HardwarePort.OperatingSystemInfoProvider operatingSystemInfoProvider,
            DomainPort.MachineIdProvider machineIdProvider,
            SystemInfo systemInfo
    ) {
        this.computerSystemProvider = computerSystemProvider;
        this.processorProvider = processorProvider;
        this.memoryProvider = memoryProvider;
        this.diskProvider = diskProvider;
        this.graphicsCardProvider = graphicsCardProvider;
        this.networkInterfaceProvider = networkInterfaceProvider;
        this.soundCardProvider = soundCardProvider;
        this.displayProvider = displayProvider;
        this.powerSourceProvider = powerSourceProvider;
        this.usbDeviceProvider = usbDeviceProvider;
        this.operatingSystemInfoProvider = operatingSystemInfoProvider;
        this.machineIdProvider = machineIdProvider;
        this.systemInfo = systemInfo;
    }

    @Override
    public SystemSnapshot provide() {
        return new SystemSnapshot(
                Instant.now(),
                machineIdProvider.provide(),
                systemInfo.getOperatingSystem().getNetworkParams().getHostName(),
                operatingSystemInfoProvider.provide(),
                computerSystemProvider.provide(),
                processorProvider.provide(),
                memoryProvider.provide(),
                diskProvider.provide(),
                graphicsCardProvider.provide(),
                networkInterfaceProvider.provide(),
                soundCardProvider.provide(),
                usbDeviceProvider.provide(),
                displayProvider.provide(),
                powerSourceProvider.provide()
        );
    }
}
