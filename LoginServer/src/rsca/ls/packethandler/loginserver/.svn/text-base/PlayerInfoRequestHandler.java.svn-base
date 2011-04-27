package rsca.ls.packethandler.loginserver;

import org.apache.mina.common.IoSession;

import rsca.ls.Server;
import rsca.ls.model.World;
import rsca.ls.net.LSPacket;
import rsca.ls.net.Packet;
import rsca.ls.packetbuilder.LSPacketBuilder;
import rsca.ls.packethandler.PacketHandler;

public class PlayerInfoRequestHandler implements PacketHandler {

    public void handlePacket(Packet p, final IoSession session) throws Exception {
	final long uID = ((LSPacket) p).getUID();
	final long user = p.readLong();
	final World w = Server.getServer().findWorld(user);
	if (w == null) {
	    LSPacketBuilder builder = new LSPacketBuilder();
	    builder.setUID(uID);
	    builder.addByte((byte) 0);
	    session.write(builder.toPacket());
	    return;
	}
	w.getActionSender().requestPlayerInfo(user, new PacketHandler() {
	    public void handlePacket(Packet p, IoSession s) throws Exception {
		LSPacketBuilder builder = new LSPacketBuilder();
		builder.setUID(uID);
		if (p.readByte() == 0) {
		    builder.addByte((byte) 0);
		} else {
		    builder.addByte((byte) 1);
		    builder.addShort(w == null ? 0 : w.getID());
		    builder.addBytes(p.getRemainingData());
		}
		session.write(builder.toPacket());
	    }
	});

    }

}