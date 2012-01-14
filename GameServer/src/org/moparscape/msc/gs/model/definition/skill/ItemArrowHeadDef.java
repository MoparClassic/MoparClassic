package org.moparscape.msc.gs.model.definition.skill;

/**
 * The definition wrapper for items
 */
public class ItemArrowHeadDef {

	/**
	 * The ID of the arrow created
	 */
	public int arrowID;
	/**
	 * The exp given by attaching this arrow head
	 */
	public double exp;
	/**
	 * The level required to attach this head to an arrow
	 */
	public int requiredLvl;

	public int getArrowID() {
		return arrowID;
	}

	public double getExp() {
		return exp;
	}

	public int getReqLevel() {
		return requiredLvl;
	}
}
