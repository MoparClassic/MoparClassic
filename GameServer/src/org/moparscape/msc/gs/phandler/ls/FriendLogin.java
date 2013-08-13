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
		long loggingIn = p.readLong();
		Player userToInform = world.getPlayer(p.readLong());
		if (userToInform == null) {
			return;
		}
		if (userToInform.isFriendsWith(loggingIn)) {
			userToInform.getActionSender().sendFriendUpdate(loggingIn, p.readShort());
		}
	}

}