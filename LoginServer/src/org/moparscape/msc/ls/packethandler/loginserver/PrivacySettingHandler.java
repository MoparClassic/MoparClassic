package org.moparscape.msc.ls.packethandler.loginserver;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.model.World;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packethandler.PacketHandler;


public class PrivacySettingHandler implements PacketHandler {

    public void handlePacket(Packet p, IoSession session) throws Exception {
	World world = (World) session.getAttachment();
	Server server = Server.getServer();

	long user = p.readLong();
	boolean on = p.readByte() == 1;
	int idx = (int) p.readByte();
	switch (idx) {
	case 0: // Chat block
	    try {
		Server.db.updateQuery("UPDATE `pk_players` SET block_chat=" + (on ? 1 : 0) + " WHERE user='" + user + "'");
	    } catch (SQLException e) {
		Server.error(e.getMessage());
	    }
	    break;
	case 1: // Private block
	    try {
		Server.db.updateQuery("UPDATE `pk_players` SET block_private=" + (on ? 1 : 0) + " WHERE user='" + user + "'");
		ResultSet result = Server.db.getQuery("SELECT user FROM `pk_friends` WHERE friend='" + user + "' AND user NOT IN (SELECT friend FROM `pk_friends` WHERE user='" + user + "')");
		while (result.next()) {
		    long friend = result.getLong("user");
		    World w = server.findWorld(friend);
		    if (w != null) {
			if (on) {
			    w.getActionSender().friendLogout(friend, user);
			} else {
			    w.getActionSender().friendLogin(friend, user, world.getID());
			}
		    }
		}
	    } catch (SQLException e) {
		Server.error(e.getMessage());
	    }
	    break;
	case 2: // Trade block
	    try {
		Server.db.updateQuery("UPDATE `pk_players` SET block_trade=" + (on ? 1 : 0) + " WHERE user='" + user + "'");
	    } catch (SQLException e) {
		Server.error(e.getMessage());
	    }
	    break;
	case 3: // Duel block
	    try {
		Server.db.updateQuery("UPDATE `pk_players` SET block_duel=" + (on ? 1 : 0) + " WHERE user='" + user + "'");
	    } catch (SQLException e) {
		Server.error(e.getMessage());
	    }
	    break;
	}
	server.findSave(user, world).setPrivacySetting(idx, on);
    }

}
