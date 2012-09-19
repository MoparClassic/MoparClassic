package org.moparscape.msc.gs.model.mini;

/**
 * Damage values
 * 
 * @author CodeForFame
 * 
 */
public class Damage {

	public static final int COMBAT_DAMAGE = 0;
	public static final int MAGIC_DAMAGE = 1;
	public static final int RANGE_DAMAGE = 2;

	private final int[] damage = new int[3];

	public Damage(final int damage, final int type) {
		this.addDamage(damage, type);
	}

	public int getMagicDamage() {
		return damage[MAGIC_DAMAGE];
	}

	public void setMagicDamage(final int damage) {
		this.damage[MAGIC_DAMAGE] = damage;
	}

	public void addMagicDamage(final int damage) {
		this.damage[MAGIC_DAMAGE] += damage;
	}

	public int getRangeDamage() {
		return this.damage[RANGE_DAMAGE];
	}

	public void setRangeDamage(final int damage) {
		this.damage[RANGE_DAMAGE] = damage;
	}

	public void addRangeDamage(final int damage) {
		this.damage[RANGE_DAMAGE] += damage;
	}

	public int getCombatDamage() {
		return damage[COMBAT_DAMAGE];
	}

	public void setCombatDamage(final int damage) {
		this.damage[COMBAT_DAMAGE] = damage;
	}

	public void addCombatDamage(final int damage) {
		this.damage[COMBAT_DAMAGE] += damage;
	}

	public void addDamage(final int damage, final int damageType) {
		// CBF typing out legit bounds checking...
		this.damage[damageType % 3] += damage;
	}

	public int getTotalDamage() {
		return getCombatDamage() + getRangeDamage() + getMagicDamage();
	}

	public double getRangePortion() {
		return getRangeDamage() / getTotalDamage();
	}

	public double getMagicPortion() {
		return getMagicDamage() / getTotalDamage();
	}

	public double getCombatPortion() {
		return getCombatDamage() / getTotalDamage();
	}
}