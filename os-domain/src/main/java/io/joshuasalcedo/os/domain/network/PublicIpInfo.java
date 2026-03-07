package io.joshuasalcedo.os.domain.network;

import java.util.Objects;

/**
 * Value object representing public IP geolocation information (e.g. from ipinfo.io).
 */
public record PublicIpInfo(
        String ip,
        String hostname,
        String city,
        String region,
        String country,
        String loc,
        String org,
        String postal,
        String timezone
) implements OSNetworkObject {

    public PublicIpInfo {
        Objects.requireNonNull(ip, "IP address must not be null");
    }

    public double latitude() {
        if (loc == null || !loc.contains(",")) return 0.0;
        return Double.parseDouble(loc.split(",")[0]);
    }

    public double longitude() {
        if (loc == null || !loc.contains(",")) return 0.0;
        return Double.parseDouble(loc.split(",")[1]);
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s, %s) [%s] %s",
                ip, city, region, country, org, hostname != null ? hostname : "");
    }
}
