package msc.gs.phandler.ls;

import org.apache.mina.common.IoSession;

import msc.gs.Instance;
import msc.gs.builders.ls.ReportInfoRequestPacketBuilder;
import msc.gs.connection.LSPacket;
import msc.gs.connection.Packet;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.phandler.PacketHandler;
import msc.gs.util.Logger;

public class ReportInfoRequestHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();
    private ReportInfoRequestPacketBuilder builder = new ReportInfoRequestPacketBuilder();

    public void handlePacket(Packet p, IoSession session) throws Exception {
	long uID = ((LSPacket) p).getUID();
	Logger.event("LOGIN_SERVER requested report information (uID: " + uID + ")");
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