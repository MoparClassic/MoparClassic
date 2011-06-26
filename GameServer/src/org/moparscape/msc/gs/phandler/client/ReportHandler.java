package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.alert.AlertHandler;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.tools.DataConversions;

public class ReportHandler implements PacketHandler {

	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (!player.canReport()) {
			player.getActionSender().sendMessage(
					"You may only send one abuse report per minute.");
			return;
		}
		long temp = -121;
		byte b = 1;
		try {
			temp = p.readLong();
			b = p.readByte();
		} catch (Exception e) {
			return;
		} finally {
			if (temp == player.getUsernameHash()) {
				player.getActionSender().sendMessage(
						"You can't report yourself!");
				return;
			}
			AlertHandler.sendAlert(
					player.getUsername() + " sent a report about: "
							+ DataConversions.hashToUsername(temp), 1);
			// Instance.getServer().getLoginConnector().getActionSender().reportUser(player.getUsernameHash(),
			// temp, b);
			Instance.getReport().submitReport(player.getUsernameHash(), temp, b,
					player);
			player.setLastReport();
			world.addEntryToSnapshots(new Activity(player.getUsername(), player
					.getUsername()
					+ " sent a report about: "
					+ DataConversions.hashToUsername(temp)));
			player.getActionSender().sendMessage(
					"Your report has been received, thank you.");
		}

	}
}
