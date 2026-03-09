package io.joshuasalcedo.os.application.api.ssh;

import io.joshuasalcedo.os.application.api.SshAPI;
import io.joshuasalcedo.os.application.api.ssh.client.SshClientAPI;
import io.joshuasalcedo.os.application.api.ssh.host.SshHostAPI;
import io.joshuasalcedo.os.domain.ssh.SshAuthorizedKeys;
import io.joshuasalcedo.os.domain.ssh.SshHost;

import java.time.Duration;
import java.util.List;

/**
 * Package-private SSHJ-backed implementation of {@link SshAPI}.
 * Delegates to {@link SshClientAPI} and {@link SshHostAPI}.
 *
 * @author JoshuaSalcedo
 * @since 3/9/2026
 */
class DefaultSshAPI implements SshAPI {

	private final SshClientAPI clientAPI;
	private final SshHostAPI hostAPI;

	DefaultSshAPI(SshClientAPI.SshConnectionConfig config) {
		this.clientAPI = SshClientAPI.createDefault(config);
		this.hostAPI = SshHostAPI.createDefault(config);
	}

	@Override
	public boolean isReachable(SshHost host) {
		return clientAPI.isReachable(host);
	}

	@Override
	public SshCommandResult execute(SshHost host, String command) {
		SshClientAPI.SshCommandResult r = clientAPI.execute(host, command);
		return new SshCommandResult(r.command(), r.exitCode(), r.stdout(), r.stderr(), r.elapsed());
	}

	@Override
	public List<SshCommandResult> executeAll(SshHost host, List<String> commands) {
		return clientAPI.executeAll(host, commands).stream()
				.map(r -> new SshCommandResult(r.command(), r.exitCode(), r.stdout(), r.stderr(), r.elapsed()))
				.toList();
	}

	@Override
	public void upload(SshHost host, String localPath, String remotePath) {
		clientAPI.upload(host, localPath, remotePath);
	}

	@Override
	public void download(SshHost host, String remotePath, String localPath) {
		clientAPI.download(host, remotePath, localPath);
	}

	@Override
	public SshAuthorizedKeys readAuthorizedKeys(SshHost host) {
		return hostAPI.readAuthorizedKeys(host);
	}

	@Override
	public void writeAuthorizedKeys(SshHost host, SshAuthorizedKeys authorizedKeys) {
		hostAPI.writeAuthorizedKeys(host, authorizedKeys);
	}
}
