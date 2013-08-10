package org.moparscape.msc.gs.model.definition.entity;

public class ItemDropDef {
	public int amount;
	public int id;
	public int weight;

	public ItemDropDef(int id, int amount, int weight) {
		this.id = id;
		this.amount = amount;
		this.weight = weight;
	}

	public int getAmount() {
		return amount;
	}

	public int getID() {
		return id;
	}

	public int getWeight() {
		return weight;
	}
}