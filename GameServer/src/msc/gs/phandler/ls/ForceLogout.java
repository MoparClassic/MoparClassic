package msc.gs.phandler.ls;

import org.apache.mina.common.IoSession;

import msc.gs.Instance;
import msc.gs.connection.LSPacket;
import msc.gs.connection.Packet;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.phandler.PacketHandler;
import msc.gs.util.Logger;

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