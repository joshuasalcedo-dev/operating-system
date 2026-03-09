package io.joshuasalcedo.os.application.api.hardware;

import io.joshuasalcedo.os.domain.hardware.SoundCardInfo;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for querying sound card information.
 */
interface SoundCardAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns all sound cards detected on the system. */
    List<SoundCardInfo> getSoundCards();

    // ── Lookup ────────────────────────────────────────────────────────────────

    /** Find a sound card by name (case-insensitive, partial match). */
    default Optional<SoundCardInfo> findByName(String name) {
        return getSoundCards().stream()
                .filter(s -> s.name().toLowerCase().contains(name.toLowerCase()))
                .findFirst();
    }

    /** Find a sound card by codec name (case-insensitive, partial match). */
    default Optional<SoundCardInfo> findByCodec(String codec) {
        return getSoundCards().stream()
                .filter(s -> s.codec() != null &&
                             s.codec().toLowerCase().contains(codec.toLowerCase()))
                .findFirst();
    }

    // ── Presence ─────────────────────────────────────────────────────────────

    /** True if at least one sound card is present. */
    default boolean hasSoundCard() {
        return !getSoundCards().isEmpty();
    }

    /** Number of sound cards present. */
    default int count() {
        return getSoundCards().size();
    }

    /** True if more than one sound card is present (e.g. onboard + USB audio). */
    default boolean hasMultipleSoundCards() {
        return count() > 1;
    }

    // ── Codecs ────────────────────────────────────────────────────────────────

    /** All distinct codec names across all sound cards. */
    default List<String> codecs() {
        return getSoundCards().stream()
                .map(SoundCardInfo::codec)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .toList();
    }

    // ── Drivers ───────────────────────────────────────────────────────────────

    /** All distinct driver versions across all sound cards. */
    default List<String> driverVersions() {
        return getSoundCards().stream()
                .map(SoundCardInfo::driverVersion)
                .filter(v -> v != null && !v.isBlank())
                .distinct()
                .toList();
    }

    /** True if all sound cards are running the same driver version. */
    default boolean hasUniformDriverVersion() {
        return driverVersions().size() <= 1;
    }

    static SoundCardAPI oshi(oshi.SystemInfo systemInfo) {
        return new OshiSoundCardAPI(systemInfo);
    }
}