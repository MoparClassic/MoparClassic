package org.moparscape.msc.gs.phandler.ls;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.LSPacket;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.util.Logger;


public class ForceLogout implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();

    public void handlePacket(Packet p, IoSession session) throws Exception {
	long uID = ((LSPacket) p).getUID();
	Logger.event("LOGIN_SERVER requested player logout (uID: " + uID + ")");
	Player player = world.getPlayer(p.readLong());
	if (player != null) {
	    player.getActionSender().sendLogout();
	    player.destroy(true);
	}
    }

}