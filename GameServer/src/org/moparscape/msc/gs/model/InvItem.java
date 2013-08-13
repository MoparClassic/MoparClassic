package org.moparscape.msc.gs.model;

import net.jcip.annotations.ThreadSafe;

import org.moparscape.msc.gs.model.definition.EntityHandler;
import org.moparscape.msc.gs.model.definition.entity.ItemDef;
import org.moparscape.msc.gs.model.definition.skill.ItemWieldableDef;

@ThreadSafe
public class InvItem {

	public final int amount;
	public final boolean wielded;
	public final int id;

	public InvItem(int id) {
		this(id, 1);
	}

	public InvItem(int id, int amount) {
		this(id, amount, false);
	}

	public InvItem(int id, int amount, boolean wielded) {
		this.id = id;
		this.amount = amount;
		this.wielded = wielded;
	}

	public boolean equals(Object o) {
		if (o instanceof InvItem) {
			InvItem item = (InvItem) o;
			return item.id == id;
		}
		return false;
	}

	public ItemDef getDef() {
		return EntityHandler.getItemDef(id);
	}

	private boolean isWieldable() {
		return EntityHandler.getItemWieldableDef(id) != null;
	}

	public boolean wieldingAffectsItem() {
		if (!isWieldable()) {
			return false;
		}
		ItemWieldableDef def = EntityHandler.getItemWieldableDef(id);
		for (int affected : def.getAffectedTypes()) {
			if (def.getType() == affected) {
				return true;
			}
		}
		return false;
	}

}
