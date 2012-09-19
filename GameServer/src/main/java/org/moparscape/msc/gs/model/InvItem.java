package org.moparscape.msc.gs.model;

import org.moparscape.msc.gs.external.EntityHandler;
import org.moparscape.msc.gs.external.ItemCookingDef;
import org.moparscape.msc.gs.external.ItemDef;
import org.moparscape.msc.gs.external.ItemSmeltingDef;
import org.moparscape.msc.gs.external.ItemUnIdentHerbDef;
import org.moparscape.msc.gs.external.ItemWieldableDef;

public class InvItem extends Entity implements Comparable<InvItem> {

	private int amount;
	private boolean wielded = false;

	public InvItem(int id) {
		setID(id);
		setAmount(1);
	}

	public InvItem(int id, int amount) {
		setID(id);
		setAmount(amount);
	}

	public int compareTo(InvItem item) {
		if (item.getDef().isStackable()) {
			return -1;
		}
		if (getDef().isStackable()) {
			return 1;
		}
		return item.getDef().getBasePrice() - getDef().getBasePrice();
	}

	public int eatingHeals() {
		if (!isEdible()) {
			return 0;
		}
		return EntityHandler.getItemEdibleHeals(id);
	}

	public boolean equals(Object o) {
		if (o instanceof InvItem) {
			InvItem item = (InvItem) o;
			return item.getID() == getID();
		}
		return false;
	}

	public int getAmount() {
		return amount;
	}

	public ItemCookingDef getCookingDef() {
		return EntityHandler.getItemCookingDef(id);
	}

	public ItemDef getDef() {
		return EntityHandler.getItemDef(id);
	}

	public ItemSmeltingDef getSmeltingDef() {
		return EntityHandler.getItemSmeltingDef(id);
	}

	public ItemUnIdentHerbDef getUnIdentHerbDef() {
		return EntityHandler.getItemUnIdentHerbDef(id);
	}

	public ItemWieldableDef getWieldableDef() {
		return EntityHandler.getItemWieldableDef(id);
	}

	public boolean isEdible() {
		return EntityHandler.getItemEdibleHeals(id) > 0;
	}

	public boolean isWieldable() {
		return EntityHandler.getItemWieldableDef(id) != null;
	}

	public boolean isWielded() {
		return wielded;
	}

	public void setAmount(int amount) {
		if (amount < 0) {
			amount = 0;
		}
		this.amount = amount;
	}

	public void setWield(boolean wielded) {
		this.wielded = wielded;
	}

	public boolean wieldingAffectsItem(InvItem i) {
		if (!i.isWieldable() || !isWieldable()) {
			return false;
		}
		for (int affected : getWieldableDef().getAffectedTypes()) {
			if (i.getWieldableDef().getType() == affected) {
				return true;
			}
		}
		return false;
	}

}
