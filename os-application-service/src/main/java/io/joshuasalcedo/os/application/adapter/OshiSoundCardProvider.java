package io.joshuasalcedo.os.application.adapter;

import io.joshuasalcedo.os.domain.hardware.HardwarePort;
import io.joshuasalcedo.os.domain.hardware.SoundCardInfo;
import oshi.SystemInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OshiSoundCardProvider implements HardwarePort.SoundCardProvider {

    private final SystemInfo systemInfo;

    public OshiSoundCardProvider(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<SoundCardInfo> provide() {
        return systemInfo.getHardware().getSoundCards().stream()
                .map(sc -> new SoundCardInfo(
                        sc.getName(),
                        sc.getCodec(),
                        sc.getDriverVersion()
                ))
                .toList();
    }
}
