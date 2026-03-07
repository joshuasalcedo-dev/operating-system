package io.joshuasalcedo.os.domain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Value object representing a stable machine identifier.
 * Resolution chain: /etc/machine-id → Windows registry → macOS ioreg → OSHI UUID → UNKNOWN.
 */
public record MachineId(String value) implements OperatingSystemObject {

    public static final MachineId UNKNOWN = new MachineId("Unknown");

    public MachineId {
        Objects.requireNonNull(value, "Machine ID must not be null");
        value = value.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Machine ID must not be blank");
        }
    }

    public static MachineId of(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }
        return new MachineId(value);
    }

    /**
     * Resolves the machine ID using a platform-specific chain:
     * <ol>
     *   <li>Linux: {@code /etc/machine-id}</li>
     *   <li>Windows: {@code MachineGuid} from the registry</li>
     *   <li>macOS: {@code IOPlatformUUID} from ioreg</li>
     *   <li>OSHI {@code hardwareUuid} fallback</li>
     *   <li>{@link #UNKNOWN} if all fail</li>
     * </ol>
     *
     * @param hardwareUuid the OSHI-provided hardware UUID (may be null or "unknown")
     * @return a resolved {@code MachineId}, never null
     */
    public static MachineId resolve(String hardwareUuid) {
        String os = System.getProperty("os.name", "").toLowerCase();

        // 1. Linux: /etc/machine-id
        if (os.contains("linux")) {
            String id = readLinuxMachineId();
            if (id != null) return new MachineId(id);
        }

        // 2. Windows: registry MachineGuid
        if (os.contains("win")) {
            String id = readWindowsMachineGuid();
            if (id != null) return new MachineId(id);
        }

        // 3. macOS: IOPlatformUUID
        if (os.contains("mac")) {
            String id = readMacOsUuid();
            if (id != null) return new MachineId(id);
        }

        // 4. OSHI hardwareUuid fallback
        if (hardwareUuid != null && !hardwareUuid.isBlank() && !"unknown".equalsIgnoreCase(hardwareUuid.trim())) {
            return new MachineId(hardwareUuid.trim());
        }

        // 5. Give up
        return UNKNOWN;
    }

    public boolean isKnown() {
        return !this.equals(UNKNOWN);
    }

    @Override
    public String toString() {
        return value;
    }

    // ── platform helpers ────────────────────────────────────────

    private static String readLinuxMachineId() {
        try {
            Path path = Path.of("/etc/machine-id");
            if (Files.isReadable(path)) {
                String id = Files.readString(path).trim();
                if (!id.isEmpty()) return id;
            }
        } catch (Exception ignored) { }
        return null;
    }

    private static String readWindowsMachineGuid() {
        try {
            Process p = new ProcessBuilder(
                    "reg", "query",
                    "HKLM\\SOFTWARE\\Microsoft\\Cryptography",
                    "/v", "MachineGuid"
            ).redirectErrorStream(true).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("MachineGuid")) {
                        String[] parts = line.trim().split("\\s+");
                        if (parts.length >= 3) {
                            String guid = parts[parts.length - 1].trim();
                            if (!guid.isEmpty()) return guid;
                        }
                    }
                }
            }
        } catch (Exception ignored) { }
        return null;
    }

    private static String readMacOsUuid() {
        try {
            Process p = new ProcessBuilder(
                    "ioreg", "-rd1", "-c", "IOPlatformExpertDevice"
            ).redirectErrorStream(true).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("IOPlatformUUID")) {
                        int eqIdx = line.indexOf('=');
                        if (eqIdx >= 0) {
                            String uuid = line.substring(eqIdx + 1)
                                    .replace("\"", "").trim();
                            if (!uuid.isEmpty()) return uuid;
                        }
                    }
                }
            }
        } catch (Exception ignored) { }
        return null;
    }
}
