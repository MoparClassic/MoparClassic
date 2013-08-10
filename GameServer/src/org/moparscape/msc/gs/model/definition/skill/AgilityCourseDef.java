package org.moparscape.msc.gs.model.definition.skill;

/**
 * Defines an agility course
 */
public class AgilityCourseDef {
	/**
	 * Ending object ID
	 */
	public int endID;
	/**
	 * Ending object x
	 */
	public int endX;
	/**
	 * Ending object y
	 */
	public int endY;
	/**
	 * The exp reward for completing this course
	 */
	public int exp;
	/**
	 * This course's name
	 */
	public String name;
	/**
	 * Starting object x
	 */
	public int startX;
	/**
	 * Starting object y
	 */
	public int startY;

	/**
	 * @return the end ID
	 */
	public int getEndID() {
		return endID;
	}

	/**
	 * @return the end x
	 */
	public int getEndX() {
		return endX;
	}

	/**
	 * @return the end y
	 */
	public int getEndY() {
		return endY;
	}

	/**
	 * @return the exp reward
	 */
	public int getExp() {
		return exp;
	}

	/**
	 * @return this course's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the starting object x
	 */
	public int getStartX() {
		return startX;
	}

	/**
	 * @return the starting object y
	 */
	public int getStartY() {
		return startY;
	}
}