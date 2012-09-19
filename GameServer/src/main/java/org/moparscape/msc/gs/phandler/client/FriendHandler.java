package org.moparscape.msc.gs.phandler.client;

import java.util.ArrayList;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.alert.AlertHandler;
import org.moparscape.msc.gs.builders.ls.MiscPacketBuilder;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.model.snapshot.Chatlog;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.tools.DataConversions;
import org.moparscape.msc.gs.util.Logger;

public class FriendHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();
	private MiscPacketBuilder loginSender = Instance.getServer()
			.getLoginConnector().getActionSender();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();

		long user = player.getUsernameHash();
		long friend = p.readLong();
		switch (pID) {
		case 168: // Add friend
			if (player.friendCount() >= 200) {
				player.getActionSender().sendMessage(
						"Your friend list is too full");
				return;
			}
			loginSender.addFriend(user, friend);
			player.addFriend(friend, 0);
			world.addEntryToSnapshots(new Activity(player.getUsername(), player
					.getUsername()
					+ " added friend "
					+ DataConversions.hashToUsername(friend)
					+ " at: "
					+ player.getX() + "/" + player.getY()));

			break;
		case 52: // Remove friend
			loginSender.removeFriend(user, friend);
			player.removeFriend(friend);
			world.addEntryToSnapshots(new Activity(player.getUsername(), player
					.getUsername()
					+ " removed friend "
					+ DataConversions.hashToUsername(friend)
					+ " at: "
					+ player.getX() + "/" + player.getY()));

			break;
		case 25: // Add ignore
			if (player.ignoreCount() >= 200) {
				player.getActionSender().sendMessage(
						"Your ignore list is too full");
				return;
			}
			loginSender.addIgnore(user, friend);
			player.addIgnore(friend);
			break;
		case 108: // Remove ignore
			loginSender.removeIgnore(user, friend);
			player.removeIgnore(friend);
			break;
		case 254: // Send PM
			try {
				byte[] data = p.getRemainingData();
				String s = DataConversions.byteToString(data, 0, data.length);
				s = s.toLowerCase();
				String k = s;
				s = s.replace(" ", "");
				s = s.replace(".", "");
				if (s.contains("runeblast")) {
					Logger.println(player.getUsername() + " pmed "
							+ DataConversions.hashToUsername(friend) + ":" + k);
					AlertHandler.sendAlert(player.getUsername() + " pmed "
							+ DataConversions.hashToUsername(friend) + ":" + k,
							2);
					return;
				}
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(DataConversions.hashToUsername(friend));
				world.addEntryToSnapshots(new Chatlog(player.getUsername(),
						"(PM) " + k, temp));

				loginSender.sendPM(user, friend, player.isPMod(), data);
			} catch (NegativeArraySizeException e) {
				player.destroy(false);
			}
			break;
		}
	}

}
