package msc.gs.phandler.ls;

import org.apache.mina.common.IoSession;

import msc.gs.Instance;
import msc.gs.connection.LSPacket;
import msc.gs.connection.Packet;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.phandler.PacketHandler;

public class FriendLogin implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();

    public void handlePacket(Packet p, IoSession session) throws Exception {
	long uID = ((LSPacket) p).getUID();
	Player player = world.getPlayer(p.readLong());
	if (player == null) {
	    return;
	}
	long friend = p.readLong();
	if (player.isFriendsWith(friend)) {
	    player.getActionSender().sendFriendUpdate(friend, p.readShort());
	}
    }

}