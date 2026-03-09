package io.joshuasalcedo.os.application.api;

import io.joshuasalcedo.os.domain.hardware.*;
import java.util.List;

/**
 * HardwareAPI class.
 *
 * @author JoshuaSalcedo
 * @since 3/9/2026 7:37 AM
 */

public interface HardwareAPI {

	List<DiskInfo> getDiskInfo();
	List<DisplayInfo> getDisplayInfo();
	List<GraphicsCardInfo> getGraphicsCardInfo();
	MemoryInfo getMemoryInfo();
	List<NetworkInterfaceInfo> getNetworkInterfaceInfo();
	List<ProcessorInfo> getProcessors();

	/** Returns caches for a given processor filtered by cache level (1, 2, 3). */
	List<ProcessorInfo.CacheInfo> getCaches(ProcessorInfo processor, int level);
	List<SoundCardInfo> getSoundCardInfo();
	List<UsbDeviceInfo> getUsbDeviceInfo();
	ComputerSystemInfo getComputerSystemInfo();
}
