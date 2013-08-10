package org.moparscape.msc.gs.model.definition.skill;

public class FiremakingDef {
	/**
	 * The exp given by these logs
	 */
	public int exp;
	/**
	 * How many ms the fire should last for
	 */
	public int length;
	/**
	 * The firemaking level required to light these logs
	 */
	public int level;

	public int getExp() {
		return exp;
	}

	public int getLength() {
		return length * 1000;
	}

	public int getRequiredLevel() {
		return level;
	}
}