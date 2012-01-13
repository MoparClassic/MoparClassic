package org.moparscape.msc.gs.model.definition.skill;

/**
 * The definition wrapper for fishing spots
 */
public class ObjectFishingDefinition {

	/**
	 * The If of any bait required to go with the net
	 */
	public int baitId;
	/**
	 * The fish that can be caught here
	 */
	public ObjectFishDefinition[] defs;
	/**
	 * The Id of the net required to fish with
	 */
	public int netId;

	public int getBaitId() {
		return baitId;
	}

	public ObjectFishDefinition[] getFishDefs() {
		return defs;
	}

	public int getNetId() {
		return netId;
	}

	public int getReqLevel() {
		int requiredLevel = 99;
		for (ObjectFishDefinition def : defs) {
			if (def.getReqLevel() < requiredLevel) {
				requiredLevel = def.getReqLevel();
			}
		}
		return requiredLevel;
	}

}
