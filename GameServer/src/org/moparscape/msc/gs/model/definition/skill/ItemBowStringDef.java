package org.moparscape.msc.gs.model.definition.skill;

/**
 * The definition wrapper for items
 */
public class ItemBowStringDef {

	/**
	 * The ID of the bow created
	 */
	public int bowID;
	/**
	 * The exp given by attaching this bow string
	 */
	public int exp;
	/**
	 * The level required to attach this bow string
	 */
	public int requiredLvl;

	public int getBowID() {
		return bowID;
	}

	public int getExp() {
		return exp;
	}

	public int getReqLevel() {
		return requiredLvl;
	}
}
