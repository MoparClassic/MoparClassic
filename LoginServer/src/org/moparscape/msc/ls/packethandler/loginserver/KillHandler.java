package org.moparscape.msc.ls.packethandler.loginserver;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packethandler.PacketHandler;

public class KillHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		long user = p.readLong();
		long killed = p.readLong();
		byte type = p.readByte();
		Server.storage.logKill(user, killed, type);
	}

}
