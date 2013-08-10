package org.moparscape.msc.gs.phandler.ls;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;

public class FriendLogin implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = world.getPlayer(p.readLong());
		if (player == null) {
			return;
		}
		long friend = p.readLong();
		if (player.isFriendsWith(friend)) {
			player.getActionSender().sendFriendUpdate(friend, p.readShort());
		}
	}

}