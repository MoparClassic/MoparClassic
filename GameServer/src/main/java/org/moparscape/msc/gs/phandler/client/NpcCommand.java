package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.Mob;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.plugins.extras.Thieving;

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
		if (affectedNpc == null
				|| affectedMob == null
				|| player == null
				|| !World.getQuestManager().isNpcVisible((Npc) affectedMob,
						player))
			return;
		if (!World.isMembers()) {
			player.getActionSender().sendMessage(
					"This feature is only avaliable on a members server");
			return;
		}

		Thieving thiev = new Thieving(player, affectedNpc, affectedMob);
		world.addEntryToSnapshots(new Activity(player.getUsername(), player
				.getUsername()
				+ " thieved a ("
				+ affectedNpc.getDef().name
				+ ")"));
		thiev.beginPickpocket();

	}

}
