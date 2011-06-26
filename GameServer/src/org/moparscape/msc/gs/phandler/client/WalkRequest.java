package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.event.FightEvent;
import org.moparscape.msc.gs.event.MiniEvent;
import org.moparscape.msc.gs.event.WalkMobToMobEvent;
import org.moparscape.msc.gs.model.Mob;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Path;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.states.Action;
import org.moparscape.msc.gs.states.CombatState;

public class WalkRequest implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();
		if (player.inCombat()) {
			if (pID == 132) {
				Mob opponent = player.getOpponent();
				if (opponent == null) { // This shouldn't happen
					player.setSuspiciousPlayer(true);
					return;
				}
				if (opponent.getHitsMade() >= 3) {
					if (player.isDueling() && player.getDuelSetting(0)) {
						player.getActionSender().sendMessage(
								"Running has been disabled in this duel.");
						return;
					}
					player.setLastRun(GameEngine.getTime());
					player.resetCombat(CombatState.RUNNING);

					if (player.isInfected()
							&& GameEngine.getTime() - player.getLastMoved() < 1900) {
						final Packet newpacket = p;
						final IoSession newsession = session;
						Instance.getDelayedEventHandler().add(
								new MiniEvent(player, 2000) {
									public void action() {
										try {
											handlePacket(newpacket, newsession);
										} catch (Exception e) {
											return;
										}
									}
								});
					}
					player.isMining(false);
					if (opponent instanceof Npc) {
						Npc n = (Npc) opponent;
						n.unblock();
						opponent.resetCombat(CombatState.WAITING);
						if (n.getDef().aggressive) {
							player.setLastNpcChasingYou(n);
							Instance.getDelayedEventHandler().add(
									new MiniEvent(player, 2000) {
										public void action() {

											final Npc npc = owner
													.getLastNpcChasingYou();
											owner.setLastNpcChasingYou(null);
											if (npc.isBusy()
													|| npc.getChasing() != null)
												return;

											npc.resetPath();
											npc.setChasing(owner);

											Instance.getDelayedEventHandler()
													.add(new WalkMobToMobEvent(
															npc, owner, 0) {
														public void arrived() {
															if (affectedMob
																	.isBusy()
																	|| owner.isBusy()) {
																npc.setChasing(null);
																return;
															}
															if (affectedMob
																	.inCombat()
																	|| owner.inCombat()) {
																npc.setChasing(null);
																return;
															}
															Player player = (Player) affectedMob;
															player.resetPath();
															player.setBusy(true);
															npc.resetPath();
															player.resetAll();
															player.setStatus(Action.FIGHTING_MOB);
															player.getActionSender()
																	.sendSound(
																			"underattack");
															player.getActionSender()
																	.sendMessage(
																			"You are under attack!");

															npc.setLocation(
																	player.getLocation(),
																	true);
															for (Player p : npc
																	.getViewArea()
																	.getPlayersInView())
																p.removeWatchedNpc(npc);
															player.setBusy(true);
															player.setSprite(9);
															player.setOpponent(npc);
															player.setCombatTimer();

															npc.setBusy(true);
															npc.setSprite(8);
															npc.setOpponent(player);
															npc.setCombatTimer();
															npc.setChasing(null);
															FightEvent fighting = new FightEvent(
																	player,
																	npc, true);
															fighting.setLastRun(0);
															world.getDelayedEventHandler()
																	.add(fighting);
														}

														public void failed() {
															npc.setChasing(null);
														}
													});
										}
									});
						}

					} else {
						opponent.resetCombat(CombatState.WAITING);
					}
				} else {
					player.getActionSender()
							.sendMessage(
									"You cannot retreat in the first 3 rounds of battle.");
					return;
				}
			} else {
				return;
			}
		} else if (player.isBusy()
				&& GameEngine.getTime() - player.getLastMineTimer() > 2000) {
			return;
		}

		if (GameEngine.getTime() - player.getLastCast() < 600)
			return;

		player.isMining(false);
		player.resetAll();

		int startX = p.readShort();
		int startY = p.readShort();
		int numWaypoints = p.remaining() / 2;
		byte[] waypointXoffsets = new byte[numWaypoints];
		byte[] waypointYoffsets = new byte[numWaypoints];
		for (int x = 0; x < numWaypoints; x++) {
			waypointXoffsets[x] = p.readByte();
			waypointYoffsets[x] = p.readByte();
		}
		Path path = new Path(startX, startY, waypointXoffsets, waypointYoffsets);
		if (player.blink() && waypointXoffsets.length >= 1) {
			player.teleport((int) waypointXoffsets[waypointXoffsets.length - 1]
					+ startX,
					(int) waypointYoffsets[waypointYoffsets.length - 1]
							+ startY, false);
			return;
		}
		player.setStatus(Action.IDLE);
		player.setPath(path);
	}

}
