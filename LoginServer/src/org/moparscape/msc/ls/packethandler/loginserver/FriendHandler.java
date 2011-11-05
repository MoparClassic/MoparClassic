package org.moparscape.msc.ls.packethandler.loginserver;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.model.PlayerSave;
import org.moparscape.msc.ls.model.World;
import org.moparscape.msc.ls.net.LSPacket;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packethandler.PacketHandler;

public class FriendHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		World world = (World) session.getAttachment();
		Server server = Server.getServer();

		long user = p.readLong();
		long friend = p.readLong();

		World w;
		PlayerSave save = server.findSave(user, world);
		switch (((LSPacket) p).getID()) {
		case 10: // Send PM
			boolean avoidBlock = p.readByte() == 1;
			byte[] message = p.getRemainingData();
			w = server.findWorld(friend);
			if (w != null) {
				w.getActionSender().sendPM(user, friend, avoidBlock, message);
			}
			break;
		case 11: // Add friend
			save.addFriend(friend);
			Server.storage.addFriend(user, friend);
			if (Server.storage.addFriend_isOnline0(user, friend)) {
				w = server.findWorld(friend);
				if (w != null) {
					world.getActionSender()
							.friendLogin(user, friend, w.getID());
				}
			}
			if (Server.storage.addFriend_isOnline1(friend, user)) {
				w = server.findWorld(friend);
				if (w != null) {
					w.getActionSender()
							.friendLogin(friend, user, world.getID());
				}
			}
			break;
		case 12: // Remove friend
			save.removeFriend(friend);
			Server.storage.removeFriend(user, friend);
			if (Server.storage.removeFriend_isOnline(user)) {
				w = server.findWorld(friend);
				if (w != null) {
					w.getActionSender().friendLogout(friend, user);
				}
			}
			break;
		case 13: // Add ignore
			save.addIgnore(friend);
			Server.storage.addIgnore(user, friend);
			break;
		case 14: // Remove ignore
			save.removeIgnore(friend);
			Server.storage.removeIgnore(user, friend);
			break;
		}
	}

}
