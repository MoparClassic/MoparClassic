package org.moparscape.msc.gs.external;

/**
 * The definition wrapper for items
 */
public class ItemDartTipDef {

    /**
     * The ID of the arrow created
     */
    public int dartID;
    /**
     * The exp given by attaching this arrow head
     */
    public double exp;
    /**
     * The level required to attach this head to an arrow
     */
    public int requiredLvl;

    public int getDartID() {
	return dartID;
    }

    public double getExp() {
	return exp;
    }

    public int getReqLevel() {
	return requiredLvl;
    }
}
