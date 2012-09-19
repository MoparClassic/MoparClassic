package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.util.Logger;

public class Ping implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (p.getLength() > 0) {
			byte b = p.readByte();
			if (b == 1) { // 1 is for SCAR.
				if (player.getSessionFlags() < 1) {
					Logger.println(player.getUsername() + " is using SCAR!");
					player.setSessionFlags(player.getSessionFlags() + 1);
				}
			}
		}
	}
}
