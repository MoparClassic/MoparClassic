package org.moparscape.msc.gs.external;

/**
 * The definition wrapper for npcs
 */
public class NPCDef extends EntityDef {
	/**
	 * Whether the npc is aggressive
	 */
	public boolean aggressive;
	/**
	 * The attack lvl
	 */
	public int attack;
	/**
	 * Whether the npc is attackable
	 */
	public boolean attackable;
	/**
	 * Colour of our legs
	 */
	public int bottomColour;
	/**
	 * Something to do with the camera
	 */
	public int camera1, camera2;
	/**
	 * The primary command
	 */
	public String command;
	/**
	 * The def lvl
	 */
	public int defense;
	/**
	 * Possible drops
	 */
	public ItemDropDef[] drops;
	/**
	 * Colour of our hair
	 */
	public int hairColour;
	/**
	 * The hit points
	 */
	public int hits;
	/**
	 * How long the npc takes to respawn
	 */
	public int respawnTime;
	/**
	 * Skin colour
	 */
	public int skinColour;
	/**
	 * Sprites used to make up this npc
	 */
	public int[] sprites;
	/**
	 * The strength lvl
	 */
	public int strength;
	/**
	 * Colour of our top
	 */
	public int topColour;
	/**
	 * Something to do with models
	 */
	public int walkModel, combatModel, combatSprite;

	public int getAtt() {
		return attack;
	}

	public int getBottomColour() {
		return bottomColour;
	}

	public int getCamera1() {
		return camera1;
	}

	public int getCamera2() {
		return camera2;
	}

	public int getCombatModel() {
		return combatModel;
	}

	public int getCombatSprite() {
		return combatSprite;
	}

	public String getCommand() {
		return command;
	}

	public int getDef() {
		return defense;
	}

	public ItemDropDef[] getDrops() {
		return drops;
	}

	public int getHairColour() {
		return hairColour;
	}

	public int getHits() {
		return hits;
	}

	public int getSkinColour() {
		return skinColour;
	}

	public int getSprite(int index) {
		return sprites[index];
	}

	public int[] getStats() {
		return new int[] { attack, defense, strength };
	}

	public int getStr() {
		return strength;
	}

	public int getTopColour() {
		return topColour;
	}

	public int getWalkModel() {
		return walkModel;
	}

	public boolean isAggressive() {
		return attackable && aggressive;
	}

	public boolean isAttackable() {
		return attackable;
	}

	public int respawnTime() {
		return respawnTime;
	}
}
