package rsca.ls.packethandler.loginserver;

import java.sql.SQLException;

import org.apache.mina.common.IoSession;

import rsca.ls.Server;
import rsca.ls.model.World;
import rsca.ls.net.LSPacket;
import rsca.ls.net.Packet;
import rsca.ls.packetbuilder.loginserver.WorldRegisteredPacketBuilder;
import rsca.ls.packethandler.PacketHandler;
import rsca.ls.util.DataConversions;

public class RegisterWorld implements PacketHandler {
    private WorldRegisteredPacketBuilder builder = new WorldRegisteredPacketBuilder();

    public void handlePacket(Packet p, IoSession session) throws Exception {
	final long uID = ((LSPacket) p).getUID();
	builder.setUID(uID);
	builder.setSuccess(false);

	Server server = Server.getServer();
	if (((LSPacket) p).getID() == 1) {
	    int id = p.readShort();
	    if (server.getWorld(id) == null) {
		World world = server.getIdleWorld(id);
		if (world == null) {
		    world = new World(id, session);
		    server.registerWorld(world);
		    System.out.println("Registering world: " + id);
		    try {
			if (id == 1)
			    Server.db.updateQuery("UPDATE `pk_players` SET online=0");
		    } catch (SQLException e) {
			Server.error(e);
		    }

		} else {
		    world.setSession(session);
		    server.setIdle(world, false);
		    System.out.println("Reattached to world " + id);
		    try {
			Server.db.updateQuery("UPDATE `pk_players` SET online=0");
		    } catch (SQLException e) {
			Server.error(e);
		    }

		}
		int playerCount = p.readShort();
		for (int i = 0; i < playerCount; i++) {
		    world.registerPlayer(p.readLong(), DataConversions.IPToString(p.readLong()));
		}
		session.setAttachment(world);
		builder.setSuccess(true);
	    }
	} else {
	    World world = (World) session.getAttachment();

	    server.unregisterWorld(world);
	    System.out.println("UnRegistering world: " + world.getID());
	    session.setAttachment(null);
	    builder.setSuccess(true);
	}

	LSPacket temp = builder.getPacket();
	if (temp != null) {
	    session.write(temp);
	}
    }

}
