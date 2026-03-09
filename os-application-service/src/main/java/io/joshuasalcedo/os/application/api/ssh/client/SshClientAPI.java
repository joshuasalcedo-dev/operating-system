package io.joshuasalcedo.os.application.api.ssh.client;

import io.joshuasalcedo.os.domain.ssh.SshException;
import io.joshuasalcedo.os.domain.ssh.SshHost;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Service for executing commands and transferring files on a remote SSH host.
 */
public interface SshClientAPI {

    // ── Core ──────────────────────────────────────────────────────────────────

    /** Executes a single command on the remote host and returns the result. */
    SshCommandResult execute(SshHost host, String command);

    /** Executes multiple commands sequentially on the remote host. */
    List<SshCommandResult> executeAll(SshHost host, List<String> commands);

    /** Uploads a local file to the remote host via SCP. */
    void upload(SshHost host, String localPath, String remotePath);

    /** Downloads a file from the remote host via SCP. */
    void download(SshHost host, String remotePath, String localPath);

    /** Tests whether the remote host is reachable and authentication succeeds. */
    boolean isReachable(SshHost host);

    // ── Convenience ───────────────────────────────────────────────────────────

    /** Reads the contents of a remote file as a string. */
    default String readRemoteFile(SshHost host, String remotePath) {
        return execute(host, "cat " + remotePath).stdout();
    }

    /** Returns the remote machine's /etc/machine-id. */
    default String remoteMachineId(SshHost host) {
        return execute(host, "cat /etc/machine-id").stdout().strip();
    }

    /** Returns the remote machine's hostname. */
    default String remoteHostname(SshHost host) {
        return execute(host, "hostname").stdout().strip();
    }

    /** Returns the remote machine's OS family (Linux, Darwin, etc.). */
    default String remoteOsFamily(SshHost host) {
        return execute(host, "uname -s").stdout().strip();
    }

    /** Returns the remote kernel version string. */
    default String remoteKernelVersion(SshHost host) {
        return execute(host, "uname -r").stdout().strip();
    }

    /** Returns the uptime of the remote machine in human-readable form. */
    default String remoteUptime(SshHost host) {
        return execute(host, "uptime -p").stdout().strip();
    }

    /** Returns the current logged-in user on the remote machine. */
    default String remoteWhoami(SshHost host) {
        return execute(host, "whoami").stdout().strip();
    }

    /** Returns the remote machine's CPU architecture (x86_64, aarch64, etc.). */
    default String remoteArch(SshHost host) {
        return execute(host, "uname -m").stdout().strip();
    }

    /** Returns the OS pretty name from /etc/os-release. */
    default Optional<String> remoteOsPrettyName(SshHost host) {
        SshCommandResult result = execute(host,
                "grep PRETTY_NAME /etc/os-release | cut -d= -f2 | tr -d '\"'");
        return result.isSuccess() ? Optional.of(result.stdout().strip()) : Optional.empty();
    }

    /** Returns a disk usage summary of the remote machine. */
    default String remoteDiskUsage(SshHost host) {
        return execute(host, "df -h --total | tail -1").stdout().strip();
    }

    /** Returns a memory usage summary of the remote machine. */
    default String remoteMemoryUsage(SshHost host) {
        return execute(host, "free -h | grep Mem").stdout().strip();
    }

    /** True if a given command/binary exists on the remote machine. */
    default boolean remoteCommandExists(SshHost host, String command) {
        return execute(host, "command -v " + command).isSuccess();
    }

    /** True if a given path (file or directory) exists on the remote machine. */
    default boolean remotePathExists(SshHost host, String path) {
        return execute(host, "test -e " + path).isSuccess();
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    static SshClientAPI createDefault(SshConnectionConfig config) {
        return new SshjClientAPI(config);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NESTED RECORDS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Result of a remotely executed SSH command.
     */
    record SshCommandResult(
            String command,
            int exitCode,
            String stdout,
            String stderr,
            Duration elapsed
    ) {
        public SshCommandResult {
            stdout = stdout != null ? stdout : "";
            stderr = stderr != null ? stderr : "";
        }

        // ── Status ────────────────────────────────────────────────────────────

        public boolean isSuccess()  { return exitCode == 0; }
        public boolean isFailed()   { return exitCode != 0; }
        public boolean hasErrors()  { return !stderr.isBlank(); }
        public boolean hasOutput()  { return !stdout.isBlank(); }

        // ── stdout ────────────────────────────────────────────────────────────

        public List<String> stdoutLines() {
            return stdout.isBlank() ? List.of() : List.of(stdout.split("\\R"));
        }

        public List<String> stderrLines() {
            return stderr.isBlank() ? List.of() : List.of(stderr.split("\\R"));
        }

        /** First line of stdout — useful for single-value commands. */
        public Optional<String> firstLine() {
            return stdoutLines().stream().findFirst();
        }

        /** Last line of stdout — useful for commands that print a summary at the end. */
        public Optional<String> lastLine() {
            List<String> lines = stdoutLines();
            return lines.isEmpty() ? Optional.empty() : Optional.of(lines.get(lines.size() - 1));
        }

        /** Number of output lines. */
        public int lineCount() { return stdoutLines().size(); }

        /** Elapsed time in milliseconds. */
        public long elapsedMillis() {
            return elapsed != null ? elapsed.toMillis() : -1;
        }

        @Override
        public String toString() {
            return String.format("[exit=%d, %dms] %s", exitCode, elapsedMillis(), command);
        }
    }

    /**
     * Infrastructure-layer configuration for establishing an SSH connection.
     */
    record SshConnectionConfig(
            String privateKeyPath,
            String passphrase,
            boolean strictHostKeyChecking,
            int timeoutSeconds,
            AuthMethod authMethod
    ) {
        enum AuthMethod { PUBLIC_KEY, PASSWORD }
        // ── Factories ─────────────────────────────────────────────────────────

        /**
         * Auto-detects the best available SSH key on the current machine.
         * Preference order: id_ed25519 → id_ecdsa → id_rsa
         * If none exist, generates a new ED25519 key pair via ssh-keygen.
         */
        public static SshConnectionConfig defaults() {
            String home = System.getProperty("user.home");
            boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
            String sshDir = (home + "/.ssh").replace("\\", "/");

            String[] candidates = {
                sshDir + "/id_ed25519",
                sshDir + "/id_ecdsa",
                sshDir + "/id_rsa"
            };

            for (String path : candidates) {
                if (new java.io.File(path).exists()) {
                    return new SshConnectionConfig(path, null, false, 10, AuthMethod.PUBLIC_KEY);
                }
            }

            // No key found — generate one with ssh-keygen
            String newKey = sshDir + "/id_ed25519";
            try {
                new java.io.File(sshDir).mkdirs();

                ProcessBuilder pb = isWindows
                        ? new ProcessBuilder("cmd", "/c", "ssh-keygen",
                                "-t", "ed25519", "-f", newKey, "-N", "", "-C",
                                System.getProperty("user.name") + "@" + hostname())
                        : new ProcessBuilder("ssh-keygen",
                                "-t", "ed25519", "-f", newKey, "-N", "", "-C",
                                System.getProperty("user.name") + "@" + hostname());

                pb.redirectErrorStream(true);
                Process process = pb.start();
                process.waitFor();
            } catch (Exception e) {
                throw new RuntimeException(
                        "No SSH key found and ssh-keygen failed. " +
                        "Run manually: ssh-keygen -t ed25519 -f " + newKey, e);
            }

            return new SshConnectionConfig(newKey, null, false, 10, AuthMethod.PUBLIC_KEY);
        }

        /** ~/.ssh/id_ed25519 — explicit ED25519, no auto-detection. */
        public static SshConnectionConfig defaultsEd25519() {
            String sshDir = System.getProperty("user.home").replace("\\", "/") + "/.ssh";
            return new SshConnectionConfig(sshDir + "/id_ed25519", null, false, 10, AuthMethod.PUBLIC_KEY);
        }

        private static String hostname() {
            try { return java.net.InetAddress.getLocalHost().getHostName(); }
            catch (Exception e) { return "localhost"; }
        }

        /** Specific key file, no passphrase. */
        public static SshConnectionConfig withKey(String privateKeyPath) {
            return new SshConnectionConfig(privateKeyPath, null, false, 10, AuthMethod.PUBLIC_KEY);
        }

        /** Specific key file with passphrase. */
        public static SshConnectionConfig withKey(String privateKeyPath, String passphrase) {
            return new SshConnectionConfig(privateKeyPath, passphrase, false, 10, AuthMethod.PUBLIC_KEY);
        }

        /** Plain user password — no key file required. */
        public static SshConnectionConfig withPassword(String password) {
            return new SshConnectionConfig(null, password, false, 10, AuthMethod.PASSWORD);
        }

        // ── Withers ───────────────────────────────────────────────────────────

        /** Returns a copy with strict host key checking enabled. */
        public SshConnectionConfig withStrictHostChecking() {
            return new SshConnectionConfig(privateKeyPath, passphrase, true, timeoutSeconds, authMethod);
        }

        /** Returns a copy with a different timeout. */
        public SshConnectionConfig withTimeout(int seconds) {
            return new SshConnectionConfig(privateKeyPath, passphrase, strictHostKeyChecking, seconds, authMethod);
        }

        // ── Queries ───────────────────────────────────────────────────────────

        public boolean hasPassphrase() {
            return passphrase != null && !passphrase.isBlank();
        }

        public Duration timeout() {
            return Duration.ofSeconds(timeoutSeconds);
        }

        @Override
        public String toString() {
            return String.format("SshConnectionConfig[key=%s, strict=%b, timeout=%ds]",
                    privateKeyPath, strictHostKeyChecking, timeoutSeconds);
        }
    }
}

// ── SSHJ Implementation ───────────────────────────────────────────────────────

/**
 * Package-private SSHJ-backed implementation of {@link SshClientAPI}.
 */
class SshjClientAPI implements SshClientAPI {

    private final SshClientAPI.SshConnectionConfig config;

    SshjClientAPI(SshClientAPI.SshConnectionConfig config) {
        this.config = config;
    }

    @Override
    public SshClientAPI.SshCommandResult execute(SshHost host, String command) {
        long start = System.currentTimeMillis();
        try (SSHClient ssh = connect(host)) {
            try (Session session = ssh.startSession()) {
                Session.Command cmd = session.exec(command);
                String stdout = IOUtils.readFully(cmd.getInputStream()).toString().trim();
                String stderr = IOUtils.readFully(cmd.getErrorStream()).toString().trim();
                cmd.join();
                int exitCode = cmd.getExitStatus() != null ? cmd.getExitStatus() : -1;
                Duration elapsed = Duration.ofMillis(System.currentTimeMillis() - start);
                return new SshClientAPI.SshCommandResult(command, exitCode, stdout, stderr, elapsed);
            }
        } catch (IOException e) {
            throw new SshException(SshException.Reason.HOST_UNREACHABLE, host, e);
        }
    }

    @Override
    public List<SshClientAPI.SshCommandResult> executeAll(SshHost host, List<String> commands) {
        return commands.stream()
                .map(cmd -> execute(host, cmd))
                .toList();
    }

    @Override
    public void upload(SshHost host, String localPath, String remotePath) {
        try (SSHClient ssh = connect(host)) {
            ssh.useCompression();
            ssh.newSCPFileTransfer().upload(localPath, remotePath);
        } catch (IOException e) {
            throw new SshException(SshException.Reason.HOST_UNREACHABLE, host, e);
        }
    }

    @Override
    public void download(SshHost host, String remotePath, String localPath) {
        try (SSHClient ssh = connect(host)) {
            ssh.useCompression();
            ssh.newSCPFileTransfer().download(remotePath, localPath);
        } catch (IOException e) {
            throw new SshException(SshException.Reason.HOST_UNREACHABLE, host, e);
        }
    }

    @Override
    public boolean isReachable(SshHost host) {
        try (SSHClient ssh = connect(host)) {
            return ssh.isConnected() && ssh.isAuthenticated();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private SSHClient connect(SshHost host) throws IOException {
        SSHClient ssh = new SSHClient();

        if (config.strictHostKeyChecking()) {
            ssh.loadKnownHosts();
        } else {
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
        }

        ssh.setConnectTimeout((int) config.timeout().toMillis());
        ssh.connect(host.hostname());
        authenticate(ssh, host);
        return ssh;
    }

    private void authenticate(SSHClient ssh, SshHost host) throws IOException {
        try {
            if (config.authMethod() == SshConnectionConfig.AuthMethod.PASSWORD) {
                // Plain user password authentication
                ssh.authPassword(host.username(), config.passphrase());
            } else if (config.hasPassphrase()) {
                // Key file protected by a passphrase
                ssh.authPublickey(host.username(),
                        ssh.loadKeys(config.privateKeyPath(), null,
                                net.schmizz.sshj.userauth.password.PasswordUtils.createOneOff(
                                        config.passphrase().toCharArray())));
            } else if (config.privateKeyPath() != null) {
                // Unprotected key file
                ssh.authPublickey(host.username(), config.privateKeyPath());
            } else {
                // No explicit key — try all standard keys in ~/.ssh/
                ssh.authPublickey(host.username(), allAvailableKeys());
            }
        } catch (IOException e) {
            throw new SshException(SshException.Reason.AUTHENTICATION_FAILED, host, e);
        }
    }

    /** Returns all existing standard private key paths under ~/.ssh/. */
    private String[] allAvailableKeys() {
        String sshDir = System.getProperty("user.home").replace("\\", "/") + "/.ssh";
        return java.util.Arrays.stream(new String[]{
                sshDir + "/id_ed25519",
                sshDir + "/id_ecdsa",
                sshDir + "/id_rsa"
        })
        .filter(path -> new java.io.File(path).exists())
        .toArray(String[]::new);
    }
}
