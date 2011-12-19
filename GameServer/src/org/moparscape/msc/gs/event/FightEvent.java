package org.moparscape.msc.gs.event;

import java.util.ArrayList;

import org.moparscape.msc.config.Constants;
import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.model.Mob;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Path;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.mini.Damage;
import org.moparscape.msc.gs.states.CombatState;
import org.moparscape.msc.gs.tools.DataConversions;

public class FightEvent extends DelayedEvent {

	private Mob affectedMob;
	private int firstHit;
	private int hits;
	private boolean attacked = false;
	boolean delay = false;
	int dela = 0;

	public FightEvent(Player owner, Mob affectedMob) {
		this(owner, affectedMob, false);
	}

	/**
	 * Slowed down a fight if PvP in wildy. - not in use atm
	 */
	public static int getDelay(Mob owner, Mob affectedMob) {
		if (owner instanceof Player && affectedMob instanceof Player) {
			if (((Player) owner).getLocation().inWilderness()) {
				return 1400;
			}
		}
		return 1100;
	}

	public FightEvent(Player owner, Mob affectedMob, boolean attacked) {
		super(owner, 1400);
		this.affectedMob = affectedMob;
		firstHit = attacked ? 1 : 0;
		this.attacked = attacked;
		hits = 0;
	}

	public boolean equals(Object o) {
		if (o instanceof FightEvent) {
			FightEvent e = (FightEvent) o;
			return e.belongsTo(owner) && e.getAffectedMob().equals(affectedMob);
		}
		return false;
	}

	public Mob getAffectedMob() {
		return affectedMob;
	}

	public void run() {
		if (!owner.loggedIn()
				|| (affectedMob instanceof Player && !((Player) affectedMob)
						.loggedIn())) {
			owner.resetCombat(CombatState.ERROR);
			affectedMob.resetCombat(CombatState.ERROR);
			return;
		}

		Mob attacker, opponent;

		if (hits++ % 2 == firstHit) {
			attacker = owner;
			opponent = affectedMob;
		} else {
			attacker = affectedMob;
			opponent = owner;
		}

		if (attacker instanceof Npc) {
			Npc n = (Npc) attacker;
			if (attacker.getHits() <= 0) {
				n.resetCombat(CombatState.ERROR);
				owner.resetCombat(CombatState.ERROR);
				this.stop();
			}
		}
		if (opponent instanceof Npc) {
			Npc n = (Npc) opponent;
			if (opponent.getHits() <= 0) {
				n.resetCombat(CombatState.ERROR);
				owner.resetCombat(CombatState.ERROR);
				this.stop();
			}
		}
		if (opponent instanceof Player && attacker instanceof Player) {
			if (((Player) opponent).isSleeping()) {
				((Player) opponent).getActionSender().sendWakeUp(false);
			}
		}

		/*
		 * if(opponent.getHits() <= 0) { attacker.resetCombat(CombatState.WON);
		 * opponent.resetCombat(CombatState.LOST); return; }
		 */
		attacker.incHitsMade();
		if (attacker instanceof Npc && opponent.isPrayerActivated(12)) {
			return;
		}
		int damage = (attacker instanceof Player && opponent instanceof Player ? Formulae
				.calcFightHit(attacker, opponent) : Formulae
				.calcFightHitWithNPC(attacker, opponent));

		if (attacker instanceof Player && opponent instanceof Npc) {
			Npc npc = (Npc) opponent;

			int newDmg = damage;
			if (npc.getCurHits() - damage <= 0 && npc.getCurHits() > 0) {
				newDmg = npc.getCurHits();
			}
			npc.getSyndicate().addDamage(owner, newDmg, Damage.COMBAT_DAMAGE);
		}

		if (attacker instanceof Npc && opponent instanceof Player
				&& attacker.getHitsMade() >= (attacked ? 4 : 3)) {
			Npc npc = (Npc) attacker;
			Player player = (Player) opponent;
			if (npc.getCurHits() <= npc.getDef().hits * 0.10
					&& npc.getCurHits() > 0) {
				if (!npc.getLocation().inWilderness()
						&& npc.getDef().attackable && !npc.getDef().aggressive) {
					boolean go = true;
					for (int i : Constants.GameServer.NPCS_THAT_DONT_RETREAT) {
						if (i == npc.getID()) {
							go = false;
							break;
						}
					}

					if (go) {
						player.getActionSender().sendSound("retreat");
						npc.unblock();
						npc.resetCombat(CombatState.RUNNING);
						player.resetCombat(CombatState.WAITING);
						npc.setRan(true);
						npc.setPath(new Path(attacker.getX(), attacker.getY(),
								DataConversions.random(npc.getLoc().minX(), npc
										.getLoc().maxX()), DataConversions
										.random(npc.getLoc().minY(), npc
												.getLoc().maxY())));
						player.resetAll();
						player.getActionSender().sendMessage(
								"Your opponent is retreating");
						this.stop();
						return;
					}
				}
			}
		}

		opponent.setLastDamage(damage);
		int newHp = opponent.getHits() - damage;
		opponent.setHits(newHp);
		if (opponent instanceof Npc && newHp > 0) {
			Npc n = (Npc) opponent;

			double max = n.getDef().hits;
			double cur = n.getHits();
			int percent = (int) ((cur / max) * 100);
			if (n.isScripted()) {
				Instance.getPluginHandler().getNpcAIHandler(opponent.getID())
						.onHealthPercentage(n, percent);
			}
		}

		String combatSound = null;
		combatSound = damage > 0 ? "combat1b" : "combat1a";
		if (opponent instanceof Player) {
			if (attacker instanceof Npc) {
				Npc n = (Npc) attacker;
				if (n.hasArmor) {
					combatSound = damage > 0 ? "combat2b" : "combat2a";
				} else if (n.undead) {
					combatSound = damage > 0 ? "combat3b" : "combat3a";
				} else {
					combatSound = damage > 0 ? "combat1b" : "combat1a";
				}
			}
			Player opponentPlayer = ((Player) opponent);
			opponentPlayer.getActionSender().sendStat(3);
			opponentPlayer.getActionSender().sendSound(combatSound);
		}
		if (attacker instanceof Player) {
			if (opponent instanceof Npc) {
				Npc n = (Npc) opponent;
				if (n.hasArmor) {
					combatSound = damage > 0 ? "combat2b" : "combat2a";
				} else if (n.undead) {
					combatSound = damage > 0 ? "combat3b" : "combat3a";
				} else {
					combatSound = damage > 0 ? "combat1b" : "combat1a";
				}
			}
			Player attackerPlayer = (Player) attacker;
			attackerPlayer.getActionSender().sendSound(combatSound);
		}

		if (newHp <= 0) {

			Player toLoot = null;

			// Logging.debug(opponent+" killed by "+attacker);

			if (attacker instanceof Player) {
				Player attackerPlayer = (Player) attacker;
				toLoot = attackerPlayer;
				if (opponent instanceof Player) {
					// Logging.debug(opponent+" killed by "+attacker);
					opponent.killedBy(attackerPlayer, false);
				}

				if (attacker instanceof Player && opponent instanceof Npc) {
					Npc npc = (Npc) opponent;
					npc.getSyndicate().distributeExp(npc);
				} else {
					int exp = DataConversions.roundUp(Formulae
							.combatExperience(opponent) / 4D);
					switch (attackerPlayer.getCombatStyle()) {
					case 0:

						for (int x = 0; x < 2; x++) {
							attackerPlayer.incExp(x, exp, true, true);
							attackerPlayer.getActionSender().sendStat(x);
						}
						attackerPlayer.incExp(2, exp, true, true);
						attackerPlayer.getActionSender().sendStat(2);
						break;
					case 1:
						attackerPlayer.incExp(2, exp * 3, true, true);
						attackerPlayer.getActionSender().sendStat(2);
						break;
					case 2:
						attackerPlayer.incExp(0, exp * 3, true, true);
						attackerPlayer.getActionSender().sendStat(0);
						break;
					case 3:
						attackerPlayer.incExp(1, exp * 3, true, true);
						attackerPlayer.getActionSender().sendStat(1);
						break;
					}
					attackerPlayer.incExp(3, exp, true, true);
					attackerPlayer.getActionSender().sendStat(3);
				}
			}
			// if the dead mob isn't a player...
			if (!(affectedMob instanceof Player)) {
				opponent.killedBy(toLoot, false);
			}
			attacker.resetCombat(CombatState.WON);
			opponent.resetCombat(CombatState.LOST);
			this.stop();
		} else {
			ArrayList<Player> playersToInform = new ArrayList<Player>();
			playersToInform.addAll(opponent.getViewArea().getPlayersInView());
			playersToInform.addAll(attacker.getViewArea().getPlayersInView());
			for (Player p : playersToInform) {
				p.informOfModifiedHits(opponent);
			}
		}
	}
}
