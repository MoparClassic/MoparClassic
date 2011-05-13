package org.moparscape.msc.ls.packethandler.loginserver;

import java.sql.SQLException;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packethandler.PacketHandler;


public class KillHandler implements PacketHandler {

    public void handlePacket(Packet p, IoSession session) throws Exception {
	try {
	    Server.db.updateQuery("INSERT INTO `pk_kills`(`user`, `killed`, `time`, `type`) VALUES('" + p.readLong() + "', '" + p.readLong() + "', " + (int) (System.currentTimeMillis() / 1000) + ", " + p.readByte() + ")");
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

}
