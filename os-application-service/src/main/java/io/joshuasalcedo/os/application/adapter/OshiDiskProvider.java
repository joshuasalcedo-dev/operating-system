package io.joshuasalcedo.os.application.adapter;

import io.joshuasalcedo.os.domain.SerialNumber;
import io.joshuasalcedo.os.domain.hardware.DiskInfo;
import io.joshuasalcedo.os.domain.hardware.HardwarePort;
import oshi.SystemInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OshiDiskProvider implements HardwarePort.DiskProvider {

    private final SystemInfo systemInfo;

    public OshiDiskProvider(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<DiskInfo> provide() {
        return systemInfo.getHardware().getDiskStores().stream()
                .map(disk -> {
                    List<DiskInfo.PartitionInfo> partitions = disk.getPartitions().stream()
                            .map(p -> new DiskInfo.PartitionInfo(
                                    p.getIdentification(),
                                    p.getName(),
                                    p.getType(),
                                    p.getUuid(),
                                    p.getSize(),
                                    p.getMajor(),
                                    p.getMinor(),
                                    p.getMountPoint()
                            ))
                            .toList();
                    return new DiskInfo(
                            disk.getName(),
                            disk.getModel(),
                            SerialNumber.of(disk.getSerial()),
                            disk.getSize(),
                            disk.getReads(),
                            disk.getWrites(),
                            disk.getReadBytes(),
                            disk.getWriteBytes(),
                            disk.getTransferTime(),
                            partitions
                    );
                })
                .toList();
    }
}
