package org.moparscape.msc.gs.util;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.builders.ls.MiscPacketBuilder;
import org.moparscape.msc.gs.model.World;

public class DuelLog {
	public static final World world = Instance.getWorld();

	public static void sendlog(final long from, final long to, final int item,
			final long amount, final int x, final int y, final int type) {
		MiscPacketBuilder loginServer = Instance.getServer()
				.getLoginConnector().getActionSender();
		loginServer.tradeLog(to, from, item, amount, x, y, type);
	}
}
