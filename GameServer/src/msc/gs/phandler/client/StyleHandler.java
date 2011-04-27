package msc.gs.phandler.client;

import org.apache.mina.common.IoSession;

import msc.gs.Instance;
import msc.gs.connection.Packet;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.phandler.PacketHandler;

public class StyleHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();

    public void handlePacket(Packet p, IoSession session) throws Exception {
	Player player = (Player) session.getAttachment();
	int style = p.readByte();
	if (style < 0 || style > 3) {
	    player.setSuspiciousPlayer(true);
	    return;
	}
	player.setCombatStyle(style);
    }

}