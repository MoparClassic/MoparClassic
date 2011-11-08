package org.moparscape.msc.ls.packethandler.loginserver;

import java.util.List;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.model.World;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packethandler.PacketHandler;

public class PrivacySettingHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		World world = (World) session.getAttachment();
		Server server = Server.getServer();

		long user = p.readLong();
		boolean on = p.readByte() == 1;
		int idx = (int) p.readByte();
		int isOn = (on ? 1 : 0);
		switch (idx) {
		case 0: // Chat block
			Server.storage.chatBlock(isOn, user);
			break;
		case 1: // Private block
			Server.storage.privateBlock(isOn, user);
			List<Long> friends = Server.storage
					.getPrivateBlockFriendsOnline(user);
			for (long friend : friends) {
				World w = server.findWorld(friend);
				if (w != null) {
					if (on) {
						w.getActionSender().friendLogout(friend, user);
					} else {
						w.getActionSender().friendLogin(friend, user,
								world.getID());
					}
				}
			}
			break;
		case 2: // Trade block
			Server.storage.tradeBlock(isOn, user);
			break;
		case 3: // Duel block
			Server.storage.duelBlock(isOn, user);
			break;
		}
		server.findSave(user, world).setPrivacySetting(idx, on);
	}

}
