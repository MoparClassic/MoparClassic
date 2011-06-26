package org.moparscape.msc.gs.plugins.ai;

import java.util.ArrayList;
import java.util.Map;

import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.event.ObjectRemover;
import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.Projectile;
import org.moparscape.msc.gs.model.mini.Damage;
import org.moparscape.msc.gs.plugins.dependencies.NpcAI;
import org.moparscape.msc.gs.plugins.dependencies.NpcScript;

/**
 * KingBlackDragon intelligence class.
 * 
 * @author xEnt
 * 
 */
public class KingBlackDragon extends NpcScript implements NpcAI {

	@Override
	public int getID() {
		return 477;
	}

	@Override
	public void onHealthPercentage(Npc npc, int percent) {
		/*
		 * if(percent > 60 && percent <= 80 && npc.getStage() == 1) {
		 * npc.setStage(2); shootFire(npc); } if(percent > 45 && percent <= 60
		 * && npc.getStage() == 2) { npc.setStage(3); if(npc.getOpponent() !=
		 * null || npc.getOpponent() instanceof Player) { sendNpcChat(npc,
		 * (Player)npc.getOpponent(), "You can't kill me damn humans", true); }
		 * } if(percent > 35 && percent < 50 && npc.getStage() == 3) {
		 * npc.setStage(4); shootFire(npc); } if(percent > 5 && percent < 25 &&
		 * npc.getStage() == 4) { npc.setStage(5); absorbHealth(npc); }
		 * if(percent > 50 && percent < 65 && npc.getStage() == 5) {
		 * npc.setStage(6); shootFire(npc); } if(percent > 42 && percent < 49 &&
		 * npc.getStage() == 5) { npc.setStage(7); for(Player p :
		 * npc.getViewArea().getPlayersInView()) { if(p != null) {
		 * p.getActionSender().sendMessage("The " + npc.getDef().name +
		 * " gets stronger!"); npc.getDef().strength+=npc.getDef().strength *
		 * 0.30; } } } if(percent > 25 && percent < 40 && npc.getStage() == 7) {
		 * npc.setStage(8); shootFire(npc); }
		 */
	}

	public void absorbHealth(Npc npc) {
		final Map<Player, Damage> dmgs = npc.getSyndicate().getDamages();
		for (final Player p : dmgs.keySet()) {
			if (p == null)
				continue;

			int drain = Formulae.Rand((int) (p.getMaxStat(3) * 0.25) / 2,
					(int) (p.getMaxStat(3) * 0.25));
			Projectile projectile = new Projectile(p, npc, 4);
			int newhp = npc.getCurHits() + drain;
			if (newhp > npc.getDef().hits)
				newhp = npc.getDef().hits;
			npc.setHits(newhp);
			ArrayList<Player> playersToInform = new ArrayList<Player>();
			playersToInform.addAll(npc.getViewArea().getPlayersInView());
			playersToInform.addAll(p.getViewArea().getPlayersInView());
			for (Player p1 : playersToInform) {
				p1.informOfModifiedHits(npc);
				p1.informOfProjectile(projectile);
			}

			shootPlayer(npc, p, drain, 1);
			p.getActionSender().sendMessage(
					"The " + npc.getDef().name + " absorbs health from you");
		}
	}

	@Override
	public void onMageAttack(Player attacker, Npc npc) {
		/*
		 * if(npc.getStage() == 0) { npc.setStage(1); sendNpcChat(npc, attacker,
		 * "Feel my Wrath Humans!", true); shootFire(npc); }
		 */
	}

	@Override
	public void onMeleeAttack(Player attacker, final Npc npc) {
		/*
		 * if (attacker.getCurStat(5) > (int) (attacker.getMaxStat(5) * 0.01)) {
		 * attacker.setCurStat(5, (int) (attacker.getMaxStat(5) * 0.01));
		 * attacker.getActionSender().sendStat(5);
		 * attacker.getActionSender().sendMessage
		 * ("The dragon lowers your Prayer"); }
		 */
		if (attacker.getCurStat(5) > 0) {
			// attacker.setCurStat(5, (int)(attacker.getCurStat(5) * 0.10));
			// for (int prayerID = 0; prayerID < 14; prayerID++) {
			// attacker.setPrayer(prayerID, false);
			// }
			// attacker.setDrainRate(0);
			// attacker.getActionSender().sendMessage("You have run out of prayer points. Return to a church to recharge");
			// attacker.getActionSender().sendPrayers();
		}
		/*
		 * if(npc.getStage() == 0) { npc.setStage(1); sendNpcChat(npc, attacker,
		 * "Feel my Wrath Humans!", true); }
		 */
	}

	public void shootFire(Npc npc) {
		try {
			if (npc != null) {
				if (npc.getOpponent() != null
						&& npc.getOpponent() instanceof Player) {
					Instance.getDelayedEventHandler().add(
							new org.moparscape.msc.gs.event.MiniEvent(
									(Player) npc.getOpponent(), 2500,
									new Object[] { npc }) {
								public void action() {
									Npc n = (Npc) super.args[0];
									if (n != null && n.getHits() > 0) {
										NpcScript script = new NpcScript();
										final Map<Player, Damage> dmgs = n
												.getSyndicate().getDamages();
										for (final Player p : dmgs.keySet()) {
											if (p == null)
												continue;
											if (p.equals(owner))
												continue;
											script.shootPlayer(
													n,
													p,
													Formulae.Rand(
															(int) (p.getMaxStat(3) * 0.25) / 2,
															(int) (p.getMaxStat(3) * 0.25)),
													1);
											p.getActionSender()
													.sendMessage(
															"The "
																	+ n.getDef().name
																	+ " spits fire at you");
											GameObject obj = new GameObject(p
													.getLocation(), 1036, 0, 0);
											world.registerGameObject(obj);
											Instance.getDelayedEventHandler()
													.add(new ObjectRemover(obj,
															500));
											Instance.getDelayedEventHandler()
													.add(new org.moparscape.msc.gs.event.MiniEvent(
															(Player) n
																	.getOpponent(),
															2500,
															new Object[] { n }) {
														public void action() {
															shootFire((Npc) super.args[0]);
														}

													});
										}
									}
								}
							});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onNpcAttack(Npc npc, Player player) {
		/*
		 * if (player.getCurStat(5) > (int) (player.getMaxStat(5) * 0.01)) {
		 * player.setCurStat(5, (int) (player.getMaxStat(5) * 0.01));
		 * player.getActionSender().sendStat(5);
		 * player.getActionSender().sendMessage
		 * ("The dragon lowers your Prayer"); }
		 */
		if (player.getCurStat(5) > 0) {
			// player.setCurStat(5, (int)(player.getCurStat(5) * 0.10));
			// for (int prayerID = 0; prayerID < 14; prayerID++) {
			// player.setPrayer(prayerID, false);
			// }
			// player.setDrainRate(0);
			// player.getActionSender().sendMessage("You have run out of prayer points. Return to a church to recharge");
			// player.getActionSender().sendPrayers();
		}
	}

	@Override
	public void onNpcDeath(Npc npc, Player player) {
		/*
		 * if(npc.getOpponent() != null || npc.getOpponent() instanceof Player)
		 * { sendNpcChat(npc, (Player)npc.getOpponent(),
		 * "I'll be back muhahahaa", true); }
		 */
	}

	@Override
	public void onRangedAttack(Player p, Npc npc) {
		/*
		 * if(npc.getStage() == 0) { npc.setStage(1); sendNpcChat(npc, p,
		 * "Feel my Wrath Humans!", true); shootFire(npc); }
		 */
	}

}
