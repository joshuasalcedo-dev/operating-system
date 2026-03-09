package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.hardware.NetworkInterfaceInfo;
import oshi.SystemInfo;

import java.util.List;

final class OshiNetworkInterfaceAPI implements NetworkInterfaceAPI {

    private final SystemInfo systemInfo;

    OshiNetworkInterfaceAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<NetworkInterfaceInfo> getInterfaces() {
        return systemInfo.getHardware().getNetworkIFs().stream()
                .map(ni -> new NetworkInterfaceInfo(
                        ni.getName(),
                        ni.getDisplayName(),
                        ni.getMacaddr(),
                        ni.getSpeed(),
                        ni.getMTU(),
                        List.of(ni.getIPv4addr()),
                        List.of(ni.getIPv6addr()),
                        ni.getBytesRecv(),
                        ni.getBytesSent(),
                        ni.getPacketsRecv(),
                        ni.getPacketsSent(),
                        ni.getInErrors(),
                        ni.getOutErrors()
                ))
                .toList();
    }
}
