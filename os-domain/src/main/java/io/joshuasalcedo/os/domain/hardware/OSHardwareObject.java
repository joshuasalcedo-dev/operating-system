package io.joshuasalcedo.os.domain.hardware;

/**
 * Sealed interface for all hardware-related operating system value objects.
 *
 * @author JoshuaSalcedo
 * @since 3/7/2026
 */
public sealed interface OSHardwareObject permits
	ComputerSystemInfo,
	DeviceStatus,
	DeviceType,
	DiskInfo,
	DisplayInfo,
	GraphicsCardInfo,
	MemoryInfo,
	NetworkInterfaceInfo,
	OperatingSystemInfo,
	PowerSourceInfo,
	ProcessorInfo,
	SoundCardInfo,
	UsbDeviceInfo
	{
}
