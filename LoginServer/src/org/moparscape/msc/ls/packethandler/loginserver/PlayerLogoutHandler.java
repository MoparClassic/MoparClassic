package org.moparscape.msc.ls.packethandler.loginserver;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.model.World;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packethandler.PacketHandler;


public class PlayerLogoutHandler implements PacketHandler {

    public void handlePacket(Packet p, IoSession session) throws Exception {
	long user = p.readLong();
	World world = (World) session.getAttachment();
	world.unregisterPlayer(user);
    }
}
