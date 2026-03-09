package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.Manufacturer;
import io.joshuasalcedo.os.domain.SerialNumber;
import io.joshuasalcedo.os.domain.hardware.UsbDeviceInfo;
import oshi.SystemInfo;
import oshi.hardware.UsbDevice;

import java.util.List;

final class OshiUsbDeviceAPI implements UsbDeviceAPI {

    private final SystemInfo systemInfo;

    OshiUsbDeviceAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<UsbDeviceInfo> getRootDevices() {
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
