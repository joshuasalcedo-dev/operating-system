package io.joshuasalcedo.os.application.api.network;

import io.joshuasalcedo.os.domain.network.ListeningPort;
import io.joshuasalcedo.os.domain.network.TransportProtocol;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Service interface for querying listening ports.
 */
interface ListeningPortAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Returns all ports currently listening for incoming connections. */
    List<ListeningPort> getPorts();

    // ── By protocol ───────────────────────────────────────────────────────────

    /** Ports listening on TCP. */
    default List<ListeningPort> tcp() {
        return getPorts().stream()
                .filter(p -> p.protocol() == TransportProtocol.TCP)
                .toList();
    }

    /** Ports listening on TCP over IPv6. */
    default List<ListeningPort> tcp6() {
        return getPorts().stream()
                .filter(p -> p.protocol() == TransportProtocol.TCP6)
                .toList();
    }

    /** Ports listening on UDP. */
    default List<ListeningPort> udp() {
        return getPorts().stream()
                .filter(p -> p.protocol() == TransportProtocol.UDP)
                .toList();
    }

    /** Ports listening on UDP over IPv6. */
    default List<ListeningPort> udp6() {
        return getPorts().stream()
                .filter(p -> p.protocol() == TransportProtocol.UDP6)
                .toList();
    }

    // ── Port ranges ───────────────────────────────────────────────────────────

    /** System / well-known ports: 0–1023 (HTTP=80, SSH=22, HTTPS=443). */
    default List<ListeningPort> systemPorts() {
        return getPorts().stream()
                .filter(p -> p.port() <= 1023)
                .toList();
    }

    /** Registered ports: 1024–49151 (Tomcat=8080, Postgres=5432, Redis=6379). */
    default List<ListeningPort> registeredPorts() {
        return getPorts().stream()
                .filter(p -> p.port() >= 1024 && p.port() <= 49151)
                .toList();
    }

    /** Dynamic / ephemeral ports: 49152–65535 (short-lived client connections). */
    default List<ListeningPort> ephemeralPorts() {
        return getPorts().stream()
                .filter(p -> p.port() >= 49152)
                .toList();
    }

    // ── Bind address ──────────────────────────────────────────────────────────

    /** Ports accessible from outside this machine (bound to 0.0.0.0 or ::). */
    default List<ListeningPort> publicPorts() {
        return getPorts().stream()
                .filter(ListeningPort::isBindAll)
                .toList();
    }

    /** Ports accessible only from this machine (bound to 127.x or ::1). */
    default List<ListeningPort> localOnlyPorts() {
        return getPorts().stream()
                .filter(p -> p.bindAddress().isLoopback())
                .toList();
    }

    // ── Lookup ────────────────────────────────────────────────────────────────

    /** Find a listening port by exact port number. */
    default Optional<ListeningPort> find(int port) {
        return getPorts().stream()
                .filter(p -> p.port() == port)
                .findFirst();
    }

    /** True if the given port number has any listener. */
    default boolean isInUse(int port) {
        return find(port).isPresent();
    }

    /** True if the given port is free (nothing listening on it). */
    default boolean isAvailable(int port) {
        return !isInUse(port);
    }

    /** Available (free) ports within a range, inclusive. */
    default List<Integer> availablePorts(int from, int to) {
        Set<Integer> used = getPorts().stream()
                .map(ListeningPort::port)
                .collect(Collectors.toSet());
        return IntStream.rangeClosed(from, to)
                .filter(p -> !used.contains(p))
                .boxed()
                .toList();
    }

    // ── By process ────────────────────────────────────────────────────────────

    /** All ports owned by the given PID. */
    default List<ListeningPort> byPid(long pid) {
        return getPorts().stream()
                .filter(p -> p.pid() == pid)
                .toList();
    }

    /** All ports owned by a process name (partial, case-insensitive). */
    default List<ListeningPort> byProcess(String processName) {
        return getPorts().stream()
                .filter(p -> p.processName() != null &&
                             p.processName().toLowerCase().contains(processName.toLowerCase()))
                .toList();
    }

    /** All distinct process names that own at least one listening port. */
    default Set<String> listeningProcessNames() {
        return getPorts().stream()
                .filter(p -> p.processName() != null)
                .map(ListeningPort::processName)
                .collect(Collectors.toSet());
    }

    static ListeningPortAPI oshi(oshi.SystemInfo systemInfo) {
        return new OshiListeningPortAPI(systemInfo);
    }
}
