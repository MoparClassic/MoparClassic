package org.moparscape.msc.ls.model;

public class Item {
    private int amount;
    private int id;

    public Item(int id, int amount) {
	this.id = id;
	this.amount = amount;
    }

    public int getAmount() {
	return amount;
    }

    public int getID() {
	return id;
    }
}