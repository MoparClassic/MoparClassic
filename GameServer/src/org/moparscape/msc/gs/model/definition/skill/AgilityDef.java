package org.moparscape.msc.gs.model.definition.skill;

/**
 * Defines an agility course object
 */
public class AgilityDef {
	/**
	 * Can you fail this obstacle?
	 */
	public boolean canFail;
	/**
	 * The xp you get for doing this obstacle safely
	 */
	public int exp;
	/**
	 * The fail x coord
	 */
	public int failX;
	/**
	 * The fail y coord
	 */
	public int failY;
	/**
	 * What message does this object spew to the client?
	 */
	public String message;
	/**
	 * What order this object is in the obstacle course
	 */
	public int order;
	/**
	 * Required level
	 */
	public int reqLevel;
	/**
	 * Tele x coord
	 */
	public int toX;
	/**
	 * Tele y coord
	 */
	public int toY;
	/**
	 * The x coord
	 */
	public int x;
	/**
	 * The y coord
	 */
	public int y;

	/**
	 * @return if you can fail this def
	 */
	public boolean canFail() {
		return canFail;
	}

	/**
	 * @return the xp you get for doing this agility object
	 */
	public int getExp() {
		return exp;
	}

	/**
	 * @return the fail x coord we go to if we fail
	 */
	public int getFailX() {
		return failX;
	}

	/**
	 * @return the fail y coord we go to if we fail
	 */
	public int getFailY() {
		return failY;
	}

	/**
	 * @return the object's message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the order of this agility object
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @return this def's required level
	 */
	public int getReqLevel() {
		return reqLevel;
	}

	/**
	 * @return this def's tele x
	 */
	public int getToX() {
		return toX;
	}

	/**
	 * @return this def's tele y
	 */
	public int getToY() {
		return toY;
	}

	/**
	 * @return this def's x coord
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return this def's y coord
	 */
	public int getY() {
		return y;
	}
}