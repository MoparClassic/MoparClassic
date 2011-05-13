package org.moparscape.msc.ls.packethandler.loginserver;

import java.sql.SQLException;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.model.World;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packethandler.PacketHandler;


public class ReportHandler implements PacketHandler {

    public void handlePacket(Packet p, IoSession session) throws Exception {
	World world = (World) session.getAttachment();

	final long user = p.readLong();
	final long reported = p.readLong();
	final byte reason = p.readByte();
	world.getActionSender().requestReportInfo(reported, new PacketHandler() {
	    public void handlePacket(Packet p, IoSession session) throws Exception {
		int x = p.readShort();
		int y = p.readShort();
		String status = p.readString();
		try {
		    Server.db.updateQuery("INSERT INTO `pk_reports`(`from`, `about`, `time`, `reason`, `x`, `y`, `status`) VALUES('" + user + "', '" + reported + "', '" + (System.currentTimeMillis() / 1000) + "', '" + reason + "', '" + x + "', '" + y + "', '" + status + "')");
		} catch (SQLException e) {
		    Server.error(e);
		}
	    }
	});
    }

}
