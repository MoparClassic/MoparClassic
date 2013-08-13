package org.moparscape.msc.ls.model;

import java.io.Serializable;

public class InvItem extends Item implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean wielded;

	public InvItem(int id, int amount, boolean wielded) {
		super(id, amount);
		this.wielded = wielded;
	}

	public boolean isWielded() {
		return wielded;
	}
}