package io.joshuasalcedo.os.application.api.network;

import io.joshuasalcedo.os.domain.network.NetworkRoute;
import oshi.SystemInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

final class OshiNetworkRouteAPI implements NetworkRouteAPI {

    private final SystemInfo systemInfo;

    OshiNetworkRouteAPI(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public List<NetworkRoute> getRoutes() {
        String os = systemInfo.getOperatingSystem().getFamily().toLowerCase();

        if (os.contains("windows")) {
            return parseWindows();
        } else {
            return parseUnix();
        }
    }

    // ── Linux / macOS ─────────────────────────────────────────────────────────
    // `ip route` output example:
    // default via 192.168.1.1 dev eth0 proto dhcp metric 100
    // 192.168.1.0/24 dev eth0 proto kernel scope link src 192.168.1.5

    private List<NetworkRoute> parseUnix() {
        List<NetworkRoute> routes = new ArrayList<>();
        try {
            Process process = new ProcessBuilder("ip", "route").start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    NetworkRoute route = parseIpRouteLine(line.trim());
                    if (route != null) routes.add(route);
                }
            }
        } catch (Exception e) {
            // fallback to netstat -rn on macOS / older Linux
            routes.addAll(parseNetstat());
        }
        return routes;
    }

    private NetworkRoute parseIpRouteLine(String line) {
        if (line.isBlank()) return null;
        // Tokens: destination [via gateway] [dev iface] [metric N]
        String[] tokens = line.split("\\s+");
        if (tokens.length == 0) return null;

        String destination = tokens[0].equals("default") ? "0.0.0.0" : tokens[0];
        String gateway     = null;
        String iface       = null;
        int    metric      = 0;

        for (int i = 1; i < tokens.length - 1; i++) {
            switch (tokens[i]) {
                case "via"    -> gateway = tokens[++i];
                case "dev"    -> iface   = tokens[++i];
                case "metric" -> {
                    try { metric = Integer.parseInt(tokens[++i]); } catch (NumberFormatException ignored) {}
                }
            }
        }
        return new NetworkRoute(destination, gateway, null, iface, metric);
    }

    // ── netstat -rn fallback (macOS / BSD) ────────────────────────────────────
    // Destination        Gateway         Flags        Netif Expire
    // default            192.168.1.1     UGScg          en0

    private List<NetworkRoute> parseNetstat() {
        List<NetworkRoute> routes = new ArrayList<>();
        try {
            Process process = new ProcessBuilder("netstat", "-rn").start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                boolean inTable = false;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Destination") || line.startsWith("Internet")) {
                        inTable = true;
                        continue;
                    }
                    if (!inTable || line.isBlank()) continue;

                    String[] tokens = line.trim().split("\\s+");
                    if (tokens.length < 2) continue;

                    String destination = tokens[0].equals("default") ? "0.0.0.0" : tokens[0];
                    String gateway     = tokens[1].startsWith("link") ? null : tokens[1];
                    String iface       = tokens.length >= 4 ? tokens[3] : null;

                    routes.add(new NetworkRoute(destination, gateway, null, iface, 0));
                }
            }
        } catch (Exception ignored) {}
        return routes;
    }

    // ── Windows ───────────────────────────────────────────────────────────────
    // `route print` output example:
    // Network Destination    Netmask          Gateway       Interface  Metric
    //          0.0.0.0          0.0.0.0      192.168.1.1   192.168.1.5     25

    private List<NetworkRoute> parseWindows() {
        List<NetworkRoute> routes = new ArrayList<>();
        try {
            Process process = new ProcessBuilder("route", "print").start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                boolean inIpv4Section = false;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("IPv4 Route Table")) { inIpv4Section = true;  continue; }
                    if (trimmed.startsWith("IPv6 Route Table")) { inIpv4Section = false; continue; }
                    if (!inIpv4Section || trimmed.isBlank()) continue;
                    if (trimmed.startsWith("=") || trimmed.startsWith("Network Destination")) continue;

                    String[] tokens = trimmed.split("\\s+");
                    if (tokens.length < 5) continue;

                    String destination = tokens[0];
                    String netmask     = tokens[1];
                    String gateway     = tokens[2];
                    String iface       = tokens[3];
                    int    metric      = 0;
                    try { metric = Integer.parseInt(tokens[4]); } catch (NumberFormatException ignored) {}

                    routes.add(new NetworkRoute(destination, gateway, netmask, iface, metric));
                }
            }
        } catch (Exception ignored) {}
        return routes;
    }
}
