package org.moparscape.msc.gs.quest;

/**
 * Handles the list of possible quest triggers
 */
public enum QuestAction {
	KILLED_NPC("The player killed a NPC", 0), TALKED_NPC(
			"The player talked to a NPC", 1), USED_OBJECT(
			"The player used an object", 2), USED_ITEM(
			"The player used an inventory item", 3), ITEM_PICKED_UP(
			"The player picked up an item", 4), ITEM_USED_ON_OBJECT(
			"The player used an item on an object", 5), ITEM_USED_ON_ITEM(
			"The player used an item on another item", 6), ATTACKED_NPC(
			"The player attacked an NPC", 7);

	private String description = "";
	private int id = -1;

	public String toString() {
		return description + " (id " + id + ")";
	}

	public int getID() {
		return id;
	}

	private QuestAction(String description, int id) {
		this.description = description;
		this.id = id;
	}
}