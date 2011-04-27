package msc.gs.phandler.ls;

import org.apache.mina.common.IoSession;

import msc.gs.Instance;
import msc.gs.builders.ls.StatRequestPacketBuilder;
import msc.gs.connection.LSPacket;
import msc.gs.connection.Packet;
import msc.gs.model.World;
import msc.gs.phandler.PacketHandler;
import msc.gs.util.Logger;

public class StatRequestHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();
    private StatRequestPacketBuilder builder = new StatRequestPacketBuilder();

    public void handlePacket(Packet p, IoSession session) throws Exception {
	long uID = ((LSPacket) p).getUID();
	Logger.event("LOGIN_SERVER requested stats (uID: " + uID + ")");
	builder.setUID(uID);
	LSPacket temp = builder.getPacket();
	if (temp != null) {
	    session.write(temp);
	}
    }

}