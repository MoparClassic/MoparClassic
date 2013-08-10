package org.moparscape.msc.ls.packethandler.frontend;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.model.World;
import org.moparscape.msc.ls.net.FPacket;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packetbuilder.FPacketBuilder;
import org.moparscape.msc.ls.packethandler.PacketHandler;


public class Global implements PacketHandler {
    private static final FPacketBuilder builder = new FPacketBuilder();

    public void handlePacket(Packet p, IoSession session) throws Exception {
	String[] params = ((FPacket) p).getParameters();
	try {
	    String message = params[0];
	    for (World w : Server.getServer().getWorlds()) {
		w.getActionSender().alert(message);
	    }
	    builder.setID(1);
	} catch (Exception e) {
	    builder.setID(0);
	}
	FPacket packet = builder.toPacket();
	if (packet != null) {
	    session.write(packet);
	}
    }

}