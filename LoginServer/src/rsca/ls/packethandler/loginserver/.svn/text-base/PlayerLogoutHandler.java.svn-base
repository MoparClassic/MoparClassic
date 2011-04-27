package rsca.ls.packethandler.loginserver;

import org.apache.mina.common.IoSession;

import rsca.ls.model.World;
import rsca.ls.net.Packet;
import rsca.ls.packethandler.PacketHandler;

public class PlayerLogoutHandler implements PacketHandler {

    public void handlePacket(Packet p, IoSession session) throws Exception {
	long user = p.readLong();
	World world = (World) session.getAttachment();
	world.unregisterPlayer(user);
    }
}
