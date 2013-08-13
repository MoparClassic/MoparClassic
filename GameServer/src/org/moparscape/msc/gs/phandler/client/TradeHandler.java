package org.moparscape.msc.gs.phandler.client;

import java.util.List;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.builders.ls.MiscPacketBuilder;
import org.moparscape.msc.gs.config.Formulae;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.db.DataManager;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.container.Inventory;
import org.moparscape.msc.gs.model.definition.entity.ItemDef;
import org.moparscape.msc.gs.model.definition.skill.ItemWieldableDef;
import org.moparscape.msc.gs.model.landscape.PathGenerator;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.service.ItemAttributes;
import org.moparscape.msc.gs.tools.DataConversions;
import org.moparscape.msc.gs.util.Logger;

public class TradeHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	private boolean busy(Player player) {
		return player.isBusy() || player.isRanging() || player.accessingBank()
				|| player.isDueling();
	}

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();
		Player affectedPlayer;

		if (busy(player)) {
			affectedPlayer = player.getWishToTrade();
			unsetOptions(player);
			unsetOptions(affectedPlayer);
			return;
		}
		switch (pID) {
		case 166: // Sending trade request
			affectedPlayer = world.getPlayer(p.readShort());
			if (affectedPlayer == null)
				return;
			if (affectedPlayer.isTrading()) {
				player.getActionSender().sendMessage(
						"That person is already trading");
				return;
			}
			if (affectedPlayer == null || affectedPlayer.isDueling()
					|| !player.withinRange(affectedPlayer, 8)
					|| player.isTrading() || player.tradeDuelThrottling()) {
				unsetOptions(player);
				return;
			}
			if (affectedPlayer == player) {
				player.setSuspiciousPlayer(true);
				Logger.println("Warning: " + player.getUsername()
						+ " tried to trade to himself.");
				unsetOptions(player);
				return;
			}
			if (!new PathGenerator(player.getX(), player.getY(),
					affectedPlayer.getX(), affectedPlayer.getY()).isValid()) {
				player.getActionSender().sendMessage(
						"you can't reach this person");
				unsetOptions(player);
				return;
			}

			if ((affectedPlayer.getPrivacySetting(2) && !affectedPlayer
					.isFriendsWith(player.getUsernameHash()))
					|| affectedPlayer.isIgnoring(player.getUsernameHash())) {
				player.getActionSender().sendMessage(
						"This player has trade requests blocked.");
				return;
			}

			player.setWishToTrade(affectedPlayer);
			player.getActionSender().sendMessage(
					affectedPlayer.isTrading() ? affectedPlayer.getUsername()
							+ " is already in a trade"
							: "Sending trade request");
			affectedPlayer.getActionSender().sendMessage(
					player.getUsername() + " wishes to trade with you");

			if (!player.isTrading() && affectedPlayer.getWishToTrade() != null
					&& affectedPlayer.getWishToTrade().equals(player)
					&& !affectedPlayer.isTrading()) {
				player.setTrading(true);
				player.resetPath();
				player.resetAllExceptTrading();
				affectedPlayer.setTrading(true);
				affectedPlayer.resetPath();
				affectedPlayer.resetAllExceptTrading();

				player.getActionSender().sendTradeWindowOpen();
				affectedPlayer.getActionSender().sendTradeWindowOpen();
				world.addEntryToSnapshots(new Activity(player.getUsername(),
						player.getUsername() + " sent trade request "
								+ affectedPlayer.getUsername() + " at: "
								+ player.getX() + "/" + player.getY() + " | "
								+ affectedPlayer.getX() + "/"
								+ affectedPlayer.getY()));

			}
			break;
		case 211: // Trade accepted
			affectedPlayer = player.getWishToTrade();
			if (affectedPlayer == null || busy(affectedPlayer)
					|| !player.isTrading() || !affectedPlayer.isTrading()) { // This
				// shouldn't
				// happen
				player.setSuspiciousPlayer(true);
				unsetOptions(player);
				unsetOptions(affectedPlayer);
				return;
			}

			player.setTradeOfferAccepted(true);

			player.getActionSender().sendTradeAcceptUpdate();
			affectedPlayer.getActionSender().sendTradeAcceptUpdate();

			if (affectedPlayer.isTradeOfferAccepted()) {
				player.getActionSender().sendTradeAccept();
				affectedPlayer.getActionSender().sendTradeAccept();
			}
			world.addEntryToSnapshots(new Activity(player.getUsername(), player
					.getUsername()
					+ " accepted trade "
					+ affectedPlayer.getUsername()
					+ " at: "
					+ player.getX()
					+ "/"
					+ player.getY()
					+ " | "
					+ affectedPlayer.getX()
					+ "/"
					+ affectedPlayer.getY()));

			break;
		case 53: // Confirm accepted
			affectedPlayer = player.getWishToTrade();
			if (affectedPlayer == null || busy(affectedPlayer)
					|| !player.isTrading() || !affectedPlayer.isTrading()
					|| !player.isTradeOfferAccepted()
					|| !affectedPlayer.isTradeOfferAccepted()) { // This
				// shouldn't
				// happen
				player.setSuspiciousPlayer(true);
				unsetOptions(player);
				unsetOptions(affectedPlayer);
				return;
			}
			player.setTradeConfirmAccepted(true);

			if (affectedPlayer.isTradeConfirmAccepted()) {
				world.addEntryToSnapshots(new Activity(player.getUsername(),
						player.getUsername() + " finished trade "
								+ affectedPlayer.getUsername() + " at: "
								+ player.getX() + "/" + player.getY() + " | "
								+ affectedPlayer.getX() + "/"
								+ affectedPlayer.getY()));

				List<InvItem> myOffer = player.getTradeOffer();
				List<InvItem> theirOffer = affectedPlayer.getTradeOffer();

				if (!affectedPlayer.getInventory().canHold(myOffer, theirOffer)) {
					player.getActionSender()
							.sendMessage(
									"The other player does not have room to accept your items.");
					affectedPlayer
							.getActionSender()
							.sendMessage(
									"You do not have room in your inventory to hold those items.");
					unsetOptions(player);
					unsetOptions(affectedPlayer);
					return;
				}
				if (!player.getInventory().canHold(theirOffer, myOffer)) {
					player.getActionSender()
							.sendMessage(
									"You do not have room in your inventory to hold those items.");
					affectedPlayer
							.getActionSender()
							.sendMessage(
									"The other player does not have room to accept your items.");
					unsetOptions(player);
					unsetOptions(affectedPlayer);
					return;
				}

				int slot = -1;
				for (InvItem item : myOffer) {
					slot++;
					if (player.getInventory().countId(item.id) < item.amount) {
						player.setSuspiciousPlayer(true);
						unsetOptions(player);
						unsetOptions(affectedPlayer);
						return;
					}
					if (item.getDef().isMembers() && !World.isMembers()) {
						player.getActionSender().sendMessage(
								"You can not trade members items.");
						unsetOptions(player);
						unsetOptions(affectedPlayer);
						return;

					}

					if (player.getInventory().getSlot(slot).wielded) {
						player.getInventory().setWield(slot, false);
						ItemWieldableDef def = ItemAttributes
								.getWieldable(item.id);
						player.updateWornItems(
								def.getWieldPos(),
								player.getPlayerAppearance().getSprite(
										def.getWieldPos()));
					}
					player.getInventory().remove(item.id, item.amount, false);
				}
				slot = -1;
				for (InvItem item : theirOffer) {
					slot++;
					if (affectedPlayer.getInventory().countId(item.id) < item.amount) {
						affectedPlayer.setSuspiciousPlayer(true);
						unsetOptions(player);
						unsetOptions(affectedPlayer);
						return;
					}
					if (item.getDef().isMembers() && !World.isMembers()) {
						affectedPlayer.getActionSender().sendMessage(
								"You can not trade members items.");
						unsetOptions(player);
						unsetOptions(affectedPlayer);
						return;

					}

					if (affectedPlayer.getInventory().getSlot(slot).wielded) {
						affectedPlayer.getInventory().setWield(slot, false);
						ItemWieldableDef def = ItemAttributes
								.getWieldable(item.id);
						affectedPlayer.updateWornItems(
								def.getWieldPos(),
								affectedPlayer.getPlayerAppearance().getSprite(
										def.getWieldPos()));
					}
					affectedPlayer.getInventory().remove(item.id, item.amount,
							false);
				}
				MiscPacketBuilder loginServer = Instance.getServer()
						.getLoginConnector().getActionSender();
				long playerhash = DataConversions.usernameToHash(player
						.getUsername());
				long affectedPlayerhash = DataConversions
						.usernameToHash(affectedPlayer.getUsername());
				for (InvItem item : myOffer) {
					affectedPlayer.getInventory().add(item.id, item.amount,
							false);
				}
				for (InvItem item : theirOffer) {
					player.getInventory().add(item.id, item.amount, false);

				}
				boolean senddata = false;
				for (InvItem item : myOffer) {
					loginServer.tradeLog(playerhash, affectedPlayerhash,
							item.id, item.amount, player.getX(), player.getY(),
							1);
					if (item.amount > 10000000 || Formulae.isRareItem(item.id))
						senddata = true;
				}
				if (senddata)
					DataManager.reportHandler.submitDupeData(
							DataConversions.hashToUsername(playerhash),
							playerhash);
				senddata = false;
				for (InvItem item : theirOffer) {
					loginServer.tradeLog(affectedPlayerhash, playerhash,
							item.id, item.amount, player.getX(), player.getY(),
							1);
					if (item.amount > 10000000 || Formulae.isRareItem(item.id))
						senddata = true;
				}
				if (senddata)
					DataManager.reportHandler.submitDupeData(
							DataConversions.hashToUsername(affectedPlayerhash),
							affectedPlayerhash);

				player.getActionSender().sendInventory();
				player.getActionSender().sendEquipmentStats();
				long now = GameEngine.getTime();
				player.save();
				player.setLastSaveTime(now);
				affectedPlayer.save();
				affectedPlayer.setLastSaveTime(now);
				player.getActionSender().sendMessage("Trade completed.");

				affectedPlayer.getActionSender().sendInventory();
				affectedPlayer.getActionSender().sendEquipmentStats();
				affectedPlayer.getActionSender()
						.sendMessage("Trade completed.");

				unsetOptions(player);
				unsetOptions(affectedPlayer);
			}
			break;
		case 216: // Trade declined
			affectedPlayer = player.getWishToTrade();
			if (affectedPlayer == null || busy(affectedPlayer)
					|| !player.isTrading() || !affectedPlayer.isTrading()) { // This
				// shouldn't
				// happen
				player.setSuspiciousPlayer(true);
				unsetOptions(player);
				unsetOptions(affectedPlayer);
				return;
			}
			affectedPlayer.getActionSender().sendMessage(
					player.getUsername() + " has declined the trade.");

			unsetOptions(player);
			unsetOptions(affectedPlayer);
			break;
		case 70: // Receive offered item data
			affectedPlayer = player.getWishToTrade();
			if (affectedPlayer == null
					|| busy(affectedPlayer)
					|| !player.isTrading()
					|| !affectedPlayer.isTrading()
					|| (player.isTradeOfferAccepted() && affectedPlayer
							.isTradeOfferAccepted())
					|| player.isTradeConfirmAccepted()
					|| affectedPlayer.isTradeConfirmAccepted()) { // This
				// shouldn't
				// happen
				player.setSuspiciousPlayer(true);
				unsetOptions(player);
				unsetOptions(affectedPlayer);
				return;
			}

			player.setTradeOfferAccepted(false);
			player.setTradeConfirmAccepted(false);
			affectedPlayer.setTradeOfferAccepted(false);
			affectedPlayer.setTradeConfirmAccepted(false);

			// player.getActionSender().sendTradeAcceptUpdate();
			// affectedPlayer.getActionSender().sendTradeAcceptUpdate();

			Inventory tradeOffer = new Inventory(player);
			player.resetTradeOffer();
			int count = (int) p.readByte();
			for (int slot = 0; slot < count; slot++) {
				InvItem tItem = new InvItem(p.readShort(), p.readInt());
				if (tItem.amount < 1) {
					player.setSuspiciousPlayer(true);
					player.setRequiresOfferUpdate(true);
					continue;
				}
				ItemDef def = tItem.getDef();
				if (!def.canTrade() && !player.isMod()) {
					player.getActionSender().sendMessage(
							def.getName()
									+ " cannot be traded with other players");
					player.setRequiresOfferUpdate(true);
					continue;
				}
				tradeOffer.add(tItem.id, tItem.amount, false);
			}
			for (InvItem item : tradeOffer.getItems()) {
				if (tradeOffer.countId(item.id) > player.getInventory()
						.countId(item.id)) {
					player.setSuspiciousPlayer(true);
					unsetOptions(player);
					unsetOptions(affectedPlayer);
					return;
				}
				player.addToTradeOffer(item);
			}
			player.setRequiresOfferUpdate(true);
			break;
		}
	}

	private void unsetOptions(Player p) {
		if (p == null) {
			return;
		}
		p.resetTrading();
	}

}
