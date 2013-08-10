package org.moparscape.msc.ls.packethandler.loginserver;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.model.PlayerSave;
import org.moparscape.msc.ls.model.World;
import org.moparscape.msc.ls.net.LSPacket;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packethandler.PacketHandler;
import org.moparscape.msc.ls.service.FriendsListService;

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
			if (FriendsListService.isVisible(user, friend)) {
				w = server.findWorld(user);
				w.getActionSender().friendLogin(user, friend, w.getID());
			}
			if (FriendsListService.isVisible(friend, user)) {
				try {
					w = server.findWorld(friend);
					w.getActionSender().friendLogin(friend, user, w.getID());
				} catch (Exception e) {
				}
			}
			break;
		case 12: // Remove friend
			save.removeFriend(friend);
			Server.storage.removeFriend(user, friend);
			if (FriendsListService.isVisible(user, friend)) {
				w = server.findWorld(friend);
				w.getActionSender().friendLogout(friend, user);
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
