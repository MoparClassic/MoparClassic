package rsca.ls.packethandler.frontend;

import org.apache.mina.common.IoSession;

import rsca.ls.Server;
import rsca.ls.model.World;
import rsca.ls.net.FPacket;
import rsca.ls.net.Packet;
import rsca.ls.packetbuilder.FPacketBuilder;
import rsca.ls.packethandler.PacketHandler;

public class Alert implements PacketHandler {
    private static final FPacketBuilder builder = new FPacketBuilder();

    public void handlePacket(Packet p, IoSession session) throws Exception {
	String[] params = ((FPacket) p).getParameters();
	try {
	    long usernameHash = Long.parseLong(params[0]);
	    World world = Server.getServer().findWorld(usernameHash);
	    if (world == null) {
		throw new Exception("World not found");
	    }
	    world.getActionSender().alert(usernameHash, params[1]);
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