package org.moparscape.msc.gs.phandler.ls;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.builders.ls.ReportInfoRequestPacketBuilder;
import org.moparscape.msc.gs.connection.LSPacket;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.util.Logger;

public class ReportInfoRequestHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();
	private ReportInfoRequestPacketBuilder builder = new ReportInfoRequestPacketBuilder();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		long uID = ((LSPacket) p).getUID();
		Logger.event("LOGIN_SERVER requested report information (uID: " + uID
				+ ")");
		Player player = world.getPlayer(p.readLong());
		if (player == null) {
			return;
		}
		builder.setUID(uID);
		builder.setPlayer(player);
		LSPacket temp = builder.getPacket();
		if (temp != null) {
			session.write(temp);
		}
	}

}