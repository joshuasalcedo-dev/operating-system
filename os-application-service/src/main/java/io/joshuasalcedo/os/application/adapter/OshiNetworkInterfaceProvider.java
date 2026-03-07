package io.joshuasalcedo.os.application.adapter;

import io.joshuasalcedo.os.domain.hardware.HardwarePort;
import io.joshuasalcedo.os.domain.hardware.NetworkInterfaceInfo;
import oshi.SystemInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OshiNetworkInterfaceProvider implements HardwarePort.NetworkInterfaceProvider {

    private final SystemInfo systemInfo;

    public OshiNetworkInterfaceProvider(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<NetworkInterfaceInfo> provide() {
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
