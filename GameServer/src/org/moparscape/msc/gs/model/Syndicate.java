package org.moparscape.msc.gs.model;

import java.util.Map;
import java.util.WeakHashMap;

import org.moparscape.msc.gs.config.Formulae;
import org.moparscape.msc.gs.model.mini.Damage;
import org.moparscape.msc.gs.tools.DataConversions;

/**
 * Each NPC has a Damage Syndicate (pool of damage) binded to them.
 * 
 * @author xEnt
 * 
 */
public class Syndicate {

	private Map<Player, Damage> damage = new WeakHashMap<Player, Damage>();

	public void addDamage(final Player player, final int damage,
			final int damageType) {
		final Damage prev = this.damage.get(player);
		if (prev != null) {
			prev.addDamage(damage, damageType);
		} else {
			this.damage.put(player, new Damage(damage, damageType));
		}
	}

	public Map<Player, Damage> getDamages() {
		return damage;
	}

	public void distributeExp(final Npc npc) {
		final Map<Player, Damage> dmgs = getDamages();
		final int exp = DataConversions
				.roundUp(Formulae.combatExperience(npc) / 4D);
		int newXP = 0;
		for (final Player p : dmgs.keySet()) {
			Damage dmg = dmgs.get(p);
			int total = dmg.getTotalDamage();
			if (total > npc.getDef().hits) {
				total = npc.getDef().hits;
			}
			if (p != null) {
				newXP = (exp * total) / npc.getDef().hits;

				p.incExp(4, roundAndCast(newXP * 4 * dmg.getRangePortion()),
						true);
				p.getActionSender().sendStat(4);
				switch (p.getCombatStyle()) {
				case 0:
					for (int x = 0; x < 3; x++) {
						p.incExp(x,
								roundAndCast(newXP * dmg.getCombatPortion()),
								true);
						p.getActionSender().sendStat(x);
					}
					break;
				case 1:
					p.incExp(2,
							roundAndCast(newXP * 3 * dmg.getCombatPortion()),
							true);
					p.getActionSender().sendStat(2);
					break;
				case 2:
					p.incExp(0,
							roundAndCast(newXP * 3 * dmg.getCombatPortion()),
							true);
					p.getActionSender().sendStat(0);
					break;
				case 3:
					p.incExp(1,
							roundAndCast(newXP * 3 * dmg.getCombatPortion()),
							true);
					p.getActionSender().sendStat(1);
					break;
				}
				p.incExp(3, roundAndCast(newXP * dmg.getCombatPortion()), true);
				p.getActionSender().sendStat(3);
			}
		}
	}

	public int roundAndCast(double d) {
		return (int) Math.round(d);
	}

}
