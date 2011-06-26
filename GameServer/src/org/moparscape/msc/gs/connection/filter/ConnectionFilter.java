package org.moparscape.msc.gs.connection.filter;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.common.IoFilter;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.BlacklistFilter;
import org.moparscape.msc.config.Config;
import org.moparscape.msc.gs.util.Cache;

public class ConnectionFilter extends BlacklistFilter {
	private Cache<InetSocketAddress, Integer> connections = new Cache<InetSocketAddress, Integer>(
			Config.CONNECTION_THROTTLE_SIZE);

	public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) {
		final SocketAddress sa = session.getRemoteAddress();
		if (sa != null && sa instanceof InetSocketAddress) {
			final InetSocketAddress a = (InetSocketAddress) sa;
			if (IPBanManager.isBlocked(a)) {
				block(a.getAddress());
				return;
			}
			final Integer val = connections.get(a);
			final Integer retVal = connections
					.put(a, val == null ? 1 : val + 1);
			if (retVal != null && retVal > Config.CONENCTION_THROTTLE_THRESHOLD) {
				block(a.getAddress());
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
			final Integer val = connections.get(a);
			final Integer retVal = connections
					.put(a, val == null ? 1 : val + 1);
			if (retVal != null
					&& retVal - 1 <= Config.CONENCTION_THROTTLE_THRESHOLD) {
				unblock(a.getAddress());
			}
		}
		super.sessionClosed(nextFilter, session);
	}
}
