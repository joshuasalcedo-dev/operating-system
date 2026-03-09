package io.joshuasalcedo.os.application.api.network;

import io.joshuasalcedo.os.domain.network.DnsConfiguration;
import oshi.SystemInfo;
import oshi.software.os.NetworkParams;

import java.util.List;

final class OshiDnsConfigurationAPI implements DnsConfigurationAPI {

    private final SystemInfo systemInfo;

    OshiDnsConfigurationAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public DnsConfiguration getDnsConfiguration() {
        NetworkParams np = systemInfo.getOperatingSystem().getNetworkParams();
        return new DnsConfiguration(
                np.getHostName(),
                np.getDomainName(),
                List.of(np.getDnsServers()),
                List.of()
        );
    }
}
