package io.joshuasalcedo.os.application.api.os;

import io.joshuasalcedo.os.domain.computer.OSEnvironment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Detects the current OS environment using filesystem probes, environment
 * variables, DMI data, and systemd signals.
 * <p>
 * Detection order matters — WSL must be checked before VIRTUAL_MACHINE
 * because WSL also exposes VM-like DMI strings.
 */
final class OSEnvironmentDetector {

    private final String osFamily;
    private final String kernelBuildNumber;
    private final String sysManufacturer;
    private final String sysModel;
    private final String firmwareManufacturer;
    private final String baseboardManufacturer;

    OSEnvironmentDetector(String osFamily,
                          String kernelBuildNumber,
                          String sysManufacturer,
                          String sysModel,
                          String firmwareManufacturer,
                          String baseboardManufacturer) {
        this.osFamily = normalize(osFamily);
        this.kernelBuildNumber = normalize(kernelBuildNumber);
        this.sysManufacturer = normalize(sysManufacturer);
        this.sysModel = normalize(sysModel);
        this.firmwareManufacturer = normalize(firmwareManufacturer);
        this.baseboardManufacturer = normalize(baseboardManufacturer);
    }

    OSEnvironment detect() {
        // Order matters — WSL before VM, Container before VM
        if (isWSL())            return OSEnvironment.WSL;
        if (isContainer())      return OSEnvironment.CONTAINER;
        if (isVirtualMachine()) return OSEnvironment.VIRTUAL_MACHINE;
        if (isPhysical())       return OSEnvironment.PHYSICAL;
        return OSEnvironment.UNKNOWN;
    }

    // ── WSL ───────────────────────────────────────────────────────────────────

    private boolean isWSL() {
        if (!isLinuxFamily()) return false;

        // /proc/version contains "microsoft" or "WSL" on WSL1 and WSL2
        try {
            String procVersion = Files.readString(Path.of("/proc/version")).toLowerCase();
            if (procVersion.contains("microsoft") || procVersion.contains("wsl")) return true;
        } catch (IOException ignored) {}

        // WSL2 also exposes /proc/sys/fs/binfmt_misc/WSLInterop
        if (Files.exists(Path.of("/proc/sys/fs/binfmt_misc/WSLInterop"))) return true;

        // Kernel version string from OSHI
        return kernelBuildNumber.contains("microsoft") || kernelBuildNumber.contains("wsl");
    }

    // ── Container ─────────────────────────────────────────────────────────────

    private boolean isContainer() {
        // 1. Docker creates /.dockerenv
        if (Files.exists(Path.of("/.dockerenv"))) return true;

        // 2. Podman creates /run/.containerenv
        if (Files.exists(Path.of("/run/.containerenv"))) return true;

        // 3. /proc/1/cgroup contains container runtime markers
        try {
            String cgroup = Files.readString(Path.of("/proc/1/cgroup")).toLowerCase();
            if (containsAny(cgroup, "docker", "kubepods", "lxc", "containerd")) return true;
        } catch (IOException ignored) {}

        // 4. cgroup v2 unified hierarchy
        try {
            String selfCgroup = Files.readString(Path.of("/proc/self/cgroup")).toLowerCase();
            if (containsAny(selfCgroup, "docker", "kubepods", "lxc", "containerd")) return true;
        } catch (IOException ignored) {}

        // 5. Environment variables set by container runtimes
        String containerEnv = System.getenv("container");
        if (containerEnv != null && !containerEnv.isEmpty()) return true;
        if (System.getenv("KUBERNETES_SERVICE_HOST") != null) return true;
        if (System.getenv("DOTNET_RUNNING_IN_CONTAINER") != null) return true;

        // 6. systemd-detect-virt --container
        try {
            Process proc = new ProcessBuilder("systemd-detect-virt", "--container").start();
            String output = new String(proc.getInputStream().readAllBytes()).trim();
            proc.waitFor();
            if (!output.isEmpty() && !"none".equalsIgnoreCase(output)) return true;
        } catch (IOException | InterruptedException ignored) {}

        // 7. PID 1 is not init/systemd — likely a container entrypoint
        try {
            String sched = Files.readString(Path.of("/proc/1/sched"));
            String firstLine = sched.lines().findFirst().orElse("");
            if (!firstLine.contains("init") && !firstLine.contains("systemd")) return true;
        } catch (IOException ignored) {}

        return false;
    }

    // ── Virtual Machine ───────────────────────────────────────────────────────

    private boolean isVirtualMachine() {
        return containsAny(sysManufacturer, "vmware", "virtualbox", "microsoft corporation",
                        "qemu", "xen", "parallels", "innotek", "bochs")
                || containsAny(sysModel, "vmware", "virtualbox", "virtual machine",
                        "kvm", "qemu", "hvm domU", "parallels")
                || containsAny(firmwareManufacturer, "vmware", "virtualbox", "seabios", "qemu", "xen")
                || containsAny(baseboardManufacturer, "vmware", "virtualbox", "microsoft corporation");
    }

    // ── Physical ──────────────────────────────────────────────────────────────

    private static final String[] OEM_KEYWORDS = {
            "lenovo", "dell", "hp", "hewlett", "asus", "acer", "apple",
            "msi", "micro-star", "gigabyte", "supermicro", "toshiba",
            "samsung", "sony", "fujitsu", "panasonic", "nec", "intel"
    };

    private static final String[] VM_KEYWORDS = {
            "vmware", "virtualbox", "qemu", "xen",
            "microsoft corporation", "bochs", "innotek", "parallels"
    };

    private boolean isPhysical() {
        // 1. OSHI DMI manufacturer (works with root)
        if (containsAny(sysManufacturer, OEM_KEYWORDS)) return true;

        // 2. Fallback: /sys/class/dmi/id/ files (readable without root on most Linux)
        String[] dmiFiles = {
                "/sys/class/dmi/id/sys_vendor",
                "/sys/class/dmi/id/board_vendor",
                "/sys/class/dmi/id/chassis_vendor"
        };
        for (String dmiFile : dmiFiles) {
            try {
                String vendor = Files.readString(Path.of(dmiFile)).toLowerCase().trim();
                if (containsAny(vendor, OEM_KEYWORDS)) return true;
                if (!vendor.isEmpty() && !vendor.equals("unknown") &&
                        !containsAny(vendor, VM_KEYWORDS)) {
                    return true;
                }
            } catch (IOException ignored) {}
        }

        // 3. Fallback: battery presence suggests physical laptop
        if (Files.exists(Path.of("/sys/class/power_supply/BAT0")) ||
                Files.exists(Path.of("/sys/class/power_supply/BAT1"))) {
            return true;
        }

        // 4. Fallback: systemd-detect-virt returns "none" on physical hardware
        try {
            Process proc = new ProcessBuilder("systemd-detect-virt").start();
            String output = new String(proc.getInputStream().readAllBytes()).trim();
            proc.waitFor();
            if ("none".equalsIgnoreCase(output)) return true;
        } catch (IOException | InterruptedException ignored) {}

        return false;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean isLinuxFamily() {
        return containsAny(osFamily, "linux", "ubuntu", "debian", "fedora",
                "centos", "red hat", "suse", "arch", "manjaro", "mint");
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase().trim();
    }

    private static boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) return true;
        }
        return false;
    }
}
