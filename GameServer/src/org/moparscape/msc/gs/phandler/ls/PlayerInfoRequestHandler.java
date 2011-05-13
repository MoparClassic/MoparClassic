package org.moparscape.msc.gs.phandler.ls;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.builders.ls.PlayerInfoRequestPacketBuilder;
import org.moparscape.msc.gs.connection.LSPacket;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.util.Logger;


public class PlayerInfoRequestHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();
    private PlayerInfoRequestPacketBuilder builder = new PlayerInfoRequestPacketBuilder();

    public void handlePacket(Packet p, IoSession session) throws Exception {
	long uID = ((LSPacket) p).getUID();
	Logger.event("LOGIN_SERVER requested player information (uID: " + uID + ")");
	builder.setUID(uID);
	builder.setPlayer(world.getPlayer(p.readLong()));
	LSPacket temp = builder.getPacket();
	if (temp != null) {
	    session.write(temp);
	}
    }

}