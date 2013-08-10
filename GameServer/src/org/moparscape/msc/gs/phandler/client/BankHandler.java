package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.container.Bank;
import org.moparscape.msc.gs.model.container.Inventory;
import org.moparscape.msc.gs.model.definition.EntityHandler;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.util.Logger;

public class BankHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();
		if (player.isBusy() || player.isRanging() || player.isTrading()
				|| player.isDueling()) {
			player.resetBank();
			return;
		}
		if (!player.accessingBank()) {
			player.setSuspiciousPlayer(true);
			player.resetBank();
			return;
		}
		Bank bank = player.getBank();
		Inventory inventory = player.getInventory();
		int itemID, amount, slot;
		switch (pID) {
		case 48: // Close bank
			player.resetBank();
			break;
		case 198: // Deposit item
			itemID = p.readShort();
			amount = p.readInt();
			if (amount < 1 || inventory.countId(itemID) < amount) {
				world.addEntryToSnapshots(new Activity(player.getUsername(),
						player.getUsername() + " tried to deposit ID: "
								+ itemID + " amount: " + amount));
				player.setSuspiciousPlayer(true);
				return;
			}
			world.addEntryToSnapshots(new Activity(player.getUsername(), player
					.getUsername()
					+ " deposited ID: "
					+ itemID
					+ " amount: "
					+ amount));
			if (EntityHandler.getItemDef(itemID).isMembers()
					&& !World.isMembers()) {
				player.getActionSender().sendMessage(
						"This feature is only avaliable on a members server");
				return;
			}
			if (bank.canHold(itemID, amount)
					&& inventory.remove(itemID, amount, false)) {
				bank.add(itemID, amount, false);
			} else {
				player.getActionSender().sendMessage(
						"You don't have room for that in your bank");
			}
			slot = bank.getLastItemSlot(itemID);
			if (slot > -1) {
				player.getActionSender().sendInventory();
				player.getActionSender().updateBankItem(slot, itemID,
						bank.countId(itemID));
			}
			break;
		case 183: // Withdraw item
			itemID = p.readShort();
			amount = p.readInt();
			if (amount < 1 || bank.countId(itemID) < amount) {
				world.addEntryToSnapshots(new Activity(player.getUsername(),
						player.getUsername() + " tried to withdraw ID: "
								+ itemID + " amount: " + amount));
				player.setSuspiciousPlayer(true);
				return;
			}
			if (EntityHandler.getItemDef(itemID).isMembers()
					&& !World.isMembers()) {
				player.getActionSender().sendMessage(
						"This feature is only avaliable on a members server");
				return;
			}

			if (!inventory.isStackable(itemID) && amount > 1) {
				Logger.println(player.getUsername() + " tried to withdraw ID: "
						+ itemID + " amount: " + amount + " unstackable items");
				player.setSuspiciousPlayer(true);
			}

			slot = bank.getLastItemSlot(itemID);
			if (!inventory.canHold(itemID, amount)) {
				amount = inventory.maxSize() - inventory.size();
				if (amount == 0) {
					return;
				}
			}
			world.addEntryToSnapshots(new Activity(player.getUsername(), player
					.getUsername()
					+ " withrew ID: "
					+ itemID
					+ " amount: "
					+ amount));

			if (bank.remove(itemID, amount, false)) {
				inventory.add(itemID, amount, false);
			}

			if (slot > -1) {
				player.getActionSender().sendInventory();
				player.getActionSender().updateBankItem(slot, itemID,
						bank.countId(itemID));
			}
			break;
		}
	}

}