package msc.gs.phandler.client;

import org.apache.mina.common.IoSession;

import msc.gs.builders.RSCPacketBuilder;
import msc.gs.connection.Packet;
import msc.gs.model.Player;
import msc.gs.phandler.PacketHandler;

public class DummyPacket implements PacketHandler {

    public void handlePacket(Packet p, IoSession session) throws Exception {
	try {
	    if (p.getLength() > 2) { // 1 for byte, 2 for short
		byte b = p.readByte();
		int clientVersion = p.readShort();
		if (clientVersion > 34 && clientVersion < 40) {
		    RSCPacketBuilder pb = new RSCPacketBuilder();
		    pb.setBare(true);
		    pb.addByte((byte) 4); // client update
		    session.write(pb.toPacket());
		    Player player = (Player) session.getAttachment();
		    player.destroy(true);
		}
	    }
	} catch (Exception e) {

	}
    }
}
