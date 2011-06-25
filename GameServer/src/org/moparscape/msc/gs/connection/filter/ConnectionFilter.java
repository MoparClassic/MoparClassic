package org.moparscape.msc.gs.connection.filter;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.common.IoFilter;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.BlacklistFilter;
import org.moparscape.msc.gs.util.Cache;

public class ConnectionFilter extends BlacklistFilter {
	private Cache<InetSocketAddress, Integer> connections = new Cache<InetSocketAddress, Integer>();
	private static final int BLOCK_THRESHOLD = 5;

	public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) {
		final SocketAddress sa = session.getRemoteAddress();
		if (sa != null && sa instanceof InetSocketAddress) {
			final InetSocketAddress a = (InetSocketAddress) sa;
			final Integer val = connections.get(a);
			System.out.println(val);
			final Integer retVal = connections
					.put(a, val == null ? 1 : val + 1);
			if (retVal != null && retVal > BLOCK_THRESHOLD) {
				block(a.getAddress());
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
			System.out.println(val);
			final Integer retVal = connections
					.put(a, val == null ? 1 : val + 1);
			if (retVal != null && retVal - 1 <= BLOCK_THRESHOLD) {
				unblock(a.getAddress());
			}
		}
		super.sessionClosed(nextFilter, session);
	}
}
