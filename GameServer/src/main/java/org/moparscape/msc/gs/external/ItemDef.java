package org.moparscape.msc.gs.external;

/**
 * The definition wrapper for items
 */
public class ItemDef extends EntityDef {
	/**
	 * The base price of the object
	 */
	public int basePrice;
	/**
	 * The command of the object
	 */
	public String command;
	/**
	 * PictureMask
	 */
	public int mask;
	/**
	 * Is this item a member's item?
	 */
	public boolean members;
	/**
	 * The sprite id
	 */
	public int sprite;
	/**
	 * Whether the item is stackable or not
	 */
	public boolean stackable;
	/**
	 * Is this item tradeable?
	 */
	public boolean trade;
	/**
	 * Whether the item is wieldable or not
	 */
	public int wieldable;

	public boolean canTrade() {
		return trade;
	}

	public int getBasePrice() {
		return basePrice;
	}

	public String getCommand() {
		return command;
	}

	public String getCommandType() {
		return wieldable == 16 ? "Wield" : "Wear";
	}

	public int getPictureMask() {
		return mask;
	}

	public int getSprite() {
		return sprite;
	}

	public boolean isMembers() {
		return members;
	}

	public boolean isStackable() {
		return stackable;
	}

	public boolean isWieldable() {
		return wieldable > 0;
	}
}