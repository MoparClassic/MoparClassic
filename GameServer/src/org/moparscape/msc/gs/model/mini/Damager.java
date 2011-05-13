package org.moparscape.msc.gs.model.mini;

import org.moparscape.msc.gs.model.Player;

/**
 * a Damager (Player) that adds to a pool (Syndicate) of an NPC
 * 
 * @author xEnt
 * 
 */
public class Damager {

    /**
     * Damage this player has dealt
     */
    private int damage = 0;
    /**
     * the Player/Damager
     */
    private Player player;
    /**
     * Have they used melee on this NPC?
     */
    private boolean useCombat = false;
    /**
     * Have they used magic on this NPC?
     */
    private boolean useMagic = false;
    /**
     * Have they used ranged on this NPC?
     */
    private boolean useRanged = false;

    public int getDamage() {
	return damage;
    }

    public void setDamage(int damage) {
	this.damage = damage;
    }

    public Player getPlayer() {
	return player;
    }

    public void setPlayer(Player player) {
	this.player = player;
    }

    public boolean isUseCombat() {
	return useCombat;
    }

    public void setUseCombat(boolean useCombat) {
	this.useCombat = useCombat;
    }

    public boolean isUseMagic() {
	return useMagic;
    }

    public void setUseMagic(boolean useMagic) {
	this.useMagic = useMagic;
    }

    public boolean isUseRanged() {
	return useRanged;
    }

    public void setUseRanged(boolean useRanged) {
	this.useRanged = useRanged;
    }

    public Damager(Player p) {
	this.player = p;
    }

}
