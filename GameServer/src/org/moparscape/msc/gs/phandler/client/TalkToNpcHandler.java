package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.config.Formulae;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.event.WalkToMobEvent;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.service.DialogService;
import org.moparscape.msc.gs.states.Action;

public class TalkToNpcHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		if (GameEngine.getTime() - player.getLastNPCChat() < 1500)
			return;
		player.setLastNPCChat(GameEngine.getTime());
		player.resetAll();
		final Npc affectedNpc = world.getNpc(p.readShort());
		if (affectedNpc == null) {
			return;
		}
		world.addEntryToSnapshots(new Activity(player.getUsername(), player
				.getUsername()
				+ " talked to NPC ("
				+ affectedNpc.getID()
				+ ") at: "
				+ player.getX()
				+ "/"
				+ player.getY()
				+ "|"
				+ affectedNpc.getX() + "/" + affectedNpc.getY()));

		player.setFollowing(affectedNpc);
		player.setStatus(Action.TALKING_MOB);
		Instance.getDelayedEventHandler().add(
				new WalkToMobEvent(player, affectedNpc, 1) {
					public void arrived() {
						owner.resetFollowing();
						owner.resetPath();
						if (owner.isBusy() || owner.isRanging()
								|| !owner.nextTo(affectedNpc)
								|| owner.getStatus() != Action.TALKING_MOB) {
							return;
						}
						owner.resetAll();
						if (affectedNpc.isBusy()) {
							owner.getActionSender().sendMessage(
									affectedNpc.getDef().getName()
											+ " is currently busy.");
							return;
						}
						affectedNpc.resetPath();
						if (Formulae.getDirection(owner, affectedNpc) != -1) {
							affectedNpc.setSprite(Formulae.getDirection(owner,
									affectedNpc));
							owner.setSprite(Formulae.getDirection(affectedNpc,
									owner));
						}

						DialogService.talk(affectedNpc, owner);

					}

				});
	}
}