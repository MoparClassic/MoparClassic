package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Inventory;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.Shop;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.util.Logger;

public class ShopHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();
		if (player.isBusy()) {
			player.resetShop();
			return;
		}
		final Shop shop = player.getShop();
		if (shop == null) {
			player.setSuspiciousPlayer(true);
			player.resetShop();
			return;
		}
		int value;
		InvItem item;
		switch (pID) {
		case 253: // Close shop
			player.resetShop();
			world.addEntryToSnapshots(new Activity(player.getUsername(), player
					.getUsername()
					+ " closed shop window at: "
					+ player.getX()
					+ "/" + player.getY()));

			break;
		case 128: // Buy item

			Short s = p.readShort();
			item = new InvItem(s, 1);
			world.addEntryToSnapshots(new Activity(player.getUsername(), player
					.getUsername()
					+ " tried to buy item ("
					+ s
					+ "): "
					+ player.getX() + "/" + player.getY()));

			if (item.getDef().isMembers() && !World.isMembers()) {
				player.getActionSender().sendMessage(
						"This feature is only avaliable on a members server");
				return;
			}
			value = p.readInt();
			if (value < item.getDef().basePrice) {
				Logger.println("[SHOPDUPE] " + player.getUsername()
						+ " tried to buy " + item.getDef().name + " for "
						+ value);
				return;
			}
			if (shop.countId(item.getID()) < 1)
				return;
			if (player.getInventory().countId(10) < value) {
				player.getActionSender().sendMessage(
						"You don't have enough money to buy that!");
				return;
			}
			if ((Inventory.MAX_SIZE - player.getInventory().size())
					+ player.getInventory().getFreedSlots(
							new InvItem(10, value)) < player.getInventory()
					.getRequiredSlots(item)) {
				player.getActionSender().sendMessage(
						"You don't have room for that in your inventory");
				return;
			}
			int itemprice = Formulae.getPrice(
					shop.getItems()
							.get(Formulae.getItemPos(shop, item.getID())),
					shop, true);
			int sellprice = Formulae.getPrice(
					shop.getItems()
							.get(Formulae.getItemPos(shop, item.getID())),
					shop, false);
			if (itemprice == 0)
				return;

			if (sellprice >= itemprice)
				return;
			if (player.getInventory().remove(10, itemprice) > -1) {
				shop.remove(item);
				player.getInventory().add(item);
				player.getActionSender().sendSound("coins");
				player.getActionSender().sendInventory();
				shop.updatePlayers();
			}
			break;
		case 255: // Sell item
			Short s1 = p.readShort();
			item = new InvItem(s1, 1);
			world.addEntryToSnapshots(new Activity(player.getUsername(), player
					.getUsername()
					+ " tried to sell item ("
					+ s1
					+ "): "
					+ player.getX() + "/" + player.getY()));

			value = p.readInt();
			if (!item.getDef().canTrade()) {
				player.getActionSender().sendMessage(
						"You cannot sell this item.");
				return;
			}
			if (item.getDef().isMembers() && !World.isMembers()) {
				player.getActionSender().sendMessage(
						"This feature is only avaliable on a members server");
				return;
			}
			if (player.getInventory().countId(item.getID()) < 1) {
				return;
			}
			if (!shop.shouldStock(item.getID())) {
				return;
			}
			if (!shop.canHold(item)) {
				player.getActionSender().sendMessage(
						"The shop is currently full!");
				return;
			}
			int itempricez;
			if (Formulae.getItemPos(shop, item.getID()) == -1) {
				itempricez = Formulae.getPrice(new InvItem(item.getID(), 0),
						shop, false);
			} else {
				itempricez = Formulae.getPrice(
						shop.getItems().get(
								Formulae.getItemPos(shop, item.getID())), shop,
						false);
			}
			if (itempricez > 300000)
				return;
			if (player.getInventory().remove(item) > -1) {
				player.getInventory().add(new InvItem(10, itempricez));
				shop.add(item);
				player.getActionSender().sendSound("coins");
				player.getActionSender().sendInventory();
				shop.updatePlayers();
			}
			break;
		}
	}
}
