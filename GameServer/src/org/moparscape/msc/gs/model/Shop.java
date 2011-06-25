package org.moparscape.msc.gs.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.event.DelayedEvent;
import org.moparscape.msc.gs.tools.DataConversions;

public class Shop {
	/**
	 * The maximum size of a shop
	 */
	private static int MAX_SIZE = 40;
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	private int buyModifier;
	private int[] equilibriumAmounts;
	private int[] equilibriumIds;
	private boolean general;
	private String greeting;
	private ArrayList<InvItem> items;
	private int minX, maxX, minY, maxY;
	private String name;
	private String[] options;
	private ArrayList<Player> players;
	private int respawnRate;
	private int sellModifier;

	public int add(InvItem item) {
		if (item.getAmount() <= 0) {
			return -1;
		}
		for (int index = 0; index < items.size(); index++) {
			if (item.equals(items.get(index))) {
				items.get(index).setAmount(
						items.get(index).getAmount() + item.getAmount());
				return index;
			}
		}
		items.add(item);
		return items.size() - 2;
	}

	public void addPlayer(Player player) {
		players.add(player);
	}

	public boolean canHold(ArrayList<InvItem> items) {
		return (MAX_SIZE - items.size()) >= getRequiredSlots(items);
	}

	public boolean canHold(InvItem item) {
		return (MAX_SIZE - items.size()) >= getRequiredSlots(item);
	}

	public boolean contains(InvItem i) {
		return items.contains(i);
	}

	public int countId(int id) {
		for (InvItem i : items) {
			if (i.getID() == id) {
				return i.getAmount();
			}
		}
		return 0;
	}

	public boolean equals(Object o) {
		if (o instanceof Shop) {
			Shop shop = (Shop) o;
			return shop.getName().equals(name);
		}
		return false;
	}

	public boolean full() {
		return items.size() >= MAX_SIZE;
	}

	public int getBuyModifier() {
		return buyModifier;
	}

	public int getEquilibrium(int id) {
		for (int idx = 0; idx < equilibriumIds.length; idx++) {
			if (equilibriumIds[idx] == id) {
				return equilibriumAmounts[idx];
			}
		}
		return 0;
	}

	public InvItem getFirstById(int id) {
		for (int index = 0; index < items.size(); index++) {
			if (items.get(index).getID() == id) {
				return items.get(index);
			}
		}
		return null;
	}

	public String getGreeting() {
		return greeting;
	}

	public ArrayList<InvItem> getItems() {
		return items;
	}

	public String getName() {
		return name;
	}

	public String[] getOptions() {
		return options;
	}

	public int getRequiredSlots(InvItem item) {
		return (items.contains(item) ? 0 : 1);
	}

	public int getRequiredSlots(List<InvItem> items) {
		int requiredSlots = 0;
		for (InvItem item : items) {
			if (items.contains(item)) {
				continue;
			}
			requiredSlots++;
		}
		return requiredSlots;
	}

	public int getSellModifier() {
		return sellModifier;
	}

	public void initRestock() {
		players = new ArrayList<Player>();
		final Shop shop = this;
		Instance.getDelayedEventHandler().add(
				new DelayedEvent(null, respawnRate) {
					private int iterations = 0;

					public void run() {
						boolean changed = false;
						Iterator<InvItem> iterator = items.iterator();
						iterations++;
						while (iterator.hasNext()) {
							InvItem shopItem = iterator.next();
							int eq = shop.getEquilibrium(shopItem.getID());
							if ((iterations % 4 == 0)
									&& shopItem.getAmount() > eq) {
								shopItem.setAmount(shopItem.getAmount() - 1);
								if (shopItem.getAmount() <= 0
										&& !DataConversions.inArray(
												equilibriumIds,
												shopItem.getID())) {
									iterator.remove();
								}
								changed = true;
							} else if (shopItem.getAmount() < eq) {
								shopItem.setAmount(shopItem.getAmount() + 1);
								changed = true;
							}
						}
						if (changed) {
							shop.updatePlayers();
						}
					}
				});
	}

	public boolean isGeneral() {
		return general;
	}

	public ListIterator<InvItem> iterator() {
		return items.listIterator();
	}

	public int remove(InvItem item) {
		Iterator<InvItem> iterator = items.iterator();
		for (int index = 0; iterator.hasNext(); index++) {
			InvItem i = iterator.next();
			if (item.getID() == i.getID()) {
				if (item.getAmount() < i.getAmount()) {
					i.setAmount(i.getAmount() - item.getAmount());
				} else if (DataConversions
						.inArray(equilibriumIds, item.getID())) {
					i.setAmount(0);
				} else {
					iterator.remove();
				}
				return index;
			}
		}
		return -1;
	}

	public void removePlayer(Player player) {
		players.remove(player);
	}

	public void setEquilibrium() {
		equilibriumIds = new int[items.size()];
		equilibriumAmounts = new int[items.size()];
		for (int idx = 0; idx < items.size(); idx++) {
			equilibriumIds[idx] = items.get(idx).getID();
			equilibriumAmounts[idx] = items.get(idx).getAmount();
		}
	}

	public boolean shouldStock(int id) {
		if (general) {
			return true;
		}
		for (int eqID : equilibriumIds) {
			if (eqID == id) {
				return true;
			}
		}
		return false;
	}

	public int size() {
		return items.size();
	}

	public void updatePlayers() {
		Iterator<Player> iterator = players.iterator();
		while (iterator.hasNext()) {
			Player p = iterator.next();
			if (!equals(p.getShop())) {
				iterator.remove();
				continue;
			}
			p.getActionSender().showShop(this);
		}
	}

	public boolean withinShop(Point p) {
		return p.getX() >= minX && p.getX() <= maxX && p.getY() >= minY
				&& p.getY() <= maxY;
	}

}