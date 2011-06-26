package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.event.DuelEvent;
import org.moparscape.msc.gs.event.SingleEvent;
import org.moparscape.msc.gs.event.WalkToMobEvent;
import org.moparscape.msc.gs.external.ItemDef;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Inventory;
import org.moparscape.msc.gs.model.PathGenerator;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.states.Action;
import org.moparscape.msc.gs.tools.DataConversions;
import org.moparscape.msc.gs.util.Logger;

public class DuelHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	private boolean busy(Player player) {
		return player.isBusy() || player.isRanging() || player.accessingBank()
				|| player.isTrading();
	}

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();

		Player affectedPlayer = player.getWishToDuel();
		if (player == affectedPlayer) {
			Logger.println("Warning : " + player.getUsername()
					+ " tried to duel himself");
			unsetOptions(player);
			unsetOptions(affectedPlayer);
			return;
		}
		if (player.isPMod() && !player.isMod())
			return;
		if (player.isDuelConfirmAccepted() && affectedPlayer != null
				&& affectedPlayer.isDuelConfirmAccepted()) {
			// If we are actually dueling we shouldn't touch any settings
			// (modify or unset!)
			return;
		}
		if (busy(player) || player.getLocation().inWilderness()) {
			unsetOptions(player);
			unsetOptions(affectedPlayer);
			return;
		}
		if (player.getLocation().inModRoom()) {
			player.getActionSender().sendMessage("You cannot duel in here!");
			unsetOptions(player);
			unsetOptions(affectedPlayer);
			return;
		}
		switch (pID) {
		case 222: // Sending duel request
			affectedPlayer = world.getPlayer(p.readShort());
			if (affectedPlayer == null || affectedPlayer.isDueling()
					|| !player.withinRange(affectedPlayer, 8)
					|| player.isDueling() || player.tradeDuelThrottling()) {
				unsetOptions(player);
				return;
			}
			if (!new PathGenerator(player.getX(), player.getY(),
					affectedPlayer.getX(), affectedPlayer.getY()).isValid()) {
				unsetOptions(player);
				return;
			}

			if ((affectedPlayer.getPrivacySetting(3) && !affectedPlayer
					.isFriendsWith(player.getUsernameHash()))
					|| affectedPlayer.isIgnoring(player.getUsernameHash())) {
				player.getActionSender().sendMessage(
						"This player has duel requests blocked.");
				return;
			}

			player.setWishToDuel(affectedPlayer);
			player.getActionSender().sendMessage(
					affectedPlayer.isDueling() ? affectedPlayer.getUsername()
							+ " is already in a duel" : "Sending duel request");
			affectedPlayer.getActionSender()
					.sendMessage(
							player.getUsername()
									+ " "
									+ Formulae.getLvlDiffColour(affectedPlayer
											.getCombatLevel()
											- player.getCombatLevel())
									+ "(level-" + player.getCombatLevel()
									+ ")@whi@ wishes to duel with you");

			if (!player.isDueling() && affectedPlayer.getWishToDuel() != null
					&& affectedPlayer.getWishToDuel().equals(player)
					&& !affectedPlayer.isDueling()) {
				player.setDueling(true);
				player.resetPath();
				player.clearDuelOptions();
				player.resetAllExceptDueling();
				affectedPlayer.setDueling(true);
				affectedPlayer.resetPath();
				affectedPlayer.clearDuelOptions();
				affectedPlayer.resetAllExceptDueling();

				player.getActionSender().sendDuelWindowOpen();
				affectedPlayer.getActionSender().sendDuelWindowOpen();
				world.addEntryToSnapshots(new Activity(player.getUsername(),
						player.getUsername() + " sent duel request "
								+ affectedPlayer.getUsername() + " at: "
								+ player.getX() + "/" + player.getY() + " | "
								+ affectedPlayer.getX() + "/"
								+ affectedPlayer.getY()));

			}
			break;
		case 252: // Duel accepted
			affectedPlayer = player.getWishToDuel();
			if (affectedPlayer == null || busy(affectedPlayer)
					|| !player.isDueling() || !affectedPlayer.isDueling()) { // This
				// shouldn't
				// happen
				player.setSuspiciousPlayer(true);
				unsetOptions(player);
				unsetOptions(affectedPlayer);
				return;
			}

			player.setDuelOfferAccepted(true);

			player.getActionSender().sendDuelAcceptUpdate();
			affectedPlayer.getActionSender().sendDuelAcceptUpdate();

			if (affectedPlayer.isDuelOfferAccepted()) {
				player.getActionSender().sendDuelAccept();
				affectedPlayer.getActionSender().sendDuelAccept();
			}
			world.addEntryToSnapshots(new Activity(player.getUsername(), player
					.getUsername()
					+ " accepted duel request "
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
		case 87: // Confirm accepted
			affectedPlayer = player.getWishToDuel();
			if (affectedPlayer == null || busy(affectedPlayer)
					|| !player.isDueling() || !affectedPlayer.isDueling()
					|| !player.isDuelOfferAccepted()
					|| !affectedPlayer.isDuelOfferAccepted()) { // This
				// shouldn't
				// happen
				player.setSuspiciousPlayer(true);
				unsetOptions(player);
				unsetOptions(affectedPlayer);
				return;
			}
			player.setDuelConfirmAccepted(true);

			if (affectedPlayer.isDuelConfirmAccepted()) {
				world.addEntryToSnapshots(new Activity(player.getUsername(),
						player.getUsername() + " started duel "
								+ affectedPlayer.getUsername() + " at: "
								+ player.getX() + "/" + player.getY() + " | "
								+ affectedPlayer.getX() + "/"
								+ affectedPlayer.getY()));
				player.getActionSender().sendDuelWindowClose();
				player.getActionSender().sendMessage("Commencing Duel");
				affectedPlayer.getActionSender().sendDuelWindowClose();
				affectedPlayer.getActionSender().sendMessage("Commencing Duel");

				player.resetAllExceptDueling();
				player.setBusy(true);
				player.setStatus(Action.DUELING_PLAYER);

				affectedPlayer.resetAllExceptDueling();
				affectedPlayer.setBusy(true);
				affectedPlayer.setStatus(Action.DUELING_PLAYER);

				if (player.getDuelSetting(3)) {
					for (InvItem item : player.getInventory().getItems()) {
						if (item.isWielded()) {
							item.setWield(false);
							player.updateWornItems(
									item.getWieldableDef().getWieldPos(),
									player.getPlayerAppearance().getSprite(
											item.getWieldableDef()
													.getWieldPos()));
						}
					}
					player.getActionSender().sendSound("click");
					player.getActionSender().sendInventory();
					player.getActionSender().sendEquipmentStats();

					for (InvItem item : affectedPlayer.getInventory()
							.getItems()) {
						if (item.isWielded()) {
							item.setWield(false);
							affectedPlayer.updateWornItems(
									item.getWieldableDef().getWieldPos(),
									affectedPlayer.getPlayerAppearance()
											.getSprite(
													item.getWieldableDef()
															.getWieldPos()));
						}
					}
					affectedPlayer.getActionSender().sendSound("click");
					affectedPlayer.getActionSender().sendInventory();
					affectedPlayer.getActionSender().sendEquipmentStats();

				}

				if (player.getDuelSetting(2)) {
					for (int x = 0; x < 14; x++) {
						if (player.isPrayerActivated(x)) {
							player.removePrayerDrain(x);
							player.setPrayer(x, false);
						}
						if (affectedPlayer.isPrayerActivated(x)) {
							affectedPlayer.removePrayerDrain(x);
							affectedPlayer.setPrayer(x, false);
						}
					}
					player.getActionSender().sendPrayers();
					affectedPlayer.getActionSender().sendPrayers();
				}

				player.setFollowing(affectedPlayer);
				WalkToMobEvent walking = new WalkToMobEvent(player,
						affectedPlayer, 1) {
					public void arrived() {
						Instance.getDelayedEventHandler().add(
								new SingleEvent(owner, 300) {
									public void action() {
										Player affectedPlayer = (Player) affectedMob;
										owner.resetPath();
										if (!owner.nextTo(affectedPlayer)) {
											unsetOptions(owner);
											unsetOptions(affectedPlayer);
											return;
										}
										affectedPlayer.resetPath();

										owner.resetAllExceptDueling();
										affectedPlayer.resetAllExceptDueling();

										owner.setLocation(
												affectedPlayer.getLocation(),
												true);
										for (Player p : owner.getViewArea()
												.getPlayersInView()) {
											p.removeWatchedPlayer(owner);
										}

										owner.setSprite(9);
										owner.setOpponent(affectedMob);
										owner.setCombatTimer();

										affectedPlayer.setSprite(8);
										affectedPlayer.setOpponent(owner);
										affectedPlayer.setCombatTimer();

										Player attacker, opponent;
										if (owner.getCombatLevel() > affectedPlayer
												.getCombatLevel()) {
											attacker = affectedPlayer;
											opponent = owner;
										} else if (affectedPlayer
												.getCombatLevel() > owner
												.getCombatLevel()) {
											attacker = owner;
											opponent = affectedPlayer;
										} else if (DataConversions.random(0, 1) == 1) {
											attacker = owner;
											opponent = affectedPlayer;
										} else {
											attacker = affectedPlayer;
											opponent = owner;
										}
										DuelEvent dueling = new DuelEvent(
												attacker, opponent);
										dueling.setLastRun(0);
										Instance.getDelayedEventHandler().add(
												dueling);
									}
								});
					}

					public void failed() {
						Player affectedPlayer = (Player) affectedMob;
						owner.getActionSender().sendMessage(
								"Error walking to "
										+ affectedPlayer.getUsername()
										+ " (known bug)");
						affectedPlayer.getActionSender().sendMessage(
								"Error walking to " + owner.getUsername()
										+ " (known bug)");
						unsetOptions(owner);
						unsetOptions(affectedPlayer);
						owner.setBusy(false);
						affectedPlayer.setBusy(false);
					}
				};
				walking.setLastRun(GameEngine.getTime() + 500);
				Instance.getDelayedEventHandler().add(walking);
			}
			break;
		case 35: // Decline duel
			affectedPlayer = player.getWishToDuel();
			if (affectedPlayer == null || busy(affectedPlayer)
					|| !player.isDueling() || !affectedPlayer.isDueling()) { // This
				// shouldn't
				// happen
				player.setSuspiciousPlayer(true);
				unsetOptions(player);
				unsetOptions(affectedPlayer);
				return;
			}
			affectedPlayer.getActionSender().sendMessage(
					player.getUsername() + " has declined the duel.");

			unsetOptions(player);
			unsetOptions(affectedPlayer);
			world.addEntryToSnapshots(new Activity(player.getUsername(), player
					.getUsername()
					+ " declined duel "
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
		case 123: // Receive offered item data
			affectedPlayer = player.getWishToDuel();
			if (affectedPlayer == null
					|| busy(affectedPlayer)
					|| !player.isDueling()
					|| !affectedPlayer.isDueling()
					|| (player.isDuelOfferAccepted() && affectedPlayer
							.isDuelOfferAccepted())
					|| player.isDuelConfirmAccepted()
					|| affectedPlayer.isDuelConfirmAccepted()) { // This
				// shouldn't
				// happen
				player.setSuspiciousPlayer(true);
				unsetOptions(player);
				unsetOptions(affectedPlayer);
				return;
			}

			player.setDuelOfferAccepted(false);
			player.setDuelConfirmAccepted(false);
			affectedPlayer.setDuelOfferAccepted(false);
			affectedPlayer.setDuelConfirmAccepted(false);

			player.getActionSender().sendDuelAcceptUpdate();
			affectedPlayer.getActionSender().sendDuelAcceptUpdate();

			Inventory duelOffer = new Inventory();
			player.resetDuelOffer();
			int count = (int) p.readByte();
			for (int slot = 0; slot < count; slot++) {
				InvItem tItem = new InvItem(p.readShort(), p.readInt());
				if (tItem.getAmount() < 1) {
					player.setSuspiciousPlayer(true);
					continue;
				}
				ItemDef def = tItem.getDef();
				if (!def.canTrade()) {
					player.getActionSender().sendMessage(
							def.getName()
									+ " cannot be traded with other players");
					player.setRequiresOfferUpdate(true);
					continue;
				}
				duelOffer.add(tItem);
			}
			for (InvItem item : duelOffer.getItems()) {
				if (duelOffer.countId(item.getID()) > player.getInventory()
						.countId(item.getID())) {
					player.setSuspiciousPlayer(true);
					return;
				}
				player.addToDuelOffer(item);
			}
			player.setRequiresOfferUpdate(true);
			break;

		case 225: // Set duel options
			affectedPlayer = player.getWishToDuel();
			if (affectedPlayer == null
					|| busy(affectedPlayer)
					|| !player.isDueling()
					|| !affectedPlayer.isDueling()
					|| (player.isDuelOfferAccepted() && affectedPlayer
							.isDuelOfferAccepted())
					|| player.isDuelConfirmAccepted()
					|| affectedPlayer.isDuelConfirmAccepted()) { // This
				// shouldn't
				// happen
				player.setSuspiciousPlayer(true);
				unsetOptions(player);
				unsetOptions(affectedPlayer);
				return;
			}

			player.setDuelOfferAccepted(false);
			player.setDuelConfirmAccepted(false);
			affectedPlayer.setDuelOfferAccepted(false);
			affectedPlayer.setDuelConfirmAccepted(false);

			player.getActionSender().sendDuelAcceptUpdate();
			affectedPlayer.getActionSender().sendDuelAcceptUpdate();

			for (int i = 0; i < 4; i++) {
				boolean b = p.readByte() == 1;
				player.setDuelSetting(i, b);
				affectedPlayer.setDuelSetting(i, b);
			}

			player.getActionSender().sendDuelSettingUpdate();
			affectedPlayer.getActionSender().sendDuelSettingUpdate();
			break;
		}
	}

	private void unsetOptions(Player p) {
		if (p == null) {
			return;
		}
		p.resetDueling();
	}

}
