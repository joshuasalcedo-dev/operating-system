package io.joshuasalcedo.os.domain;

import java.util.Objects;

/**
 * Value object representing a hardware manufacturer.
 */
public record Manufacturer(String name) implements OperatingSystemObject {

    public static final Manufacturer UNKNOWN = new Manufacturer("Unknown");

    public Manufacturer {
        Objects.requireNonNull(name, "Manufacturer name must not be null");
        name = name.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Manufacturer name must not be blank");
        }
    }

    public static Manufacturer of(String name) {
        if (name == null || name.isBlank() || "To be filled by O.E.M.".equalsIgnoreCase(name.trim())) {
            return UNKNOWN;
        }
        return new Manufacturer(name);
    }

    public boolean isKnown() {
        return !this.equals(UNKNOWN);
    }

    @Override
    public String toString() {
        return name;
    }
}