package io.joshuasalcedo.os.application.api.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.joshuasalcedo.os.domain.network.PublicIpInfo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Service interface for querying the system's public IP and geolocation information.
 * Implementations typically resolve this via an external lookup (e.g. ipinfo.io).
 */
interface PublicIpAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns the current public IP and geolocation info. */
    PublicIpInfo getPublicIpInfo();

    // ── IP ────────────────────────────────────────────────────────────────────

    /** The public IP address as a string. */
    default String publicIp() {
        return getPublicIpInfo().ip();
    }

    /** The reverse DNS hostname for the public IP, if available. */
    default Optional<String> reverseDnsHostname() {
        String h = getPublicIpInfo().hostname();
        return (h != null && !h.isBlank()) ? Optional.of(h) : Optional.empty();
    }

    /** True if the IP appears to be IPv6. */
    default boolean isIpv6() {
        return publicIp().contains(":");
    }

    // ── Geolocation ───────────────────────────────────────────────────────────

    /** City where the public IP is geolocated. */
    default Optional<String> city() {
        String c = getPublicIpInfo().city();
        return (c != null && !c.isBlank()) ? Optional.of(c) : Optional.empty();
    }

    /** Region / state where the public IP is geolocated. */
    default Optional<String> region() {
        String r = getPublicIpInfo().region();
        return (r != null && !r.isBlank()) ? Optional.of(r) : Optional.empty();
    }

    /** ISO 3166-1 alpha-2 country code (e.g. "PH", "US", "DE"). */
    default Optional<String> countryCode() {
        String c = getPublicIpInfo().country();
        return (c != null && !c.isBlank()) ? Optional.of(c) : Optional.empty();
    }

    /** Latitude of the geolocated position. */
    default double latitude() {
        return getPublicIpInfo().latitude();
    }

    /** Longitude of the geolocated position. */
    default double longitude() {
        return getPublicIpInfo().longitude();
    }

    /** Postal / ZIP code of the geolocated position, if available. */
    default Optional<String> postalCode() {
        String p = getPublicIpInfo().postal();
        return (p != null && !p.isBlank()) ? Optional.of(p) : Optional.empty();
    }

    // ── Network / ASN ─────────────────────────────────────────────────────────

    /** The org/ASN string (e.g. "AS1234 Some ISP Ltd"). */
    default Optional<String> org() {
        String o = getPublicIpInfo().org();
        return (o != null && !o.isBlank()) ? Optional.of(o) : Optional.empty();
    }

    /** The ASN number extracted from the org string (e.g. "AS1234"). */
    default Optional<String> asn() {
        return org().map(o -> {
            String[] parts = o.split(" ", 2);
            return parts[0].startsWith("AS") ? parts[0] : null;
        });
    }

    /** The ISP/org name without the ASN prefix. */
    default Optional<String> ispName() {
        return org().map(o -> {
            String[] parts = o.split(" ", 2);
            return parts.length > 1 ? parts[1] : o;
        });
    }

    // ── Timezone ──────────────────────────────────────────────────────────────

    /** IANA timezone identifier for the public IP's location (e.g. "Asia/Manila"). */
    default Optional<String> timezone() {
        String tz = getPublicIpInfo().timezone();
        return (tz != null && !tz.isBlank()) ? Optional.of(tz) : Optional.empty();
    }

    /** True if the public IP is geolocated to the given ISO country code. */
    default boolean isInCountry(String isoCountryCode) {
        return countryCode()
                .map(c -> c.equalsIgnoreCase(isoCountryCode))
                .orElse(false);
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    /** Returns the default implementation backed by {@code https://ipinfo.io/json}. */
    static PublicIpAPI createDefault() {
        return new IpInfoIoPublicIpAPI();
    }
}

/**
 * Package-private implementation that fetches public IP info from
 * {@code https://ipinfo.io/json} using Java's built-in {@link HttpClient}.
 * Equivalent to: {@code curl -s https://ipinfo.io/json}
 *
 * <p>JSON is parsed manually to avoid any external dependency.
 */
class IpInfoIoPublicIpAPI implements PublicIpAPI {

    private static final String IPINFO_URL = "https://ipinfo.io/json";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    IpInfoIoPublicIpAPI() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public PublicIpInfo getPublicIpInfo() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(IPINFO_URL))
                .timeout(TIMEOUT)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "ipinfo.io returned HTTP " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());

            return new PublicIpInfo(
                    text(root, "ip"),
                    text(root, "hostname"),
                    text(root, "city"),
                    text(root, "region"),
                    text(root, "country"),
                    text(root, "loc"),
                    text(root, "org"),
                    text(root, "postal"),
                    text(root, "timezone")
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to reach ipinfo.io: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Public IP lookup interrupted", e);
        }
    }

    // ── JSON helper ───────────────────────────────────────────────────────────

    /** Returns the text value of a field, or {@code null} if absent or null in JSON. */
    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value != null && !value.isNull()) ? value.asText() : null;
    }
}
