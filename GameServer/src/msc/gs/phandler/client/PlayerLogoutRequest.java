package msc.gs.phandler.client;

import org.apache.mina.common.IoSession;

import msc.gs.Instance;
import msc.gs.connection.Packet;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.phandler.PacketHandler;

public class PlayerLogoutRequest implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();

    public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (player.canLogout()) {
			player.destroy(true, true);
		} 
		else {
		    player.getActionSender().sendCantLogout();
		}
    }
}
