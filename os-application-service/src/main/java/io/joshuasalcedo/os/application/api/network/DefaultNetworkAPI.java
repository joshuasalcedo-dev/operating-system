package io.joshuasalcedo.os.application.api.network;

import io.joshuasalcedo.os.application.api.NetworkAPI;
import io.joshuasalcedo.os.domain.network.*;

import java.util.List;

/**
 * Package-private OSHI-backed implementation of {@link NetworkAPI}.
 *
 * @author JoshuaSalcedo
 * @since 3/9/2026
 */
class DefaultNetworkAPI implements NetworkAPI {

	private final DnsConfigurationAPI dnsConfigurationAPI;
	private final InternetProtocolStatsAPI internetProtocolStatsAPI;
	private final ListeningPortAPI listeningPortAPI;
	private final NetworkRouteAPI networkRouteAPI;
	private final TcpConnectionAPI tcpConnectionAPI;
	private final UdpConnectionAPI udpConnectionAPI;
	private final PublicIpAPI publicIpAPI;

	DefaultNetworkAPI() {
		var systemInfo = new oshi.SystemInfo();
		this.dnsConfigurationAPI = new OshiDnsConfigurationAPI(systemInfo);
		this.internetProtocolStatsAPI = new OshiInternetProtocolStatsAPI(systemInfo);
		this.listeningPortAPI = new OshiListeningPortAPI(systemInfo);
		this.networkRouteAPI = new OshiNetworkRouteAPI(systemInfo);
		this.tcpConnectionAPI = new OshiTcpConnectionAPI(systemInfo);
		this.udpConnectionAPI = new OshiUdpConnectionAPI(systemInfo);
		publicIpAPI = new IpInfoIoPublicIpAPI();
	}

	@Override
	public DnsConfiguration getDnsConfiguration() {
		return dnsConfigurationAPI.getDnsConfiguration();
	}

	@Override
	public InternetProtocolStats getInternetProtocolStats() {
		return internetProtocolStatsAPI.getStats();
	}

	@Override
	public List<ListeningPort> getPorts() {
		return listeningPortAPI.getPorts();
	}

	@Override
	public List<NetworkRoute> getRoutes() {
		return networkRouteAPI.getRoutes();
	}

	@Override
	public List<TcpConnection> getConnections() {
		return tcpConnectionAPI.getConnections();
	}

	@Override
	public List<TcpConnection> getConnections(ConnectionState state) {
		return tcpConnectionAPI.getConnections(state);
	}

	@Override
	public List<UdpConnection> getEndpoints() {
		return udpConnectionAPI.getEndpoints();
	}

	@Override
	public PublicIpInfo getPublicIpInfo() {
		return publicIpAPI.getPublicIpInfo();
	}

	//	@Override
//	public void stopPort(int port) {
//		throw new UnsupportedOperationException("stopPort not yet implemented");
//	}
//
//	@Override
//	public void stopProcess(long pid) {
//		throw new UnsupportedOperationException("stopProcess not yet implemented");
//	}
}
