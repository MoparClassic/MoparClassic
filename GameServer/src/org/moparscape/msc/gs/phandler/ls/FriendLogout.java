package org.moparscape.msc.gs.phandler.ls;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.LSPacket;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;

public class FriendLogout implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		long userToLogout = p.readLong();
		switch (((LSPacket) p).getID()) {
		case 12:
			for (Player player : world.getPlayers()) {
				if (player.isFriendsWith(userToLogout)) {
					player.getActionSender().sendFriendUpdate(userToLogout, 0);
				}
			}
			break;
		case 13:
			Player playerToInform = world.getPlayer(p.readLong());
			if (playerToInform != null && playerToInform.isFriendsWith(userToLogout)) {
				playerToInform.getActionSender().sendFriendUpdate(userToLogout, 0);
			}
			break;
		}
	}

}