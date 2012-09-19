package org.moparscape.msc.gs.phandler.client;

import java.lang.reflect.InvocationTargetException;
import java.util.ConcurrentModificationException;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.event.WalkToMobEvent;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.Script;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.npchandler.NpcHandler;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.states.Action;
import org.moparscape.msc.gs.util.Logger;

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
		player.setLastQuestMenuReply(-2);
		player.setLastNPCChat(GameEngine.getTime());
		player.resetAll();
		final Npc affectedNpc = world.getNpc(p.readShort());
		if (affectedNpc == null
				|| !World.getQuestManager().isNpcVisible(affectedNpc, player)) {
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
					@SuppressWarnings("deprecation")
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
						NpcHandler handler = world.getNpcHandler(affectedNpc
								.getID());

						if (Formulae.getDirection(owner, affectedNpc) != -1) {
							affectedNpc.setSprite(Formulae.getDirection(owner,
									affectedNpc));
							owner.setSprite(Formulae.getDirection(affectedNpc,
									owner));
						}
						if (handler != null) {
							try {
								handler.handleNpc(affectedNpc, owner);
							} catch (Exception e) {
								Logger.error("Exception with npc["
										+ affectedNpc.getIndex() + "] from "
										+ owner.getUsername() + " ["
										+ owner.getCurrentIP() + "]: "
										+ e.getMessage());
								owner.getActionSender().sendLogout();
								owner.destroy(false);
							}
						} else {

							if (!World.getQuestManager().handleNpcTalk(
									affectedNpc, owner)) {
								if (affectedNpc.getDef().isAttackable())
									owner.getActionSender()
											.sendMessage(
													"The "
															+ affectedNpc
																	.getDef()
																	.getName()
															+ " doesn't appear interested in talking.");
								else if (world.npcScripts
										.containsKey(affectedNpc.getID())) {
									owner.setBusy(true);
									affectedNpc.blockedBy(owner);
									if (owner.getInterpreterThread() != null) {
										try {
											owner.getInterpreterThread().stop();
										} catch (Exception e) {

										}
									}

									owner.setInterpreterThread(new Thread(
											new Runnable() {
												public void run() {
													try {
														try {
															new Script(owner,
																	affectedNpc);
														} catch (ConcurrentModificationException cme) {
															Logger.println("CME (Ignore This): "
																	+ owner.getUsername());
														} catch (Exception e) {

														}
														owner.setBusy(false);

														affectedNpc.unblock();
													} catch (Exception e) {
														if (!(e instanceof InvocationTargetException)) {
															e.printStackTrace();
														}
														Logger.println(affectedNpc
																.getID());
														affectedNpc.unblock();
														owner.setBusy(false);
													}
												}
											}));
									owner.getInterpreterThread().start();
								} else {
									try {
										NpcHandler hand = new org.moparscape.msc.gs.npchandler.OtherNPC();
										hand.handleNpc(affectedNpc, owner);
									} catch (Exception e) {
										Logger.error("Exception with npc["
												+ affectedNpc.getIndex()
												+ "] from "
												+ owner.getUsername() + " ["
												+ owner.getCurrentIP() + "]: "
												+ e.getMessage());
										owner.getActionSender().sendLogout();
										owner.destroy(false);
									}
								}
							}

						}
					}
				});
	}
}
