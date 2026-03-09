package io.joshuasalcedo.os.domain.computer;

import io.joshuasalcedo.os.domain.hardware.ComputerSystemInfo;
import io.joshuasalcedo.os.domain.hardware.OperatingSystemInfo;

/**
 * PhysicalComputerId class.
 * @author JoshuaSalcedo
 * @since 3/7/2026 10:20 PM
 */
 
public record PhysicalComputerId(String value) {


	public static  boolean  canCreate(OperatingSystemInfo systemInfo) {
			return OSEnvironment.PHYSICAL.equals(systemInfo.osEnvironment());
	}

	public static PhysicalComputerId from(ComputerSystemInfo computerSystemInfo , OperatingSystemInfo systemInfo) {
		if (!PhysicalComputerId.canCreate(systemInfo)){
				throw new OSException("PhysicalComputerId cannot be created from a VM or container");
		}


		return new PhysicalComputerId(computerSystemInfo.uuid());



	}
}
