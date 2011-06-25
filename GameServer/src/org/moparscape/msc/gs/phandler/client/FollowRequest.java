package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;

public class FollowRequest implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		Player affectedPlayer = world.getPlayer(p.readShort());
		if (affectedPlayer == null) {
			player.setSuspiciousPlayer(true);
			return;
		}
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		if (GameEngine.getTime() - player.getLastRun() < 3000)
			return;
		player.resetAll();
		player.setFollowing(affectedPlayer, 1);
		player.getActionSender().sendMessage(
				"Now following " + affectedPlayer.getUsername());
		world.addEntryToSnapshots(new Activity(player.getUsername(), player
				.getUsername()
				+ " started to follow "
				+ affectedPlayer.getUsername()
				+ " at: "
				+ player.getX()
				+ "/"
				+ player.getY()
				+ " | "
				+ affectedPlayer.getX()
				+ "/"
				+ affectedPlayer.getY()));

	}
}
