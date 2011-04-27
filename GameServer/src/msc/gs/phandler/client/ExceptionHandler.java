package msc.gs.phandler.client;

import org.apache.mina.common.IoSession;

import msc.gs.Instance;
import msc.gs.connection.Packet;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.phandler.PacketHandler;
import msc.gs.util.Logger;

public class ExceptionHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();

    public void handlePacket(Packet p, IoSession session) throws Exception {
    	try {
	    	Player player = (Player) session.getAttachment();
			Logger.error("[CLIENT] Exception from " + player.getUsername() + ": " + p.readString());
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	
		//player.getActionSender().sendLogout();
		//player.destroy(false);
    }
}