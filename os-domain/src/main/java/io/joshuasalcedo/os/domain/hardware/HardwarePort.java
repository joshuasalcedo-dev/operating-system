package io.joshuasalcedo.os.domain.hardware;

import java.util.List;

/**
 * Sealed port interface for the hardware package.
 * Defines the provider contracts for hardware information objects.
 *
 * @author JoshuaSalcedo
 * @since 3/7/2026
 */
public sealed interface HardwarePort permits
	HardwarePort.ComputerSystemProvider,
	HardwarePort.ProcessorProvider,
	HardwarePort.MemoryProvider,
	HardwarePort.DiskProvider,
	HardwarePort.GraphicsCardProvider,
	HardwarePort.NetworkInterfaceProvider,
	HardwarePort.SoundCardProvider,
	HardwarePort.DisplayProvider,
	HardwarePort.PowerSourceProvider,
	HardwarePort.UsbDeviceProvider,
	HardwarePort.OperatingSystemInfoProvider
	{

	non-sealed interface ComputerSystemProvider extends HardwarePort {
		ComputerSystemInfo provide();
	}

	non-sealed interface ProcessorProvider extends HardwarePort {
		ProcessorInfo provide();
	}

	non-sealed interface MemoryProvider extends HardwarePort {
		MemoryInfo provide();
	}

	non-sealed interface DiskProvider extends HardwarePort {
		List<DiskInfo> provide();
	}

	non-sealed interface GraphicsCardProvider extends HardwarePort {
		List<GraphicsCardInfo> provide();
	}

	non-sealed interface NetworkInterfaceProvider extends HardwarePort {
		List<NetworkInterfaceInfo> provide();
	}

	non-sealed interface SoundCardProvider extends HardwarePort {
		List<SoundCardInfo> provide();
	}

	non-sealed interface DisplayProvider extends HardwarePort {
		List<DisplayInfo> provide();
	}

	non-sealed interface PowerSourceProvider extends HardwarePort {
		List<PowerSourceInfo> provide();
	}

	non-sealed interface UsbDeviceProvider extends HardwarePort {
		List<UsbDeviceInfo> provide();
	}

	non-sealed interface OperatingSystemInfoProvider extends HardwarePort {
		OperatingSystemInfo provide();
	}
}
