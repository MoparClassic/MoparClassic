package org.moparscape.msc.gs.builders.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.builders.RSCPacketBuilder;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.config.Constants;
import org.moparscape.msc.gs.config.Formulae;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.container.Shop;
import org.moparscape.msc.gs.tools.Captcha;

public class MiscPacketBuilder {
	/**
	 * List of packets waiting to be sent to the user
	 */
	private List<RSCPacket> packets = new CopyOnWriteArrayList<RSCPacket>();
	/**
	 * The player we are creating packets for
	 */
	private Player player; // getArmourPoints

	/**
	 * Constructs a new MiscPacketBuilder
	 */
	public MiscPacketBuilder(Player player) {
		this.player = player;
	}

	/**
	 * Clears old packets that have already been sent
	 */
	public void clearPackets() {
		packets.clear();
	}

	/**
	 * Gets a List of new packets since the last update
	 */
	public List<RSCPacket> getPackets() {
		return packets;
	}

	/**
	 * Hides the bank windows
	 */
	public void hideBank() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(171);
		packets.add(s.toPacket());
	}

	/**
	 * Hides a question menu
	 */
	public void hideMenu() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(127);
		packets.add(s.toPacket());
	}

	// rade
	/**
	 * Hides the shop window
	 */
	public void hideShop() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(220);
		packets.add(s.toPacket());
	}

	/**
	 * Sends a message box
	 */
	public void sendAlert(String message, boolean big) {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(big ? 64 : 148);
		s.addBytes(message.getBytes());
		packets.add(s.toPacket());
	}

	public void sendAppearanceScreen() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(207);
		packets.add(s.toPacket());
	}

	/**
	 * Deny logging out
	 */
	public void sendCantLogout() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(136);
		packets.add(s.toPacket());
	}

	/**
	 * Sends the players combat style
	 */
	public void sendCombatStyle() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(129);
		s.addByte((byte) player.getCombatStyle());
		packets.add(s.toPacket());
	}

	/**
	 * Alert the client that they just died
	 */
	public void sendDied() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(165);
		packets.add(s.toPacket());
	}

	public void sendDuelAccept() {
		Player with = player.getWishToDuel();
		if (with == null) { // This shouldn't happen
			return;
		}
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(147);
		s.addLong(with.getUsernameHash());
		s.addByte((byte) with.getDuelOffer().size());
		for (InvItem item : with.getDuelOffer()) {
			s.addShort(item.id);
			s.addInt(item.amount);
		}
		s.addByte((byte) player.getDuelOffer().size());
		for (InvItem item : player.getDuelOffer()) {
			s.addShort(item.id);
			s.addInt(item.amount);
		}

		s.addByte((byte) (player.getDuelSetting(0) ? 1 : 0)); // duelCantRetreat
		// = data[i7++]
		// & 0xff;
		s.addByte((byte) (player.getDuelSetting(1) ? 1 : 0)); // duelUseMagic =
		// data[i7++] &
		// 0xff;
		s.addByte((byte) (player.getDuelSetting(2) ? 1 : 0)); // duelUsePrayer =
		// data[i7++] &
		// 0xff;
		s.addByte((byte) (player.getDuelSetting(3) ? 1 : 0)); // duelUseWeapons
		// = data[i7++]
		// & 0xff;

		packets.add(s.toPacket());
	}

	public void sendDuelAcceptUpdate() {
		Player with = player.getWishToDuel();
		if (with == null) { // This shouldn't happen
			return;
		}
		RSCPacketBuilder s1 = new RSCPacketBuilder();
		s1.setID(97);
		s1.addByte((byte) (player.isDuelOfferAccepted() ? 1 : 0));
		packets.add(s1.toPacket());

		RSCPacketBuilder s2 = new RSCPacketBuilder();
		s2.setID(65);
		s2.addByte((byte) (with.isDuelOfferAccepted() ? 1 : 0));
		packets.add(s2.toPacket());
	}

	public void sendDuelItems() {
		Player with = player.getWishToDuel();
		if (with == null) { // This shouldn't happen
			return;
		}
		ArrayList<InvItem> items = with.getDuelOffer();
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(63);
		s.addByte((byte) items.size());
		for (InvItem item : items) {
			s.addShort(item.id);
			s.addInt(item.amount);
		}
		packets.add(s.toPacket());
	}

	public void sendDuelSettingUpdate() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(198);
		s.addByte((byte) (player.getDuelSetting(0) ? 1 : 0));
		s.addByte((byte) (player.getDuelSetting(1) ? 1 : 0));
		s.addByte((byte) (player.getDuelSetting(2) ? 1 : 0));
		s.addByte((byte) (player.getDuelSetting(3) ? 1 : 0));
		packets.add(s.toPacket());
	}// sendSound

	public void sendDuelWindowClose() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(160);
		packets.add(s.toPacket());
	}

	public void sendDuelWindowOpen() {
		Player with = player.getWishToDuel();
		if (with == null) { // This shouldn't happen
			return;
		}
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(229);
		s.addShort(with.getIndex());
		packets.add(s.toPacket());
	}

	/**
	 * Enter Sleep
	 */
	public void sendEnterSleep() {
		player.setSleeping(true);
		byte[] image = Captcha.generateCaptcha(player);
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(206);
		s.addBytes(image, 0, image.length);
		packets.add(s.toPacket());
	}

	/**
	 * Updates the equipment status
	 */
	public void sendEquipmentStats() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(177);
		s.addShort(player.getArmourPoints());
		s.addShort(player.getWeaponAimPoints());
		s.addShort(player.getWeaponPowerPoints());
		s.addShort(player.getMagicPoints());
		s.addShort(player.getPrayerBonus());
		s.addShort(player.getRangePoints());
		packets.add(s.toPacket());
	}

	/**
	 * Updates the fatigue percentage
	 */
	public void sendFatigue() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(126);
		s.addShort(player.getFatigue() / 10);
		packets.add(s.toPacket());
	}

	/**
	 * Sends the whole friendlist
	 */
	public void sendFriendList() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(249);
		s.addByte((byte) player.getFriendList().size());
		for (Entry<Long, Integer> friend : player.getFriendList()) {
			int world = friend.getValue();
			s.addLong(friend.getKey());
			s.addByte((byte) (world == Config.WORLD_ID ? 99 : world));
		}
		packets.add(s.toPacket());
	}

	/**
	 * Updates a friends login status
	 */
	public void sendFriendUpdate(long usernameHash, int world) {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(25);
		s.addLong(usernameHash);
		s.addByte((byte) (world == Config.WORLD_ID ? 99 : world));
		packets.add(s.toPacket());
	}

	/**
	 * Updates game settings, ie sound effects etc
	 */
	public void sendGameSettings() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(152);
		s.addByte((byte) (player.getGameSetting(0) ? 1 : 0));
		s.addByte((byte) (player.getGameSetting(2) ? 1 : 0));
		s.addByte((byte) (player.getGameSetting(3) ? 1 : 0));
		s.addByte((byte) (player.getGameSetting(4) ? 1 : 0));
		s.addByte((byte) (player.getGameSetting(5) ? 1 : 0));
		s.addByte((byte) (player.getGameSetting(6) ? 1 : 0));
		packets.add(s.toPacket());
	}

	/**
	 * Sends the whole ignore list
	 */
	public void sendIgnoreList() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(2);
		s.addByte((byte) player.getIgnoreList().size());
		for (Long usernameHash : player.getIgnoreList()) {
			s.addLong(usernameHash.longValue());
		}
		packets.add(s.toPacket());
	}

	/**
	 * Incorrect sleep word!
	 */
	public void sendIncorrectSleepword() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(225);
		packets.add(s.toPacket());
	}

	public void sendInventory() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(114);
		s.addByte((byte) player.getInventory().size());
		List<InvItem> items = player.getInventory().getItems();
		for (InvItem item : items) {
			s.addShort(item.id + (item.wielded ? 32768 : 0));
			if (item.getDef().isStackable()) {
				s.addInt(item.amount);
			}
		}
		packets.add(s.toPacket());
	}

	/**
	 * Displays the login box and last IP and login date
	 */
	public void sendLoginBox() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(248);
		s.addShort(player.getDaysSinceLastLogin());
		s.addShort(player.getDaysSubscriptionLeft());
		s.addBytes(player.getLastIP().getBytes());
		packets.add(s.toPacket());
	}

	/**
	 * Confirm logout allowed
	 */
	public RSCPacket sendLogout() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(222);
		RSCPacket packet = s.toPacket();
		packets.add(packet);
		return packet;
	}

	/**
	 * Shows a question menu
	 */
	public void sendMenu(String[] options) {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(223);
		s.addByte((byte) options.length);
		for (String option : options) {
			s.addByte((byte) option.length());
			s.addBytes(option.getBytes());
		}
		packets.add(s.toPacket());
	}

	public void sendMessage(String message) {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(48);
		s.addBytes(message.getBytes());
		packets.add(s.toPacket());
	}

	public void sendOnlinePlayers() {
		this.sendMessage("@yel@Players Online: @whi@"
				+ (int) (Instance.getWorld().getPlayers().size() * 1.1)
				+ " @yel@Accepted Connections: @whi@"
				+ Constants.GameServer.ACCEPTED_CONNECTIONS);
	}

	/**
	 * Sends the prayer list of activated/deactivated prayers
	 */
	public void sendPrayers() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(209);
		for (int x = 0; x < 14; x++) {
			s.addByte((byte) (player.isPrayerActivated(x) ? 1 : 0));
		}
		packets.add(s.toPacket());
	}

	/**
	 * Updates privacy settings, ie pm block etc
	 */
	public void sendPrivacySettings() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(158);
		s.addByte((byte) (player.getPrivacySetting(0) ? 1 : 0));
		s.addByte((byte) (player.getPrivacySetting(1) ? 1 : 0));
		s.addByte((byte) (player.getPrivacySetting(2) ? 1 : 0));
		s.addByte((byte) (player.getPrivacySetting(3) ? 1 : 0));
		packets.add(s.toPacket());
	}

	/**
	 * Send a private message
	 */
	public void sendPrivateMessage(long usernameHash, byte[] message) {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(170);
		s.addLong(usernameHash);
		s.addBytes(message);
		packets.add(s.toPacket());
	}

	/**
	 * Sends base quest information
	 */
	public void sendQuestData() {
		/*
		 * RSCPacketBuilder s = new RSCPacketBuilder(); s.setID(233);
		 * s.addByte((byte)player.getQuestPoints()); int size =
		 * world.getQuestManager().getQuests().size(); s.addByte((byte)size);
		 * for (int i = 0; i < size; i++) { Quest quest =
		 * world.getQuestManager().getQuests().get(i);
		 * s.addByte((byte)quest.getUniqueID()); Integer objectInteger =
		 * player.getQuestStages().get(quest.getUniqueID()); if
		 * (objectInteger.intValue() == Quest.COMPLETE) s.addByte((byte) 2);
		 * else if (objectInteger.intValue() >= 1) s.addByte((byte) 1); else
		 * s.addByte((byte) 0); }
		 * 
		 * packets.add(s.toPacket());
		 */
	}

	// ATTACKER
	/**
	 * Sends this player's quest info
	 */
	public void sendQuestInfo() {
		/*
		 * RSCPacketBuilder s = new RSCPacketBuilder(); s.setID(233);
		 * s.addByte((byte) player.getQuestPoints()); // TODO: Send quest info
		 * int size = 17; s.addByte((byte) size); for (int i = 0; i < size; i++)
		 * { s.addByte((byte) i); s.addByte((byte) 0); }
		 */
		/*
		 * for (int i = 0; i < size; i++) { //Quest quest =
		 * World.getQuestManager().getQuests().get(i); s.addByte((byte)
		 * quest.getUniqueID()); Integer objectInteger =
		 * player.getQuestStages().get( quest.getUniqueID()); if
		 * (objectInteger.intValue() == Quest.COMPLETE) s.addByte((byte) 2);
		 * else if (objectInteger.intValue() >= 1) s.addByte((byte) 1); else
		 * s.addByte((byte) 0); }
		 */

		// packets.add(s.toPacket());
		/*
		 * RSCPacketBuilder s = new RSCPacketBuilder(); // setID(183)
		 * s.setID(233); s.addByte((byte)player.getQuestPoints()); HashMap qs =
		 * player.getQuestStages(); Set set = qs.entrySet(); Iterator i =
		 * set.iterator(); int idx = 0;
		 * 
		 * while (i.hasNext()) { Map.Entry qe = (Map.Entry) i.next(); Integer
		 * objectInteger = (Integer) qe.getValue(); s.addByte() if
		 * (objectInteger.intValue() == Quest.COMPLETE) s.addByte((byte) 2);
		 * else if (objectInteger.intValue() >= 1) s.addByte((byte) 1); else
		 * s.addByte((byte) 0); }
		 * 
		 * packets.add(s.toPacket());
		 */
	}

	public void sendRemoveItem(int slot) {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(191);
		s.addByte((byte) slot);
		packets.add(s.toPacket());
	}

	/**
	 * Tells the client to save a screenshot
	 */
	public void sendScreenshot() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(181);
		packets.add(s.toPacket());
	}

	public void sendServerInfo() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(110);
		s.addLong(Config.START_TIME);
		s.addBytes(Config.SERVER_LOCATION.getBytes());
		packets.add(s.toPacket());
	}

	/**
	 * Sends a sound effect
	 */
	public void sendSound(String soundName) {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(11);
		s.addBytes(soundName.getBytes());
		packets.add(s.toPacket());
	}

	/**
	 * Updates just one stat
	 */
	public void sendStat(int stat) {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(208);
		s.addByte((byte) stat);
		s.addByte((byte) player.getCurStat(stat));
		s.addByte((byte) player.getMaxStat(stat));
		s.addInt(player.getExp(stat));
		packets.add(s.toPacket());
	}

	/**
	 * Updates the users stats
	 */
	public void sendStats() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(180);
		for (int lvl : player.getCurStats())
			s.addByte((byte) lvl);
		for (int lvl : player.getMaxStats())
			s.addByte((byte) lvl);
		for (int exp : player.getExps())
			s.addInt(exp);
		packets.add(s.toPacket());
	}

	public void sendTeleBubble(int x, int y, boolean grab) {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(23);
		s.addByte((byte) (grab ? 1 : 0));
		s.addByte((byte) (x - player.getX()));
		s.addByte((byte) (y - player.getY()));
		packets.add(s.toPacket());
	}

	public void sendTradeAccept() {
		Player with = player.getWishToTrade();
		if (with == null) { // This shouldn't happen
			return;
		}
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(251);
		s.addLong(with.getUsernameHash());
		s.addByte((byte) with.getTradeOffer().size());
		for (InvItem item : with.getTradeOffer()) {
			s.addShort(item.id);
			s.addInt(item.amount);
		}
		s.addByte((byte) player.getTradeOffer().size());
		for (InvItem item : player.getTradeOffer()) {
			s.addShort(item.id);
			s.addInt(item.amount);
		}
		packets.add(s.toPacket());
	}

	public void sendTradeAcceptUpdate() {
		Player with = player.getWishToTrade();
		if (with == null) { // This shouldn't happen
			return;
		}
		RSCPacketBuilder s1 = new RSCPacketBuilder();
		s1.setID(18);
		s1.addByte((byte) (player.isTradeOfferAccepted() ? 1 : 0));
		packets.add(s1.toPacket());

		RSCPacketBuilder s2 = new RSCPacketBuilder();
		s2.setID(92);
		s2.addByte((byte) (with.isTradeOfferAccepted() ? 1 : 0));
		packets.add(s2.toPacket());
	}

	public void sendTradeItems() {
		Player with = player.getWishToTrade();
		if (with == null) { // This shouldn't happen
			return;
		}
		ArrayList<InvItem> items = with.getTradeOffer();
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(250);
		s.addByte((byte) items.size());
		for (InvItem item : items) {
			s.addShort(item.id);
			s.addInt(item.amount);
		}
		packets.add(s.toPacket());
	}

	public void sendTradeWindowClose() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(187);
		packets.add(s.toPacket());
	}

	public void sendTradeWindowOpen() {
		Player with = player.getWishToTrade();
		if (with == null) { // This shouldn't happen
			return;
		}
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(4);
		s.addShort(with.getIndex());
		packets.add(s.toPacket());
	}

	public void sendUpdateItem(int slot) {
		InvItem item = player.getInventory().getSlot(slot);
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(228);
		s.addByte((byte) slot);
		s.addShort(item.id + (item.wielded ? 32768 : 0));
		if (item.getDef().isStackable()) {
			s.addInt(item.amount);
		}
		packets.add(s.toPacket());
	}

	public void sendWakeUp(boolean heh) {
		if (heh) {
			player.setFatigue(0);
			player.getActionSender().sendFatigue();
			player.getActionSender().sendMessage(
					"You wake up - feeling refreshed");
		}
		player.setSleeping(false);
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(224);
		packets.add(s.toPacket());
	}

	/*
	 * public void sendWakeUp1() { RSCPacketBuilder s = new RSCPacketBuilder();
	 * s.setID(224); packets.add(s.toPacket()); }
	 * 
	 * public void sendWakeUp2() { player.isSleeping = false; RSCPacketBuilder s
	 * = new RSCPacketBuilder(); s.setID(224); packets.add(s.toPacket()); }
	 */

	/**
	 * Sent when the user changes coords incase they moved up/down a level
	 */
	public void sendWorldInfo() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(131);
		s.addShort(player.getIndex());
		s.addShort(2304);
		s.addShort(1776);
		s.addShort(Formulae.getHeight(player.getLocation()));
		s.addShort(944);
		packets.add(s.toPacket());
	}

	/**
	 * Show the bank window
	 */
	public void showBank() {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(93);
		s.addByte((byte) player.getBank().size());
		s.addByte((byte) player.getBank().maxSize());
		for (InvItem i : player.getBank().getItems()) {
			s.addShort(i.id);
			s.addInt(i.amount);
		}
		packets.add(s.toPacket());
	}

	/**
	 * Show the bank window
	 */
	public void showShop(Shop shop) {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(253);
		s.addByte((byte) shop.size());
		s.addByte((byte) (shop.general() ? 1 : 0));
		s.addByte((byte) shop.sellModifier());
		s.addByte((byte) shop.buyModifier());
		for (InvItem i : shop.getItems()) {
			s.addShort(i.id);
			s.addShort(i.amount);
			s.addInt(Formulae.getPrice(
					shop.getItems().get(Formulae.getItemPos(shop, i.id)), shop,
					true));
			s.addInt(Formulae.getPrice(
					shop.getItems().get(Formulae.getItemPos(shop, i.id)), shop,
					false));
		}
		packets.add(s.toPacket());
	}

	/**
	 * Sends a system update message
	 */
	public void startShutdown(int seconds) {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(172);
		s.addShort((int) (((double) seconds / 32D) * 50));
		packets.add(s.toPacket());
	}

	/**
	 * Updates the id and amount of an item in the bank
	 */
	public void updateBankItem(int slot, int newId, int amount) {
		RSCPacketBuilder s = new RSCPacketBuilder();
		s.setID(139);
		s.addByte((byte) slot);
		s.addShort(newId);
		s.addInt(amount);
		packets.add(s.toPacket());
	}

	public void addPacket(RSCPacket packet) {
		packets.add(packet);
	}
}
