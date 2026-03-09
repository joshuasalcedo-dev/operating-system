package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.SerialNumber;
import io.joshuasalcedo.os.domain.hardware.DiskInfo;
import oshi.SystemInfo;

import java.util.List;

final class OshiDiskAPI implements DiskAPI {

    private final SystemInfo systemInfo;

    OshiDiskAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<DiskInfo> getDisks() {
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
