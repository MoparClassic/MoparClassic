package org.moparscape.msc.ls.packethandler;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.net.Packet;


public interface PacketHandler {
    public void handlePacket(Packet p, IoSession session) throws Exception;
}
