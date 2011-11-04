package org.moparscape.msc.gs.connection.filter;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.common.IoFilter;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.BlacklistFilter;
import org.moparscape.msc.config.Config;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.event.SingleEvent;
import org.moparscape.msc.gs.util.Cache;

public class ConnectionFilter extends BlacklistFilter {
	private Cache<String, Integer> connections = new Cache<String, Integer>(
			Config.CONNECTION_THROTTLE_SIZE);

	public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) {
		final SocketAddress sa = session.getRemoteAddress();
		if (sa != null && sa instanceof InetSocketAddress) {
			final InetSocketAddress a = (InetSocketAddress) sa;
			final String host = a.getAddress().getHostAddress();
			if (IPBanManager.isBlocked(host)) {
				block(a.getAddress());
				session.close();
				return;
			}
			Integer val;
			synchronized (connections) {
				val = connections.get(host);
				connections.put(host, val == null ? 1 : val + 1);
			}
			if (val != null
					&& val + 1 >= Config.CONENCTION_THROTTLE_THRESHOLD && !IPBanManager.isBlocked(host)) {
				IPBanManager.block(host);
				block(a.getAddress());
				session.close();
				return;
			}
		}
		super.sessionCreated(nextFilter, session);
	}

	public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session)
			throws Exception {
		final SocketAddress sa = session.getRemoteAddress();
		if (sa != null && sa instanceof InetSocketAddress) {
			final InetSocketAddress a = (InetSocketAddress) sa;
			final Integer val;
			synchronized (connections) {
				val = connections.get(a.getAddress().getHostAddress());
			}
			if (val != null) {
				if (Config.DELAY_REMOVAL > 0) {
					Instance.getDelayedEventHandler().add(
							new SingleEvent(null, Config.DELAY_REMOVAL) {
								public void action() {
									unblock(a);
								}
							});
				} else {
					unblock(a);
				}
			}

		}
		super.sessionClosed(nextFilter, session);
	}

	private void unblock(InetSocketAddress a) {
		final String host = a.getAddress().getHostAddress();
		final Integer val;
		synchronized (connections) {
			val = connections.get(host);

			// Prevents NPE caused by blocking connections 
			if (val == null) {
				return;
			}
			if (val == 1) {
				connections.remove(host);
			} else {
				connections.put(host, val - 1);
			}
		}
		if (val != null && val - 1 < Config.CONENCTION_THROTTLE_THRESHOLD) {
			if (IPBanManager.isBlocked(a))
				IPBanManager.unblock(a);
			unblock(a.getAddress());
		}
	}
}
