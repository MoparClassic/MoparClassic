package msc.gs.phandler.ls;

import org.apache.mina.common.IoSession;

import msc.gs.Instance;
import msc.gs.connection.LSPacket;
import msc.gs.connection.Packet;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.phandler.PacketHandler;

public class ReceivePM implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();

    public void handlePacket(Packet p, IoSession session) throws Exception {
	long uID = ((LSPacket) p).getUID();
	long sender = p.readLong();
	Player recipient = world.getPlayer(p.readLong());
	boolean avoidBlock = p.readByte() == 1;
	if (recipient == null || !recipient.loggedIn()) {
	    return;
	}
	if (recipient.getPrivacySetting(1) && !recipient.isFriendsWith(sender) && !avoidBlock) {
	    return;
	}
	if (recipient.isIgnoring(sender) && !avoidBlock) {
	    return;
	}
	recipient.getActionSender().sendPrivateMessage(sender, p.getRemainingData());
    }

}