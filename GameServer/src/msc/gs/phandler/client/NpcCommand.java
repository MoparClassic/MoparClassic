package msc.gs.phandler.client;

import org.apache.mina.common.IoSession;

import msc.gs.Instance;
import msc.gs.connection.Packet;
import msc.gs.model.Mob;
import msc.gs.model.Npc;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.model.snapshot.Activity;
import msc.gs.phandler.PacketHandler;
import msc.gs.plugins.extras.Thieving;

public class NpcCommand implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();

    public void handlePacket(Packet p, IoSession session) throws Exception {
	int serverIndex = p.readShort();
	final Player player = (Player) session.getAttachment();
	if (player.isBusy()) {
	    return;
	}
	final Mob affectedMob = world.getNpc(serverIndex);
	final Npc affectedNpc = (Npc) affectedMob;
	if (affectedNpc == null || affectedMob == null || player == null || !world.getQuestManager().isNpcVisible((Npc) affectedMob, player))
	    return;
	final int npcID = affectedNpc.getID();
	if (!World.isMembers()) {
	    player.getActionSender().sendMessage("This feature is only avaliable on a members server");
	    return;
	}

	Thieving thiev = new Thieving(player, affectedNpc, affectedMob);
	world.addEntryToSnapshots(new Activity(player.getUsername(), player.getUsername() + " thieved a (" + affectedNpc.getDef().name + ")"));
	thiev.beginPickpocket();

    }

}
