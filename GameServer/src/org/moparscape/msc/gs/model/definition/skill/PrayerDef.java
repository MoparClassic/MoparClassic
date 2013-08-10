package org.moparscape.msc.gs.model.definition.skill;

import org.moparscape.msc.gs.model.definition.EntityDefinition;

/**
 * The definition wrapper for prayers
 */
public class PrayerDef extends EntityDefinition {

	/**
	 * The drain rate of the prayer (perhaps points per min?)
	 */
	public int drainRate;
	/**
	 * The level required to use the prayer
	 */
	public int reqLevel;

	public int getDrainRate() {
		return drainRate;
	}

	public int getReqLevel() {
		return reqLevel;
	}
}
