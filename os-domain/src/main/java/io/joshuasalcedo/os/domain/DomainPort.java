package io.joshuasalcedo.os.domain;

/**
 * Sealed port interface for the core OS domain package.
 * Defines the provider contracts for core domain objects.
 *
 * @author JoshuaSalcedo
 * @since 3/7/2026
 */
public sealed interface DomainPort permits
	DomainPort.SystemSnapshotProvider,
	DomainPort.MachineIdProvider
	{

	non-sealed interface SystemSnapshotProvider extends DomainPort {
		SystemSnapshot provide();
	}

	non-sealed interface MachineIdProvider extends DomainPort {
		MachineId provide();
	}
}
