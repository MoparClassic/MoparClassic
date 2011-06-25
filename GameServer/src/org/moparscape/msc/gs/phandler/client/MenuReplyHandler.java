package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.MenuHandler;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;

public class MenuReplyHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		MenuHandler menuHandler = player.getMenuHandler();
		if (menuHandler == null) {
			player.setSuspiciousPlayer(true);
			return;
		}
		int option = (int) p.readByte();
		String reply = menuHandler.getOption(option);
		player.resetMenuHandler();
		if (reply == null) {
			player.setSuspiciousPlayer(true);
			return;
		}
		menuHandler.handleReply(option, reply);
	}
}
