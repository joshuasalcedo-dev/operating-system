package io.joshuasalcedo.os.domain;

/**
 * Sealed interface for core operating system domain objects.
 * @author JoshuaSalcedo
 * @since 3/7/2026 7:14 PM
 */
public sealed interface OperatingSystemObject permits
	MachineId,
	Manufacturer,
	SerialNumber,
	SystemSnapshot
	{
}
