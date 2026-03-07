package io.joshuasalcedo.os.application.adapter;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.SerialNumber;
import io.joshuasalcedo.os.domain.hardware.HardwarePort;
import io.joshuasalcedo.os.domain.hardware.UsbDeviceInfo;
import oshi.SystemInfo;
import oshi.hardware.UsbDevice;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OshiUsbDeviceProvider implements HardwarePort.UsbDeviceProvider {

    private final SystemInfo systemInfo;

    public OshiUsbDeviceProvider(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<UsbDeviceInfo> provide() {
        return systemInfo.getHardware().getUsbDevices(true).stream()
                .map(this::mapUsbDevice)
                .toList();
    }

    private UsbDeviceInfo mapUsbDevice(UsbDevice usb) {
        List<UsbDeviceInfo> children = usb.getConnectedDevices().stream()
                .map(this::mapUsbDevice)
                .toList();
        return new UsbDeviceInfo(
                usb.getName(),
                Manufacturer.of(usb.getVendor()),
                SerialNumber.of(usb.getSerialNumber()),
                usb.getVendorId(),
                usb.getProductId(),
                usb.getUniqueDeviceId(),
                children
        );
    }
}
