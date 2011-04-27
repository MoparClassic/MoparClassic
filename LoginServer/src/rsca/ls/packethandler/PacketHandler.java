package rsca.ls.packethandler;

import org.apache.mina.common.IoSession;

import rsca.ls.net.Packet;

public interface PacketHandler {
    public void handlePacket(Packet p, IoSession session) throws Exception;
}
