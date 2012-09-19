package org.moparscape.msc.ls.packethandler.loginserver;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packethandler.PacketHandler;

public class Tradelog implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception {
		long from = p.readLong();
		long to = p.readLong();
		int item = p.readInt();
		long amount = p.readLong();
		int x = p.readInt();
		int y = p.readInt();
		int type = p.readInt();
		long date = (System.currentTimeMillis() / 1000);
		Server.storage.logTrade(from, to, item, amount, x, y, type, date);
	}
}
