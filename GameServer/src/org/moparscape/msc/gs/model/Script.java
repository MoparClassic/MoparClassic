package org.moparscape.msc.gs.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ConcurrentModificationException;

import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.external.EntityHandler;
import org.moparscape.msc.gs.util.Logger;

import bsh.EvalError;

public class Script {

	public Player player;
	public Npc npc;

	/**
	 * DONT TOUCH THIS. Sets the Player and Npc instances for reach when
	 * scripting.
	 * 
	 * @param p
	 *            - the player
	 * @param n
	 *            - the affected npc
	 */
	public Script(Player p, Npc n) {
		player = p;
		npc = n;

		try {
			player.getInterpreter().getNameSpace().importObject(this);
			player.getInterpreter().source(
					World.getWorld().npcScripts.get(npc.getID()));
			player.getInterpreter().getNameSpace().clear();
		} catch (EvalError e) {
			error();
			// e.printStackTrace();
		} catch (FileNotFoundException e) {
			error();
			// e.printStackTrace();
		} catch (IOException e) {
			error();
			// e.printStackTrace();
		} catch (ConcurrentModificationException cme) {
			Logger.println("got cme");
		} catch (Exception e) {
			error();
			// e.printStackTrace();
		}

	}

	/**
	 * Unblock the NPC/Player if something unhandled happens.
	 */
	private void error() {
		npc.unblock();
		player.setBusy(false);
	}

	/**
	 * Sends a normal Message to the player
	 * 
	 * @param msg
	 *            - the message
	 */
	public void SendMessage(String msg) {
		player.getActionSender().sendMessage(msg);
	}

	/**
	 * Sends a large black box Message to the player
	 * 
	 * @param msg
	 *            - the message
	 */
	public void SendBoxMessage(String msg) {
		player.getActionSender().sendAlert(msg, true);
	}

	/**
	 * Sends a medium or large black box message
	 * 
	 * @param msg
	 *            - the message
	 * @param big
	 *            - true if large box, false if medium
	 */
	public void SendBoxMessage(String msg, boolean big) {
		player.getActionSender().sendAlert(msg, big);
	}

	/**
	 * Add item(s) to the players inventory (This is setup to handle the amount
	 * even when the item is non stackable)
	 * 
	 * @param id
	 *            - the item's ID
	 * @param amount
	 *            - the amount given
	 */
	public void AddItem(int id, int amount) {
		InvItem item = new InvItem(id, amount);
		if (item.getDef().stackable) {
			player.getInventory().add(new InvItem(id, amount));
		} else {
			for (int i = 0; i < amount; i++) {
				player.getInventory().add(new InvItem(id));
			}
		}
		player.getActionSender().sendInventory();
	}

	/**
	 * Says something to the NPC your talking too
	 * 
	 * @param msg
	 *            - the message
	 */
	public void PlayerTalk(String msg) {
		player.informOfChatMessage(new ChatMessage(player, msg, npc));
		Wait(2000);

	}

	/**
	 * the Npc your interacting with says something to you
	 * 
	 * @param msg
	 *            - the message
	 */
	public void NpcTalk(String msg) {
		player.informOfNpcMessage(new ChatMessage(npc, msg, player));
		Wait(2000);
	}

	/**
	 * Sleeps for the default delay (2000)
	 */
	public void Wait() {
		Wait(2000);
	}

	/**
	 * Wait's the delay until the next line is executed
	 */
	@SuppressWarnings("static-access")
	public void Wait(int ms) {
		try {
			Thread.currentThread().sleep(ms);
		} catch (InterruptedException e) {
			SendMessage(e.getMessage());
		}
	}

	/**
	 * AUTO EXPANDING arrays (String...) with BeanShell do not yet work. I know
	 * this looks dodgey, but easy-scripting support comes first. Currently set
	 * for 7 options max.
	 */
	public int PickOption(String s1, String s2, String s3, String s4,
			String s5, String s6, String s7) {
		return PickOption(new String[] { s1, s2, s3, s4, s5, s6, s7 });
	}

	public int PickOption(String s1, String s2, String s3, String s4,
			String s5, String s6) {
		return PickOption(new String[] { s1, s2, s3, s4, s5, s6 });
	}

	public int PickOption(String s1, String s2, String s3, String s4, String s5) {
		return PickOption(new String[] { s1, s2, s3, s4, s5 });
	}

	public int PickOption(String s1, String s2, String s3, String s4) {
		return PickOption(new String[] { s1, s2, s3, s4 });
	}

	public int PickOption(String s1, String s2, String s3) {
		return PickOption(new String[] { s1, s2, s3 });
	}

	public int PickOption(String s1, String s2) {
		return PickOption(new String[] { s1, s2 });
	}

	public int PickOption(String s1) {
		return PickOption(new String[] { s1 });
	}

	/**
	 * Sends a question menu, waits for the response.
	 * 
	 * @param strs
	 *            - array of options
	 * @return the option number
	 */
	public int PickOption(String[] strs) {
		try {
			long time = GameEngine.getTime();
			player.setBusy(false);
			player.setLastOption(-2);
			player.setMenuHandler(new MenuHandler(strs) {
				public void handleReply(int option, String reply) {
					if (option < 0 || option >= getOptions().length) {
						npc.unblock();
						player.setBusy(false);
						owner.setLastOption(-1);
						return;
					}
					owner.setLastOption(option);
				}
			});
			player.getActionSender().sendMenu(strs);
			while (player.getLastOption() == -2
					&& GameEngine.getTime() - time < 20000) { // timeout
				Wait(25);
			}
			if (player.getLastOption() == -1 || player.getLastOption() == -2) {
				player.setBusy(false);
				npc.unblock();
				return -1;
			}
			player.setBusy(true);
			int newOpt = player.getLastOption();
			player.setLastOption(-2);
			PlayerTalk(strs[newOpt]);
			return newOpt + 1;
		} catch (Exception e) {

			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Teleports you from x to y (without a bubble)
	 * 
	 * @param x
	 *            - new x axis
	 * @param y
	 *            - new y axis
	 */
	public void Teleport(int x, int y) {
		player.teleport(x, y, false);
	}

	/**
	 * Removes all the items from inventory that you specify
	 * 
	 * @param id
	 *            - the item id to remove
	 */
	public void RemoveAllItem(int id) {
		RemoveItem(id, player.getInventory().countId(id));
	}

	/**
	 * Checks inventory for items/amounts.
	 * 
	 * @param id
	 *            - the items id
	 * @param amount
	 *            - the amount
	 * @return true if it has the items.
	 */
	public boolean HasItem(int id, int amount) {
		if (EntityHandler.getItemDef(id).stackable) {
			for (InvItem i : player.getInventory().getItems()) {
				if (i.getID() == id && i.getAmount() >= amount)
					return true;
			}
		} else {
			int count = 0;
			for (InvItem i : player.getInventory().getItems()) {
				if (i.getID() == id)
					count++;
			}
			if (count >= amount)
				return true;
		}
		return false;
	}

	/**
	 * Removes item(s) from your inventory
	 * 
	 * @param id
	 *            - the item id
	 * @param amount
	 *            - the amount
	 */
	public void RemoveItem(int id, int amount) {
		if (EntityHandler.getItemDef(id).stackable) {
			player.getInventory().remove(id, amount);
		} else {
			for (int i = 0; i < amount; i++)
				player.getInventory().remove(id, 1);
		}
		player.getActionSender().sendInventory();
	}

	/**
	 * Adds experience to the selected Stat.
	 * 
	 * @param stat
	 *            - the stat number
	 * @param amount
	 *            - the amount
	 * 
	 */
	public void AddExp(int stat, int amount) {
		player.incExp(stat, amount, true);
	}

	/**
	 * Gets the free space in your inventory
	 * 
	 * @return - the free slots
	 */
	public int GetInventoryFreeSpace() {
		return 30 - player.getInventory().size();
	}

	/**
	 * Gets the max level of a stat
	 * 
	 * @param stat
	 * @return
	 */
	public int GetStatMaxLevel(int stat) {
		return player.getMaxStat(stat);
	}

	/**
	 * Gets the current level of a stat
	 * 
	 * @param stat
	 * @return
	 */
	public int GetStatCurLevel(int stat) {
		return player.getCurStat(stat);
	}

	/**
	 * Adds an item to your bank
	 * 
	 * @param id
	 *            - the item id
	 * @param amount
	 *            - the amount
	 */
	public void AddBankItem(int id, int amount) {
		player.getBank().add(new InvItem(id, amount));
	}

	public int CountItem(int id) {
		return player.getInventory().countId(id);
	}

	public static final int ATTACK = 0;
	public static final int DEFENSE = 1;
	public static final int STRENGTH = 2;
	public static final int HITS = 3;
	public static final int RANGED = 4;
	public static final int PRAYER = 5;
	public static final int MAGIC = 6;
	public static final int COOKING = 7;
	public static final int WOODCUT = 8;
	public static final int FLETCHING = 9;
	public static final int FISHING = 10;
	public static final int FIREMAKING = 11;
	public static final int CRAFTING = 12;
	public static final int SMITHING = 13;
	public static final int MINING = 14;
	public static final int HERBLAW = 15;
	public static final int AGILITY = 16;
	public static final int THIEVING = 17;
}
