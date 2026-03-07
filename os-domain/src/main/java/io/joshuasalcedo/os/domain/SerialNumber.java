package io.joshuasalcedo.os.domain;

import java.util.Objects;

/**
 * Value object representing a hardware serial number.
 */
public record SerialNumber(String value) implements OperatingSystemObject {

    public static final SerialNumber UNKNOWN = new SerialNumber("Unknown");

    public SerialNumber {
        Objects.requireNonNull(value, "Serial number must not be null");
        value = value.trim();
    }

    public static SerialNumber of(String value) {
        if (value == null || value.isBlank() || "To be filled by O.E.M.".equalsIgnoreCase(value.trim())) {
            return UNKNOWN;
        }
        return new SerialNumber(value);
    }

    public boolean isKnown() {
        return !this.equals(UNKNOWN);
    }

    @Override
    public String toString() {
        return value;
    }
}