package msc.gs.model;

import java.util.ArrayList;

import msc.gs.model.mini.Damager;

/**
 * Each NPC has a Damage Syndicate (pool of damage) binded to them.
 * 
 * @author xEnt
 * 
 */
public class Syndicate {
    /**
     * All the damagers of this NPC Syndicate
     */
    private ArrayList<Damager> damagers = new ArrayList<Damager>();

    public void addDamage(Player p, int damage, boolean magic, boolean combat, boolean ranged) {
	boolean exist = false;
	Damager other = null;
	for (Damager damager : getDamagers()) {
	    if (damager.getPlayer().getUsername().equals(p.getUsername())) {
		exist = true;
		other = damager;
		break;
	    }
	}
	if (!exist) {
	    other = new Damager(p);
	    getDamagers().add(other);
	}
	other.setDamage(other.getDamage() + damage);
	if (magic)
	    other.setUseMagic(true);
	if (combat)
	    other.setUseCombat(true);
	if (ranged)
	    other.setUseRanged(true);
    }

    public ArrayList<Damager> getDamagers() {
	return damagers;
    }

}
