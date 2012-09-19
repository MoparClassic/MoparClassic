package org.moparscape.msc.gs.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Bank {
	/**
	 * The maximum size of a bank
	 */
	public static final int MAX_SIZE = 192;
	private ArrayList<InvItem> list = new ArrayList<InvItem>();

	public Bank() {
	}

	public int add(InvItem item) {
		if (item.getAmount() <= 0) {
			return -1;
		}
		for (int index = 0; index < list.size(); index++) {
			if (item.equals(list.get(index))) {
				list.get(index).setAmount(
						list.get(index).getAmount() + item.getAmount());
				return index;
			}
		}
		list.add(item);
		return list.size() - 2;
	}

	public boolean canHold(ArrayList<InvItem> items) {
		return (MAX_SIZE - list.size()) >= getRequiredSlots(items);
	}

	public boolean canHold(InvItem item) {
		return (MAX_SIZE - list.size()) >= getRequiredSlots(item);
	}

	public boolean contains(InvItem i) {
		return list.contains(i);
	}

	public int countId(int id) {
		for (InvItem i : list) {
			if (i.getID() == id) {
				return i.getAmount();
			}
		}
		return 0;
	}

	public boolean full() {
		return list.size() >= MAX_SIZE;
	}

	public InvItem get(int index) {
		if (index < 0 || index >= list.size()) {
			return null;
		}
		return list.get(index);
	}

	public InvItem get(InvItem item) {
		for (InvItem i : list) {
			if (item.equals(i)) {
				return i;
			}
		}
		return null;
	}

	public int getFirstIndexById(int id) {
		for (int index = 0; index < list.size(); index++) {
			if (list.get(index).getID() == id) {
				return index;
			}
		}
		return -1;
	}

	public ArrayList<InvItem> getItems() {
		return list;
	}

	public int getRequiredSlots(InvItem item) {
		return (list.contains(item) ? 0 : 1);
	}

	public int getRequiredSlots(List<InvItem> items) {
		int requiredSlots = 0;
		for (InvItem item : items) {
			if (list.contains(item)) {
				continue;
			}
			requiredSlots++;
		}
		return requiredSlots;
	}

	public boolean hasItemId(int id) {
		for (InvItem i : list) {
			if (i.getID() == id)
				return true;
		}

		return false;
	}

	public ListIterator<InvItem> iterator() {
		return list.listIterator();
	}

	public void remove(int index) {
		InvItem item = get(index);
		if (item == null) {
			return;
		}
		remove(item.getID(), item.getAmount());
	}

	public int remove(int id, int amount) {
		Iterator<InvItem> iterator = list.iterator();
		for (int index = 0; iterator.hasNext(); index++) {
			InvItem i = iterator.next();
			if (id == i.getID()) {
				if (amount < i.getAmount()) {
					i.setAmount(i.getAmount() - amount);
				} else {
					iterator.remove();
				}
				return index;
			}
		}
		return -1;
	}

	public int remove(InvItem item) {
		return remove(item.getID(), item.getAmount());
	}

	public int size() {
		return list.size();
	}

}
