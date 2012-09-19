package org.moparscape.msc.gs.quest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.event.SingleEvent;
import org.moparscape.msc.gs.model.ActiveTile;
import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Item;
import org.moparscape.msc.gs.model.MenuHandler;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.util.Logger;

/**
 * Defines a quest script
 * 
 * @author punKrockeR
 */
public abstract class Quest {
	/**
	 * Empty constructor
	 */
	public Quest() {
	}

	/**
	 * The world instance
	 */
	protected static final World world = World.getWorld();
	/**
	 * The completed flag
	 */
	public static final int COMPLETE = 0;
	/**
	 * This quest's current thread
	 */
	private Thread thread = null;
	/**
	 * The list of npcs associated with this quest
	 */
	private Set<Integer> associatedNpcs = new HashSet<Integer>();
	/**
	 * The list of objects associated with this quest
	 */
	private HashMap<java.awt.Point, Point> associatedObjects = new HashMap<java.awt.Point, Point>();
	/**
	 * The list of items associated with this quest
	 */
	private Set<Integer> associatedItems = new HashSet<Integer>();

	/**
	 * Quest initialization
	 */
	public void init() {
	}

	/**
	 * Used for triggers
	 */
	public abstract void handleAction(QuestAction action, Object[] args,
			Player player);

	/**
	 * Sets this thread
	 */
	public void setThread(Thread thread) {
		this.thread = thread;
	}

	/**
	 * @return this quest's UNIQUE ID - >>MUST<< be unique.
	 */
	public abstract int getUniqueID();

	/**
	 * @return this quest's name
	 */
	public abstract String getName();

	/**
	 * The default sleep time
	 */
	private static long DEFAULT_DELAY = 3200;

	/**
	 * Adds the given npc id to the list of npc ids associated with this quest
	 */
	public void associateNpc(int id) {
		associatedNpcs.add(id);
	}

	/**
	 * Adds the given item id to the list of item ids associated with this quest
	 */
	public void associateItem(int id) {
		associatedItems.add(id);
	}

	/**
	 * Adds the given object id to the list of object ids associated with this
	 * quest
	 */
	public void associateObject(int id, int x, int y) {
		associatedObjects.put(new java.awt.Point(id, associatedObjects.size()),
				Point.location(x, y));
	}

	/**
	 * Adds the given object id to the list of object ids associated with this
	 * quest
	 */
	public void associateObject(int id) {
		associatedObjects.put(new java.awt.Point(id, associatedObjects.size()),
				Point.location(0, 0));
	}

	/**
	 * @return if the given npc id is associated with this quest
	 */
	public boolean isNpcAssociated(int id, Player player) {
		return associatedNpcs.contains(id);
	}

	/**
	 * @return if the given item id is associated with this quest
	 */
	public boolean isItemAssociated(int id, Player player) {
		return associatedItems.contains(id);
	}

	/**
	 * @return if the given object id is associated with this quest
	 */
	public boolean isObjectAssociated(GameObject obj, Player player) {
		java.util.Set<java.awt.Point> set = associatedObjects.keySet();
		for (java.awt.Point p : set) {
			int i = (int) p.getX();
			if (i == obj.getID())
				if (Point.location(obj.getX(), obj.getY()).equals(
						associatedObjects.get(p))
						|| (associatedObjects.get(p).getX() == 0 && associatedObjects
								.get(p).getY() == 0)) {
					Logger.println("obj id " + i + " is associated with quest "
							+ getName());
					return true;
				} else
					Logger.println("obj id " + i
							+ " is NOT associated with quest " + getName());
		}
		return false;
	}

	/**
	 * Sends a quest message to the player
	 */
	public void sendMessage(Player player, String message) {
		player.getActionSender().sendMessage("@que@" + message);
	}

	/**
	 * Inform of npc chat
	 */
	public void sendChat(String msg, Npc npc, Player player) {
		sendChat(msg, npc, player, DEFAULT_DELAY);
	}

	/**
	 * Inform of npc chat
	 */
	public void sendChat(String msg, Npc npc, Player player, long delay) {
		try {
			if (player != null && npc != null)
				npc.blockedBy(player);
		} catch (java.util.ConcurrentModificationException cme) {
		} catch (Exception e) {
			e.printStackTrace();
		}

		player.informOfNpcMessage(new ChatMessage(npc, msg, player));
		sleep(delay);
	}

	/**
	 * Inform of player chat
	 */
	public void sendChat(String msg, Player player, Npc npc) {
		sendChat(msg, player, npc, DEFAULT_DELAY);
	}

	/**
	 * Inform of player chat
	 */
	public void sendChat(String msg, Player player, Npc npc, long delay) {
		try {
			if (player != null && npc != null)
				npc.blockedBy(player);
		} catch (java.util.ConcurrentModificationException cme) {
		} catch (Exception e) {
			e.printStackTrace();
		}

		player.informOfChatMessage(new ChatMessage(player, msg, npc));
		sleep(delay);
	}

	/**
	 * Adds the given short event
	 */
	public void addSingleEvent(SingleEvent event) {
		world.getDelayedEventHandler().add(event);
	}

	/**
	 * @return if the given NPC is visible to the player
	 */
	public boolean isNpcVisible(Npc npc, Player player) {
		return true;
	}

	/**
	 * @return if the given NPC is shootable
	 */
	public boolean isNpcShootable(Npc npc, Player player) {
		return true;
	}

	/**
	 * @return if the given Item is visible to the player
	 */
	public boolean isItemVisible(Item item, Player player) {
		return true;
	}

	/**
	 * @return if this quest is to be loaded or not (used for when a quest is
	 *         not finished yet, and shouldn't be loaded)
	 */
	public boolean loadQuest() {
		return true;
	}

	/**
	 * Pauses the quest for 'ms' milliseconds
	 */
	@SuppressWarnings("static-access")
	public void sleep(long ms) {
		if (ms <= 0)
			return;

		try {
			thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Sleeps for the DEFAULT time
	 */
	public void sleep() {
		sleep(DEFAULT_DELAY);
	}

	/**
	 * Sets the default delay
	 */
	public void setDefaultDelay(long l) {
		DEFAULT_DELAY = l;
	}

	/**
	 * @return which option the player chooses out of the given menu
	 */
	public int getMenuOption(final Player player, long timeout, String... menu) {
		player.setLastQuestMenuReply(-1);
		player.getActionSender().sendMenu(menu);
		player.setMenuHandler(new MenuHandler(menu) {
			public void handleReply(int option, String reply) {
				player.setLastQuestMenuReply(option);
				sendChat(reply, player, null, 0);
			}
		});

		long start = GameEngine.getTime();
		while (player.getLastQuestMenuReply() == -1
				&& (GameEngine.getTime() - start) <= timeout) {
			if (player.getLastQuestMenuReply() == -2)
				return -1;
			sleep(10);
		}

		return player.getLastQuestMenuReply();
	}

	/**
	 * @return which option the player chooses out of the given menu
	 */
	public int getMenuOption(final Player player, String... menu) {
		return getMenuOption(player, 30000, menu); // 30 sec default timeout
	}

	/**
	 * Queues the given player-to-npc chat
	 */
	public void queueChat(Player player, Npc npc, long delay, String... chat) {
		int idx = 0;
		while (idx < chat.length)
			sendChat(chat[idx++], player, npc);
	}

	/**
	 * Queues the given player-to-npc chat
	 */
	public void queueChat(Player player, Npc npc, String... chat) {
		queueChat(player, npc, DEFAULT_DELAY, chat);
	}

	/**
	 * Queues the given npc-to-player chat
	 */
	public void queueChat(Npc npc, Player player, long delay, String... chat) {
		int idx = 0;
		while (idx < chat.length)
			sendChat(chat[idx++], npc, player);
	}

	/**
	 * Queues the given player-to-npc chat
	 */
	public void queueChat(Npc npc, Player player, String... chat) {
		queueChat(npc, player, DEFAULT_DELAY, chat);
	}

	/**
	 * @return if the player is wielding the given item id
	 */
	public boolean isWielding(Player player, int id) {
		return player.getInventory().wielding(id);
	}

	/**
	 * Adds the given InvItems to the player's inventory
	 */
	public void giveItem(Player player, InvItem... items) {
		boolean coins = false;

		for (InvItem i : items) {
			player.getInventory().add(i);

			if (i.getID() == 10)
				coins = true;
		}

		player.getActionSender().sendSound(coins ? "coins" : "click");
		player.getActionSender().sendInventory();
	}

	/**
	 * Adds the given item ids to the player's inventory
	 */
	public void giveItem(Player player, int... ids) {
		boolean coins = false;

		for (int i : ids) {
			player.getInventory().add(new InvItem(i, 1));

			if (i == 10)
				coins = true;
		}

		player.getActionSender().sendSound(coins ? "coins" : "click");
		player.getActionSender().sendInventory();
	}

	/**
	 * Removes the given InvItems from the player's inventory
	 */
	public void takeItem(Player player, InvItem... items) {
		boolean coins = false;

		for (InvItem i : items) {
			player.getInventory().remove(i);

			if (i.getID() == 10)
				coins = true;
		}

		player.getActionSender().sendSound(coins ? "coins" : "click");
		player.getActionSender().sendInventory();
	}

	/**
	 * Removes the given item ids from the player's inventory
	 */
	public void takeItem(Player player, int... ids) {
		boolean coins = false;

		for (int i : ids) {
			player.getInventory().remove(new InvItem(i, 1));

			if (i == 10)
				coins = true;
		}

		player.getActionSender().sendSound(coins ? "coins" : "click");
		player.getActionSender().sendInventory();
	}

	/**
	 * @return if the player has the given InvItem
	 */
	public boolean hasItem(Player player, InvItem... items) {
		boolean has = true;

		for (InvItem i : items) {
			if (!player.getInventory().hasItemId(i.getID())) {
				has = false;
				break;
			}
		}

		return has;
	}

	/**
	 * @return if the player has the given item ids
	 */
	public boolean hasItem(Player player, int... items) {
		boolean has = true;

		for (int i : items) {
			if (!player.getInventory().hasItemId(i)) {
				has = false;
				break;
			}
		}

		return has;
	}

	/**
	 * @return the amount of the given item the player has
	 */
	public int countItem(Player player, InvItem item) {
		return player.getInventory().countId(item.getID());
	}

	/**
	 * @return the amount of the given item id the player has
	 */
	public int countItem(Player player, int id) {
		return player.getInventory().countId(id);
	}

	/**
	 * @return if the player has the given item in his inventory, bank or if
	 *         it's on the ground somewhere
	 */
	public boolean hasItemAbs(Player player, int id) {
		if (player.getInventory().hasItemId(id))
			return true;

		if (player.getBank().hasItemId(id))
			return true;

		ActiveTile[][] tiles = player.getViewArea().getViewedArea(20, 20, 20,
				20);
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				ActiveTile t = tiles[x][y];
				if (t != null) {
					for (Item i : t.getItems()) {
						if (i.getID() == id) {
							if (i.getOwner().equals(player))
								return true;
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * Sets this quest's stage
	 */
	public void setStage(Player player, int stage) {
		player.setQuestStage(getUniqueID(), stage);
	}

	/**
	 * @return this quest's stage
	 */
	public int getQuestStage(Player player) {
		return player.getQuestStage(getUniqueID());
	}
}