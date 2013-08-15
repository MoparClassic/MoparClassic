package org.moparscape.msc.gs.core;

import java.util.ArrayList;
import java.util.List;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.builders.client.GameObjectPositionPacketBuilder;
import org.moparscape.msc.gs.builders.client.ItemPositionPacketBuilder;
import org.moparscape.msc.gs.builders.client.NpcPositionPacketBuilder;
import org.moparscape.msc.gs.builders.client.NpcUpdatePacketBuilder;
import org.moparscape.msc.gs.builders.client.PlayerPositionPacketBuilder;
import org.moparscape.msc.gs.builders.client.PlayerUpdatePacketBuilder;
import org.moparscape.msc.gs.builders.client.WallObjectPositionPacketBuilder;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Chatlog;
import org.moparscape.msc.gs.tools.DataConversions;
import org.moparscape.msc.gs.util.EntityList;
import org.moparscape.msc.gs.util.Logger;

public final class ClientUpdater {

	public static int pktcount = 0;
	private static World world = Instance.getWorld();
	private GameObjectPositionPacketBuilder gameObjectPositionBuilder = new GameObjectPositionPacketBuilder();
	private ItemPositionPacketBuilder itemPositionBuilder = new ItemPositionPacketBuilder();
	private NpcUpdatePacketBuilder npcApperanceBuilder = new NpcUpdatePacketBuilder();
	private NpcPositionPacketBuilder npcPositionPacketBuilder = new NpcPositionPacketBuilder();
	private EntityList<Npc> npcs = world.getNpcs();
	private PlayerUpdatePacketBuilder playerApperanceBuilder = new PlayerUpdatePacketBuilder();
	private PlayerPositionPacketBuilder playerPositionBuilder = new PlayerPositionPacketBuilder();
	private WallObjectPositionPacketBuilder wallObjectPositionPacketBuilder = new WallObjectPositionPacketBuilder();

	private EntityList<Player> players = world.getPlayers();

	public ClientUpdater() {
		world.setClientUpdater(this);
	}

	/**
	 * Sends queued packets to each player
	 */
	public void sendQueuedPackets() {
		try {
			for (Player p : players) {
				List<RSCPacket> packets = p.getActionSender().getPackets();
				for (RSCPacket packet : packets) {
					p.getSession().write(packet);
				}
				p.getActionSender().clearPackets();
				if (p.destroyed()) {
					p.getSession().close();
					p.remove();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update player/npc appearances, game objects, items, wall objects, ping
	 */
	public void doMinor() {
		for (Player p : players) {
			p.updateAppearanceID();
		}
		for (Player p : players) {
			if (p.isFirstMajorUpdateSent()) {
				updatePlayerApperances(p);
			}
			updateNpcApperances(p);
		}

		for (Player p : players) {
			if (p.isFirstMajorUpdateSent()) {
				p.setAppearnceChanged(false);
				p.clearProjectilesNeedingDisplayed();
				p.clearPlayersNeedingHitsUpdate();
				p.clearNpcsNeedingHitsUpdate();
				p.clearChatMessagesNeedingDisplayed();
				p.clearNpcMessagesNeedingDisplayed();
				p.clearBubblesNeedingDisplayed();
			}
		}

		for (Npc n : npcs) {
			n.setAppearnceChanged(false);

		}

	}

	public static boolean threaded = false;
	public volatile boolean updatingCollections;

	public void doMajor() {
		Long delay;
		Long now = System.currentTimeMillis();

		updateNpcPositions();

		delay = System.currentTimeMillis() - now;
		if (delay > 299)
			Logger.println("updateNpcPositions() is taking longer than it should, exactly "
					+ delay + "ms");

		now = System.currentTimeMillis();

		updatePlayersPositions();

		delay = System.currentTimeMillis() - now;
		if (delay > 299)
			Logger.println("updatePlayersPositions() is taking longer than it should, exactly "
					+ delay + "ms");

		now = System.currentTimeMillis();

		updateMessageQueues();

		delay = System.currentTimeMillis() - now;
		if (delay > 299)
			Logger.println("updateMessageQueues() is taking longer than it should, exactly "
					+ delay + "ms");

		now = System.currentTimeMillis();

		updateOffers();

		for (Player p : players) {
			// Logging.debug("Process for player " + p.getUsername() +
			// " | threaded: " + threaded);

			updateTimeouts(p);

			updatePlayerPositions(p);
			updateNpcPositions(p);
			updateGameObjects(p);
			updateWallObjects(p);
			updateItems(p);

			p.setFirstMajorUpdateSent(true);

		}
		updateCollections();
	}

	public void process(Player p) {

		// Logging.debug("Process for player " + p.getUsername() +
		// " | threaded: " + threaded);

		updateTimeouts(p);

		updatePlayerPositions(p); // Must be done before updating any
									// objects/items/npcs!
		updateNpcPositions(p);
		updateGameObjects(p);
		updateWallObjects(p);
		updateItems(p);

		p.setFirstMajorUpdateSent(true);
	}

	/**
	 * Updates collections, new becomes known, removing is removed etc.
	 */
	public void updateCollections() {
		updatingCollections = true;
		for (Player p : players) {
			if (p.isRemoved() && p.initialized()) {
				world.unregisterPlayer(p);
			}
		}
		for (Player p : players) {
			p.getWatchedPlayers().update();
			p.getWatchedObjects().update();
			p.getWatchedItems().update();
			p.getWatchedNpcs().update();

			// p.clearProjectilesNeedingDisplayed();
			// p.clearPlayersNeedingHitsUpdate();
			// p.clearNpcsNeedingHitsUpdate();
			// p.clearChatMessagesNeedingDisplayed();
			// p.clearNpcMessagesNeedingDisplayed();
			// p.clearBubblesNeedingDisplayed();

			p.resetSpriteChanged();
			// p.setAppearnceChanged(false);
		}
		for (Npc n : npcs) {
			n.resetSpriteChanged();
			// n.setAppearnceChanged(false);
		}
		updatingCollections = false;
	}

	/**
	 * Sends updates for game objects to the given player
	 */
	private void updateGameObjects(Player p) {
		gameObjectPositionBuilder.setPlayer(p);
		RSCPacket temp = gameObjectPositionBuilder.getPacket();
		if (temp != null) {
			p.getActionSender().addPacket(temp);
			// p.getSession().write(temp);
		}
	}

	/**
	 * Sends updates for game items to the given player
	 */
	private void updateItems(Player p) {
		itemPositionBuilder.setPlayer(p);
		RSCPacket temp = itemPositionBuilder.getPacket();
		if (temp != null) {
			p.getActionSender().addPacket(temp);
			// p.getSession().write(temp);
		}
	}

	/**
	 * Updates the messages queues for each player
	 */
	private void updateMessageQueues() {
		for (Player sender : players) {
			ChatMessage message = sender.getNextChatMessage();
			if (message == null || !sender.loggedIn()) {
				continue;
			}
			String s = DataConversions.byteToString(message.getMessage(), 0,
					message.getMessage().length);
			s = s.toLowerCase();
			String k = s;
			s = s.replace(" ", "");
			s = s.replace(".", "");
			if (s.contains("#adm#") || s.contains("#mod#")
					|| s.contains("#pmd#")) {
				sender.getActionSender()
						.sendMessage(
								"@red@Your last message was not sent out due to an illegal string");
				return;
			}
			if (sender.isMuted()) {
				sender.getActionSender().sendMessage(
						"You are muted, you cannot send messages");
				return;
			}
			List<Player> recievers = sender.getViewArea().getPlayersInView();
			ArrayList<String> recieverUsernames = new ArrayList<String>();
			for (Player p : recievers)
				recieverUsernames.add(p.getUsername());

			world.addEntryToSnapshots(new Chatlog(sender.getUsername(), k,
					recieverUsernames));
			for (Player recipient : recievers) {
				if (sender.getIndex() == recipient.getIndex()
						|| !recipient.loggedIn()) {
					continue;
				}
				if (recipient.getPrivacySetting(0)
						&& !recipient.isFriendsWith(sender.getUsernameHash())
						&& !sender.isPMod()) {
					continue;
				}
				if (recipient.isIgnoring(sender.getUsernameHash())
						&& !sender.isPMod()) {
					continue;
				}
				recipient.informOfChatMessage(message);
			}
			recievers = null;
		}
	}

	/**
	 * Update appearance of any npcs the given player should be aware of
	 */
	private void updateNpcApperances(Player p) {
		npcApperanceBuilder.setPlayer(p);
		RSCPacket temp = npcApperanceBuilder.getPacket();
		if (temp != null) {
			p.getActionSender().addPacket(temp);
			// p.getSession().write(temp);
		}

	}

	/**
	 * Update the position of npcs, and check if who (and what) they are aware
	 * of needs updated
	 */
	private void updateNpcPositions() {
		for (Npc n : npcs) {
			n.resetMoved();
			n.updatePosition();
			n.updateAppearanceID();
		}
	}

	/**
	 * Sends updates for npcs to the given player
	 */
	private void updateNpcPositions(Player p) {
		npcPositionPacketBuilder.setPlayer(p);
		RSCPacket temp = npcPositionPacketBuilder.getPacket();
		if (temp != null) {
			p.getActionSender().addPacket(temp);
			// p.getSession().write(temp);
		}
	}

	public void updateOffers() {
		for (Player player : players) {
			if (!player.requiresOfferUpdate()) {
				continue;
			}
			player.setRequiresOfferUpdate(false);
			if (player.isTrading()) {
				Player affectedPlayer = player.getWishToTrade();
				if (affectedPlayer == null) {
					continue;
				}
				affectedPlayer.getActionSender().sendTradeItems();
			} else if (player.isDueling()) {
				Player affectedPlayer = player.getWishToDuel();
				if (affectedPlayer == null) {
					continue;
				}
				player.getActionSender().sendDuelSettingUpdate();
				affectedPlayer.getActionSender().sendDuelSettingUpdate();
				affectedPlayer.getActionSender().sendDuelItems();
			}
		}
	}

	/**
	 * Update appearance of the given player, and any players they should be
	 * aware of
	 */
	private void updatePlayerApperances(Player p) {
		playerApperanceBuilder.setPlayer(p);
		RSCPacket temp = playerApperanceBuilder.getPacket();
		if (temp != null) {
			p.getActionSender().addPacket(temp);
			// p.getSession().write(temp);
		}
	}

	/**
	 * Update positions of the given player, and any players they should be
	 * aware of
	 */
	private void updatePlayerPositions(Player p) {
		playerPositionBuilder.setPlayer(p);
		RSCPacket temp = playerPositionBuilder.getPacket();
		if (temp != null) {
			p.getActionSender().addPacket(temp);
			// p.getSession().write(temp);
		}
	}

	/**
	 * Update the position of players, and check if who (and what) they are
	 * aware of needs updated
	 */
	private void updatePlayersPositions() {
		for (Player p : players) {
			p.resetMoved();
			p.updatePosition();
			p.updateAppearanceID();
		}
		for (Player p : players) {
			p.revalidateWatchedPlayers();
			p.revalidateWatchedObjects();
			p.revalidateWatchedItems();
			p.revalidateWatchedNpcs();
			p.updateViewedPlayers();
			p.updateViewedObjects();
			p.updateViewedItems();
			p.updateViewedNpcs();
		}
	}

	/**
	 * Checks the player has moved within the last 5mins
	 */
	private void updateTimeouts(Player p) {
		if (p.destroyed()) {
			return;
		}
		long curTime = GameEngine.getTime();
		if (curTime - p.getLastPing() >= 30000) {
			p.destroy(false);
		} else if (p.warnedToMove()) {
			if (curTime - p.getLastMoved() >= ((Config.AFK_TIMEOUT + 1) * 60000)
					&& p.loggedIn()) {
				p.destroy(false);
			}
		} else if (curTime - p.getLastMoved() >= (Config.AFK_TIMEOUT * 60000)
				&& !p.warnedToMove()) {
			p.getActionSender()
					.sendMessage(
							"@cya@You have not moved for "
									+ Config.AFK_TIMEOUT
									+ " mins, please move to a new area to avoid logout.");
			p.warnToMove();
		}
	}

	/**
	 * Sends updates for wall objects to the given player
	 */
	private void updateWallObjects(Player p) {
		wallObjectPositionPacketBuilder.setPlayer(p);
		RSCPacket temp = wallObjectPositionPacketBuilder.getPacket();
		if (temp != null) {
			// p.getSession().write(temp);
			p.getActionSender().addPacket(temp);
		}
	}
}
