package io.joshuasalcedo.os.domain.computer;


/**
 * NonPhysicalMachineException class.
 * @author JoshuaSalcedo
 * @since 3/7/2026 11:32 PM
 */
public class NonPhysicalMachineException extends RuntimeException {

    private final OSEnvironment detectedOSEnvironment;

    public NonPhysicalMachineException(OSEnvironment detectedOSEnvironment) {
        super(messageFor(detectedOSEnvironment));
        this.detectedOSEnvironment = detectedOSEnvironment;
    }

    private static String messageFor(OSEnvironment type) {
        return switch (type) {
            case PHYSICAL -> "Machine is physical — this exception should not have been thrown.";
            case VIRTUAL_MACHINE -> "Operation requires a physical machine, but a virtual machine was detected. " +
                    "Hardware-level identifiers (DMI serials, UUIDs) may be spoofed or absent in VM environments.";
            case CONTAINER -> "Operation requires a physical machine, but a container environment was detected. " +
                    "Containers do not have direct access to host hardware — DMI, serial numbers, and UUIDs are unavailable.";
            case WSL -> "Operation requires a physical machine, but Windows Subsystem for Linux (WSL) was detected. " +
                    "WSL shares the Windows host hardware but exposes a synthetic Linux kernel — " +
                    "use the Windows-side APIs for reliable hardware identification.";
            case UNKNOWN -> "Operation requires a physical machine, but the environment could not be determined. " +
                    "This may be a container, VM, or an unrecognized virtualization layer.";
        };
    }

    public OSEnvironment getDetectedOSEnvironment() {
        return detectedOSEnvironment;
    }
}