package org.moparscape.msc.gs.phandler;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.connection.Packet;

public interface PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception;
}
