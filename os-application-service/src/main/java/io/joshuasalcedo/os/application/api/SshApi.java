package io.joshuasalcedo.os.application.api;

import io.joshuasalcedo.os.domain.ssh.SshAuthorizedKeys;
import io.joshuasalcedo.os.domain.ssh.SshException;
import io.joshuasalcedo.os.domain.ssh.SshHost;
import io.joshuasalcedo.os.domain.ssh.SshPublicKey;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Public SSH API — unified facade over client operations (remote command
 * execution, file transfer) and host management (authorized_keys).
 *
 * @author JoshuaSalcedo
 * @since 3/9/2026
 */
public interface SshAPI {

	// ── Connection ────────────────────────────────────────────────────────────

	/** Tests whether the remote host is reachable and authentication succeeds. */
	boolean isReachable(SshHost host);

	// ── Command execution ─────────────────────────────────────────────────────

	/** Executes a single command on the remote host and returns the result. */
	SshCommandResult execute(SshHost host, String command);

	/** Executes multiple commands sequentially on the remote host. */
	List<SshCommandResult> executeAll(SshHost host, List<String> commands);

	// ── File transfer ─────────────────────────────────────────────────────────

	/** Uploads a local file to the remote host via SCP. */
	void upload(SshHost host, String localPath, String remotePath);

	/** Downloads a file from the remote host via SCP. */
	void download(SshHost host, String remotePath, String localPath);

	// ── Remote info ───────────────────────────────────────────────────────────

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

	/** Returns the remote machine's CPU architecture. */
	default String remoteArch(SshHost host) {
		return execute(host, "uname -m").stdout().strip();
	}

	/** Returns the current logged-in user on the remote machine. */
	default String remoteWhoami(SshHost host) {
		return execute(host, "whoami").stdout().strip();
	}

	/** Reads the contents of a remote file as a string. */
	default String readRemoteFile(SshHost host, String remotePath) {
		return execute(host, "cat " + remotePath).stdout();
	}

	/** True if a given path exists on the remote machine. */
	default boolean remotePathExists(SshHost host, String path) {
		return execute(host, "test -e " + path).isSuccess();
	}

	/** True if a given command exists on the remote machine. */
	default boolean remoteCommandExists(SshHost host, String command) {
		return execute(host, "command -v " + command).isSuccess();
	}

	// ── Authorized keys management ────────────────────────────────────────────

	/** Reads the authorized_keys file from the remote host. */
	SshAuthorizedKeys readAuthorizedKeys(SshHost host);

	/** Writes the given authorized_keys to the remote host. */
	void writeAuthorizedKeys(SshHost host, SshAuthorizedKeys authorizedKeys);

	/** Appends a public key to the remote host's authorized_keys. */
	default void addKey(SshHost host, SshPublicKey key) {
		SshAuthorizedKeys current = readAuthorizedKeys(host);
		if (current.contains(key))
			throw SshException.keyAlreadyAuthorized(key);
		writeAuthorizedKeys(host, current.withKey(key));
	}

	/** Removes a public key from the remote host's authorized_keys. */
	default void removeKey(SshHost host, SshPublicKey key) {
		SshAuthorizedKeys current = readAuthorizedKeys(host);
		if (!current.contains(key))
			throw SshException.keyNotFound(key);
		writeAuthorizedKeys(host, current.withoutKey(key));
	}

	/** True if the given key is already authorized on the host. */
	default boolean isAuthorized(SshHost host, SshPublicKey key) {
		return readAuthorizedKeys(host).contains(key);
	}

	/** All authorized keys on the remote host. */
	default List<SshPublicKey> listKeys(SshHost host) {
		return readAuthorizedKeys(host).keys();
	}

	/** Number of authorized keys on the remote host. */
	default int keyCount(SshHost host) {
		return readAuthorizedKeys(host).size();
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

		public boolean isSuccess()  { return exitCode == 0; }
		public boolean isFailed()   { return exitCode != 0; }
		public boolean hasErrors()  { return !stderr.isBlank(); }
		public boolean hasOutput()  { return !stdout.isBlank(); }

		public List<String> stdoutLines() {
			return stdout.isBlank() ? List.of() : List.of(stdout.split("\\R"));
		}

		public List<String> stderrLines() {
			return stderr.isBlank() ? List.of() : List.of(stderr.split("\\R"));
		}

		public Optional<String> firstLine() {
			return stdoutLines().stream().findFirst();
		}

		public long elapsedMillis() {
			return elapsed != null ? elapsed.toMillis() : -1;
		}

		@Override
		public String toString() {
			return String.format("[exit=%d, %dms] %s", exitCode, elapsedMillis(), command);
		}
	}
}
