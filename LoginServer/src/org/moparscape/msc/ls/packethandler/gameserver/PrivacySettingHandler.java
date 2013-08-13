package org.moparscape.msc.ls.packethandler.gameserver;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.model.World;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packethandler.PacketHandler;
import org.moparscape.msc.ls.service.FriendsListService;

public class PrivacySettingHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		World world = (World) session.getAttachment();
		Server server = Server.getServer();

		long user = p.readLong();
		boolean on = p.readByte() == 1;
		int idx = (int) p.readByte();
		switch (idx) {
		case 0: // Chat block
			break;
		case 1: // Private block
			if (world.getSave(user).blockPrivate() && !on) {
				FriendsListService.turnOffPrivate(user);
			} else if (!world.getSave(user).blockPrivate() && on) {
				FriendsListService.turnOnPrivate(user);
			}
			break;
		case 2: // Trade block
			break;
		case 3: // Duel block
			break;
		}
		server.findSave(user, world).setPrivacySetting(idx, on);
	}

}
