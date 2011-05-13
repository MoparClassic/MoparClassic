package org.moparscape.msc.ls.model;

public class InvItem extends Item {
    private boolean wielded;

    public InvItem(int id, int amount, boolean wielded) {
	super(id, amount);
	this.wielded = wielded;
    }

    public boolean isWielded() {
	return wielded;
    }
}