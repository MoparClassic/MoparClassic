package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.util.Logger;

public class ExceptionHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		try {
			Player player = (Player) session.getAttachment();
			Logger.error("[CLIENT] Exception from " + player.getUsername()
					+ ": " + p.readString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// player.getActionSender().sendLogout();
		// player.destroy(false);
	}
}