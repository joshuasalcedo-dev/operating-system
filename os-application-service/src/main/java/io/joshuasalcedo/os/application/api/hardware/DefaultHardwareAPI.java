package io.joshuasalcedo.os.application.api.hardware;


import io.joshuasalcedo.os.application.api.HardwareAPI;
import io.joshuasalcedo.os.domain.hardware.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;

import java.util.List;

/**
 * HardwareAPI class.
 *
 * @author JoshuaSalcedo
 * @since 3/9/2026 7:49 AM
 */

@Slf4j
class DefaultHardwareAPI implements HardwareAPI {

	private final DiskAPI diskAPI;
	private final DisplayAPI displayAPI;
	private final GraphicsCardAPI graphicsCardAPI;
	private final MemoryAPI memoryAPI;
	private final NetworkInterfaceAPI networkInterfaceAPI;
	private final ProcessorAPI processorAPI;
	private final SoundCardAPI soundCardAPI;
	private final UsbDeviceAPI usbDeviceAPI;
	private final ComputerSystemAPI computerSystemAPI;

	DefaultHardwareAPI(ComputerSystemAPI computerSystemAPI) {
		this.computerSystemAPI = computerSystemAPI;
		SystemInfo systemInfo = new SystemInfo();
		this.diskAPI = DiskAPI.oshi(systemInfo);
		this.displayAPI = DisplayAPI.oshi(systemInfo);
		 this.graphicsCardAPI = GraphicsCardAPI.oshi(systemInfo);
		 this.memoryAPI = MemoryAPI.oshi(systemInfo);
		 this.networkInterfaceAPI = NetworkInterfaceAPI.oshi(systemInfo);
		 this.processorAPI = ProcessorAPI.oshi(systemInfo);
		 this.soundCardAPI = SoundCardAPI.oshi(systemInfo);
		 this.usbDeviceAPI = UsbDeviceAPI.oshi(systemInfo);
	}






	@Override
	public List<DiskInfo> getDiskInfo() {
		return diskAPI.getDisks();
	}

	@Override
	public List<DisplayInfo> getDisplayInfo() {
		return displayAPI.getDisplays();
	}

	@Override
	public List<GraphicsCardInfo> getGraphicsCardInfo() {
		return graphicsCardAPI.getGraphicsCards();
	}

	@Override
	public MemoryInfo getMemoryInfo() {
		return  memoryAPI.getMemory();
	}

	@Override
	public List<NetworkInterfaceInfo> getNetworkInterfaceInfo() {
		return networkInterfaceAPI.getInterfaces();
	}

	@Override
	public List<ProcessorInfo> getProcessors() {
		return processorAPI.getProcessors();
	}

	@Override
	public List<ProcessorInfo.CacheInfo> getCaches(ProcessorInfo processor, int level) {
		return processorAPI.getCaches(processor, level);
	}

	@Override
	public List<SoundCardInfo> getSoundCardInfo() {
		return soundCardAPI.getSoundCards();
	}

	@Override
	public List<UsbDeviceInfo> getUsbDeviceInfo() {
		return usbDeviceAPI.getRootDevices();
	}


	@Override
	public ComputerSystemInfo getComputerSystemInfo() {
		return computerSystemAPI.getComputerSystem();
	}
}