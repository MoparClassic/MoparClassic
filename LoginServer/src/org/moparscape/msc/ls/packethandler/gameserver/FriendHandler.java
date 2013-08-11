package org.moparscape.msc.ls.packethandler.gameserver;

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

		World userWorld = server.findWorld(user);
		World friendWorld = server.findWorld(friend);

		PlayerSave save = server.findSave(user, world);
		switch (((LSPacket) p).getID()) {
		case 10: // Send PM
			boolean avoidBlock = p.readByte() == 1;
			byte[] message = p.getRemainingData();
			if (friendWorld != null) {
				friendWorld.getActionSender().sendPM(user, friend, avoidBlock,
						message);
			}
			break;
		case 11: // Add friend
			save.addFriend(friend);
			Server.storage.addFriend(user, friend);
			if (FriendsListService.canSee(user, friend)) {
				friendWorld.getActionSender().friendLogin(user, friend,
						userWorld.getID());
			}
			if (FriendsListService.canSee(friend, user)) {
				try {
					userWorld.getActionSender().friendLogin(friend, user,
							friendWorld == null ? 0 : friendWorld.getID());
				} catch (Exception e) {
				}
			}
			break;
		case 12: // Remove friend
			save.removeFriend(friend);
			Server.storage.removeFriend(user, friend);
			if (!FriendsListService.canSee(friend, user)) {
				friendWorld.getActionSender().friendLogout(user, friend);
			}
			break;
		case 13: // Add ignore
			save.addIgnore(friend);
			Server.storage.addIgnore(user, friend);
			try {
				if (!FriendsListService.canSee(friend, user)) {
					friendWorld.getActionSender().friendLogout(user, friend);
				}
			} catch (Exception e) {

			}
			break;
		case 14: // Remove ignore
			save.removeIgnore(friend);
			Server.storage.removeIgnore(user, friend);
			if (FriendsListService.canSee(user, friend)) {
				try {
					friendWorld.getActionSender().friendLogin(user, friend,
							userWorld.getID());
				} catch (Exception e) {
				}
			}
			break;
		}
	}
}
