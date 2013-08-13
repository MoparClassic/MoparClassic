package org.moparscape.msc.gs.event;

import java.util.ArrayList;

import org.moparscape.msc.gs.config.CombatFormulae;
import org.moparscape.msc.gs.config.Formulae;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.states.CombatState;
import org.moparscape.msc.gs.tools.DataConversions;

public class DuelEvent extends DelayedEvent {
	private Player affectedPlayer;
	private int hits;

	public DuelEvent(Player owner, Player affectedPlayer) {
		super(owner, 1300);
		this.affectedPlayer = affectedPlayer;
		hits = 0;
	}

	public boolean equals(Object o) {
		if (o instanceof DuelEvent) {
			DuelEvent e = (DuelEvent) o;
			return e.belongsTo(owner)
					&& e.getAffectedPlayer().equals(affectedPlayer);
		}
		return false;
	}

	public Player getAffectedPlayer() {
		return affectedPlayer;
	}

	public void run() {
		if (!owner.loggedIn() || !affectedPlayer.loggedIn()) {
			owner.resetCombat(CombatState.ERROR);
			affectedPlayer.resetCombat(CombatState.ERROR);
			return;
		}
		Player attacker, opponent;
		if (hits++ % 2 == 0) {
			attacker = owner;
			opponent = affectedPlayer;
		} else {
			attacker = affectedPlayer;
			opponent = owner;
		}
		if (opponent.getHits() <= 0) {
			attacker.resetCombat(CombatState.WON);
			opponent.resetCombat(CombatState.LOST);
			this.stop();
			return;
		}
		attacker.incHitsMade();
		attacker.setLastMoved();

		// int damage = (attacker instanceof Player && opponent instanceof
		// Player ? Formulae
		// .calcFightHit(attacker, opponent) : Formulae
		// .calcFightHitWithNPC(attacker, opponent));
		int damage = CombatFormulae.getNextHit(attacker, opponent);
		opponent.setLastDamage(damage);
		int newHp = opponent.getHits() - damage;
		opponent.setHits(newHp);
		ArrayList<Player> playersToInform = new ArrayList<Player>();
		playersToInform.addAll(opponent.getViewArea().getPlayersInView());
		playersToInform.addAll(attacker.getViewArea().getPlayersInView());
		for (Player p : playersToInform) {
			p.informOfModifiedHits(opponent);
		}
		String combatSound = damage > 0 ? "combat1b" : "combat1a";

		opponent.getActionSender().sendStat(3);
		opponent.getActionSender().sendSound(combatSound);
		attacker.getActionSender().sendSound(combatSound);

		if (newHp <= 0) {
			opponent.killedBy(attacker, true);
			int exp = DataConversions.roundUp(Formulae
					.combatExperience(opponent) / 4D);
			switch (attacker.getCombatStyle()) {
			case 0:
				for (int x = 0; x < 3; x++) {
					attacker.incExp(x, exp, true);
					attacker.getActionSender().sendStat(x);
				}
				break;
			case 1:
				attacker.incExp(2, exp * 3, true);
				attacker.getActionSender().sendStat(2);
				break;
			case 2:
				attacker.incExp(0, exp * 3, true);
				attacker.getActionSender().sendStat(0);
				break;
			case 3:
				attacker.incExp(1, exp * 3, true);
				attacker.getActionSender().sendStat(1);
				break;
			}
			attacker.incExp(3, exp, true);
			attacker.getActionSender().sendStat(3);

			attacker.resetCombat(CombatState.WON);
			opponent.resetCombat(CombatState.LOST);

			attacker.resetDueling();
			opponent.resetDueling();
			this.stop();
		}
	}
}
