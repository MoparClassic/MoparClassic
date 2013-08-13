package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;

public class ChatHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player sender = (Player) session.getAttachment();
		if (World.SERVER_MUTED && !sender.isMod()) {
			sender.getActionSender().sendMessage(
					"@red@Unable to chat while the server is muted");
			return;
		}

		sender.addMessageToChatQueue(p.getData());
	}

}