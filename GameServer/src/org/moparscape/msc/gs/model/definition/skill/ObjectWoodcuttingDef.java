package org.moparscape.msc.gs.model.definition.skill;

/**
 * The definition wrapper for trees
 */
public class ObjectWoodcuttingDef {

	/**
	 * How much experience identifying gives
	 */
	public int exp;
	/**
	 * Percent chance the tree will be felled
	 */
	public int fell;
	/**
	 * The id of the ore this turns into
	 */
	private int logId;
	/**
	 * Herblaw level required to identify
	 */
	public int requiredLvl;
	/**
	 * How long the tree takes to respawn afterwards
	 */
	public int respawnTime;

	public int getExp() {
		return exp;
	}

	public int getFell() {
		return fell;
	}

	public int getLogId() {
		return logId;
	}

	public int getReqLevel() {
		return requiredLvl;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

}
