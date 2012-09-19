package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.config.Config;
import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.event.FightEvent;
import org.moparscape.msc.gs.event.RangeEvent;
import org.moparscape.msc.gs.event.WalkToMobEvent;
import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Mob;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.PathGenerator;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.states.Action;

public class AttackHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		Mob affectedMob = null;
		int serverIndex = p.readShort();
		if (pID == 57) {
			affectedMob = world.getPlayer(serverIndex);
		} else if (pID == 73) {
			affectedMob = world.getNpc(serverIndex);
		}
		if (player.isPMod() && !player.isMod())
			return;
		if (affectedMob == null
				|| affectedMob.equals(player)
				|| (affectedMob instanceof Npc && !World.getQuestManager()
						.isNpcVisible((Npc) affectedMob, player))) {
			player.resetPath();
			return;
		}
		if (affectedMob instanceof Npc && player.getRangeEquip() > 0
				&& affectedMob.inCombat()
				&& World.getQuestManager().isNpcAssociated(affectedMob, player)) {
			player.getActionSender().sendMessage(
					"You can't range the "
							+ ((Npc) affectedMob).getDef().getName()
							+ " while it's in combat!");
			player.resetPath();
			return;
		}
		if (affectedMob instanceof Player) {
			Player pl = (Player) affectedMob;
			if (pl.inCombat() && player.getRangeEquip() < 0) {
				return;
			}

			if (pl.getLocation().inWilderness()
					&& System.currentTimeMillis() - pl.getLastRun() < 3000) {
				return;
			}
		}

		player.setFollowing(affectedMob);
		player.setStatus(Action.ATTACKING_MOB);

		if (player.getRangeEquip() < 0) {
			Instance.getDelayedEventHandler().add(
					new WalkToMobEvent(player, affectedMob,
							affectedMob instanceof Npc ? 1 : 2) {
						public void arrived() {
							owner.resetPath();
							owner.resetFollowing();
							boolean cont = false;
							if (affectedMob instanceof Player) {
								Player opp = (Player) affectedMob;

								if (GameEngine.getTime()
										- opp.getLastMineTimer() < 2000
										&& opp.isBusy())
									cont = true;
							}
							if (affectedMob instanceof Player) {
								world.addEntryToSnapshots(new Activity(owner
										.getUsername(), owner.getUsername()
										+ " attacked a Player ("
										+ ((Player) affectedMob).getUsername()
										+ ")"));
							} else {
								world.addEntryToSnapshots(new Activity(owner
										.getUsername(), owner.getUsername()
										+ " attacked a NPC ("
										+ ((Npc) affectedMob).getDef().name
										+ ")"));
							}
							if (cont) {
								if (owner.isBusy()
										|| !owner.nextTo(affectedMob)
										|| !owner.checkAttack(affectedMob,
												false)
										|| owner.getStatus() != Action.ATTACKING_MOB) {
									return;
								}
							} else {
								if (owner.isBusy()
										|| affectedMob.isBusy()
										|| !owner.nextTo(affectedMob)
										|| !owner.checkAttack(affectedMob,
												false)
										|| owner.getStatus() != Action.ATTACKING_MOB) {
									return;
								}
							}
							if (affectedMob.getID() == 35) {
								owner.getActionSender()
										.sendMessage(
												"Delrith can not be attacked without the Silverlight sword");
								return;
							}
							if (affectedMob.getID() == 140
									&& affectedMob.getX() > 327
									&& affectedMob.getX() < 335
									&& affectedMob.getY() > 433
									&& affectedMob.getY() < 439) {
								owner.informOfNpcMessage(new ChatMessage(
										affectedMob, "a curse be upon you",
										owner));
								for (int i = 0; i < 3; i++) {
									int stat = owner.getCurStat(i);
									if (stat < 3)
										owner.setCurStat(i, 0);
									else
										owner.setCurStat(i, stat - 3);
								}
								owner.getActionSender().sendStats();

							}

							owner.resetAll();
							owner.setStatus(Action.FIGHTING_MOB);
							if (affectedMob instanceof Player) {
								Player affectedPlayer = (Player) affectedMob;
								affectedPlayer.resetAll();
								affectedPlayer.setStatus(Action.FIGHTING_MOB);
								affectedPlayer.getActionSender().sendSound(
										"underattack");
								affectedPlayer.getActionSender().sendMessage(
										"You are under attack!");
							}
							affectedMob.resetPath();

							owner.setLocation(affectedMob.getLocation(), true);
							for (Player p : owner.getViewArea()
									.getPlayersInView()) {
								p.removeWatchedPlayer(owner);
							}

							owner.setBusy(true);
							owner.setSprite(9);
							/*
							 * if(affectedMob instanceof Npc) { Npc n =
							 * (Npc)affectedMob; for(Fighter p : n.fighters) {
							 * p.useCombat = true; if(p.player == owner)
							 * if(p.useMagic) { p.useMagic = false; break; } } }
							 */
							owner.setOpponent(affectedMob);
							owner.setCombatTimer();
							affectedMob.setBusy(true);
							affectedMob.setSprite(8);
							affectedMob.setOpponent(owner);
							affectedMob.setCombatTimer();
							FightEvent fighting = new FightEvent(owner,
									affectedMob);
							fighting.setLastRun(0);
							Instance.getDelayedEventHandler().add(fighting);
						}
					});
		} else {
			if (!new PathGenerator(player.getX(), player.getY(),
					affectedMob.getX(), affectedMob.getY()).isValid()) {
				player.getActionSender().sendMessage(
						"I can't get a clear shot from here");
				player.resetPath();
				return;
			}
			if (Config.f2pWildy && player.getLocation().inWilderness()) {

				for (InvItem i : player.getInventory().getItems()) {
					if (i.getID() == 638 || i.getID() == 640
							|| i.getID() == 642 || i.getID() == 644
							|| i.getID() == 646) {
						player.getActionSender()
								.sendMessage(
										"You can not have any P2P arrows in your inventory in a F2P wilderness");
						return;
					}
				}

			}
			int radius = 7;
			if (player.getRangeEquip() == 59 || player.getRangeEquip() == 60)
				radius = 5;
			if (player.getRangeEquip() == 189)
				radius = 4;
			Instance.getDelayedEventHandler().add(
					new WalkToMobEvent(player, affectedMob, radius) {
						public void arrived() {
							owner.resetPath();
							if (owner.isBusy()
									|| !owner.checkAttack(affectedMob, true)
									|| owner.getStatus() != Action.ATTACKING_MOB) {
								return;
							}

							if (!new PathGenerator(owner.getX(), owner.getY(),
									affectedMob.getX(), affectedMob.getY())
									.isValid()) {
								owner.getActionSender().sendMessage(
										"I can't get a clear shot from here");
								owner.resetPath();
								return;
							}
							if (affectedMob instanceof Player) {
								world.addEntryToSnapshots(new Activity(owner
										.getUsername(), owner.getUsername()
										+ " ranged a Player ("
										+ ((Player) affectedMob).getUsername()
										+ ")"));
							} else {
								world.addEntryToSnapshots(new Activity(owner
										.getUsername(), owner.getUsername()
										+ " ranged a NPC ("
										+ ((Npc) affectedMob).getDef().name
										+ ")"));
							}
							if (affectedMob.getID() == 35) {
								owner.getActionSender()
										.sendMessage(
												"Delrith can not be attacked without the Silverlight sword");
								return;
							}
							owner.resetAll();
							owner.setStatus(Action.RANGING_MOB);
							if (affectedMob instanceof Player) {
								Player affectedPlayer = (Player) affectedMob;
								affectedPlayer.resetTrade();
								if (affectedPlayer.getMenuHandler() != null) {
									affectedPlayer.resetMenuHandler();
								}
								if (affectedPlayer.accessingBank()) {
									affectedPlayer.resetBank();
								}
								if (affectedPlayer.accessingShop()) {
									affectedPlayer.resetShop();
								}
								if (affectedPlayer.getNpc() != null) {
									affectedPlayer.getNpc().unblock();
									affectedPlayer.setNpc(null);
								}
							}
							if (Formulae.getRangeDirection(owner, affectedMob) != -1)
								owner.setSprite(Formulae.getRangeDirection(
										owner, affectedMob));

							owner.setRangeEvent(new RangeEvent(owner,
									affectedMob));
						}
					});

		}
	}
}