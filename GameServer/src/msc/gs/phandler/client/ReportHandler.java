package msc.gs.phandler.client;

import org.apache.mina.common.IoSession;

import msc.gs.Instance;
import msc.gs.connection.Packet;
import msc.gs.db.ReportHandlerQueries;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.model.snapshot.Activity;
import msc.gs.phandler.PacketHandler;
import msc.gs.tools.DataConversions;

public class ReportHandler implements PacketHandler {

	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (!player.canReport()) {
			player.getActionSender().sendMessage("You may only send one abuse report per minute.");
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
			if(temp == player.getUsernameHash()) {
				player.getActionSender().sendMessage("You can't report yourself!");
				return;
			}
			Instance.getIRC().handleReport(player.getUsername(), DataConversions.hashToUsername(temp), b);
			//Instance.getServer().getLoginConnector().getActionSender().reportUser(player.getUsernameHash(), temp, b);
			Instance.getReport().submitRepot(player.getUsernameHash(), temp, b, player);
			player.setLastReport();
			world.addEntryToSnapshots(new Activity(player.getUsername(),player.getUsername() + " sent a repot about: " + DataConversions.hashToUsername(temp)));
			player.getActionSender().sendMessage("Your report has been received, thank you.");
		}

	}
}
