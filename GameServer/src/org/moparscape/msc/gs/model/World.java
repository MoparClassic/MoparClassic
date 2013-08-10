package org.moparscape.msc.gs.model;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.moparscape.msc.gs.Server;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.core.ClientUpdater;
import org.moparscape.msc.gs.core.DelayedEventHandler;
import org.moparscape.msc.gs.event.DelayedEvent;
import org.moparscape.msc.gs.event.SingleEvent;
import org.moparscape.msc.gs.io.WorldLoader;
import org.moparscape.msc.gs.model.container.Shop;
import org.moparscape.msc.gs.model.definition.entity.GameObjectLoc;
import org.moparscape.msc.gs.model.definition.entity.NPCLoc;
import org.moparscape.msc.gs.model.landscape.ActiveTile;
import org.moparscape.msc.gs.model.landscape.MutableTileValue;
import org.moparscape.msc.gs.model.landscape.TileValue;
import org.moparscape.msc.gs.model.snapshot.Snapshot;
import org.moparscape.msc.gs.states.CombatState;
import org.moparscape.msc.gs.util.EntityList;
import org.moparscape.msc.gs.util.Logger;

public final class World {

	/**
	 * Double ended queue to store snapshots into
	 */
	private Deque<Snapshot> snapshots = new LinkedList<Snapshot>();

	/**
	 * Returns double-ended queue for snapshots.
	 */
	public synchronized Deque<Snapshot> getSnapshots() {
		return snapshots;
	}

	/**
	 * Add entry to snapshots
	 */
	public synchronized void addEntryToSnapshots(Snapshot snapshot) {
		snapshots.offerFirst(snapshot);
	}

	public void sendWorldMessage(String msg) {
		for (Player p : getPlayers()) {
			p.getActionSender().sendMessage(msg);
		}
	}

	public void sendWorldAnnouncement(String msg) {
		for (Player p : getPlayers()) {
			p.getActionSender().sendMessage("%" + msg);
		}
	}

	public void sendMessage(Player p, String msg) {
		p.getActionSender().sendMessage(msg);
	}

	/* Places */
	protected Point[][] places = { { new Point(252, 349), new Point(260, 356) } };

	/* Can attack in these places? */
	public boolean[] wildAttackable = { false }; // 0 = No

	public final Point[][] getPlaces() {
		return places;
	}

	public final boolean wildAttackable(int i) {
		return wildAttackable[i];
	}

	/* End of Places */

	/**
	 * The maximum height of the map (944 squares per level)
	 */
	public static final int MAX_HEIGHT = 3776;
	/**
	 * The maximum width of the map
	 */
	public static final int MAX_WIDTH = 944;
	/**
	 * World instance
	 */
	public static boolean SERVER_MUTED = false;
	private static World worldInstance;

	public static boolean isMembers() {
		return Config.members;
	}

	public WorldLoader wl;

	/**
	 * Database connection
	 */

	/**
	 * returns the only instance of this world, if there is not already one,
	 * makes it and loads everything
	 */
	public static synchronized World getWorld() {
		if (worldInstance == null) {
			worldInstance = new World();
			try {
				worldInstance.wl = new WorldLoader();
				List<Point3D> sections = worldInstance.wl
						.loadWorld(worldInstance);
				Logger.println("Loading world objects.");
				worldInstance.wl.loadObjects(sections);
			} catch (Exception e) {
				Logger.error(e);
				System.exit(0);
			}
		}
		return worldInstance;
	}

	/**
	 * The client updater instance
	 */
	private ClientUpdater clientUpdater;
	/**
	 * The delayedeventhandler instance
	 */
	private DelayedEventHandler delayedEventHandler;
	public int eventlev = 0;
	/**
	 * Event vars
	 */
	public int eventx = 0;
	public int eventy = 0;

	public String lastAnswer = null;
	/**
	 * A list of all npcs on the server
	 */
	private EntityList<Npc> npcs = new EntityList<Npc>(4000);
	/**
	 * A list of all players on the server
	 */
	private EntityList<Player> players = new EntityList<Player>(2000);

	/**
	 * The server instance
	 */
	private Server server;
	/**
	 * A list of all shops on the server
	 */
	private List<Shop> shops = new ArrayList<Shop>();
	/**
	 * The tiles the map is made up of
	 */
	public ActiveTile[][] tiles = new ActiveTile[MAX_WIDTH][MAX_HEIGHT];
	/**
	 * Data about the tiles, are they walkable etc
	 */
	private TileValue[][] tileType = new TileValue[MAX_WIDTH][MAX_HEIGHT];

	/**
	 * Counts how many npcs are currently here
	 */
	public int countNpcs() {
		return npcs.size();
	}

	/**
	 * Counts how many players are currently connected
	 */
	public int countPlayers() {
		return players.size();
	}

	/**
	 * Adds a DelayedEvent that will remove a GameObject
	 */
	public void delayedRemoveObject(final GameObject object, final int delay) {
		delayedEventHandler.add(new SingleEvent(null, delay) {

			public void action() {
				ActiveTile tile = getTile(object.getLocation());
				if (tile.hasGameObject() && tile.getGameObject().equals(object)) {
					unregisterGameObject(object);
				}
			}
		});
	}

	/**
	 * Adds a DelayedEvent that will spawn a GameObject
	 */
	public void delayedSpawnObject(final GameObjectLoc loc,
			final int respawnTime) {
		delayedEventHandler.add(new SingleEvent(null, respawnTime) {

			public void action() {
				registerGameObject(new GameObject(loc));
			}
		});
	}

	public int eventlev() {
		return eventlev;
	}

	public int eventx() {
		return eventx;
	}

	public int eventy() {
		return eventy;
	}

	/**
	 * Gets the ClientUpdater instance
	 */
	public ClientUpdater getClientUpdater() {
		return clientUpdater;
	}

	/**
	 * Gets the DelayedEventHandler instance
	 */
	public DelayedEventHandler getDelayedEventHandler() {
		return delayedEventHandler;
	}

	/**
	 * Gets an Npc by their server index
	 */
	public Npc getNpc(int idx) {
		return npcs.get(idx);
	}

	/**
	 * Gets an npc by their coords and id]
	 */
	public Npc getNpc(int id, int minX, int maxX, int minY, int maxY) {
		for (Npc npc : npcs) {
			if (npc.getID() == id && npc.getX() >= minX && npc.getX() <= maxX
					&& npc.getY() >= minY && npc.getY() <= maxY) {
				return npc;
			}
		}
		return null;
	}

	/**
	 * Gets an npc by their coords and id]
	 */
	public Npc getNpc(int id, int minX, int maxX, int minY, int maxY,
			boolean notNull) {
		for (Npc npc : npcs) {
			if (npc.getID() == id && npc.getX() >= minX && npc.getX() <= maxX
					&& npc.getY() >= minY && npc.getY() <= maxY) {
				if (!npc.inCombat()) {
					return npc;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the list of npcs on the server
	 */
	public EntityList<Npc> getNpcs() {
		return npcs;
	}

	/**
	 * Gets a Player by their server index
	 */
	public Player getPlayer(int idx) {
		return players.get(idx);
	}

	/**
	 * Gets a player by their username hash
	 */
	public Player getPlayer(long usernameHash) {
		for (Player p : players) {
			if (p.getUsernameHash() == usernameHash) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Gets the list of players on the server
	 */
	public EntityList<Player> getPlayers() {
		return players;
	}

	/**
	 * Gets the server instance
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * Gets a list of all shops
	 */
	public Shop getShop(Point location) {
		for (Shop shop : shops) {
			if (shop.withinShop(location)) {
				if (!shop.inited()) {
					shop.init();
				}
				return shop;
			}
		}
		return null;
	}

	public List<Shop> getShops() {
		return shops;
	}

	/**
	 * Gets the active tile at point x, y
	 */
	public ActiveTile getTile(int x, int y) {
		if (!withinWorld(x, y)) {
			return null;
		}
		ActiveTile t = tiles[x][y];
		if (t == null) {
			t = new ActiveTile(x, y);
			tiles[x][y] = t;
		}
		return t;
	}

	/**
	 * Gets the tile at a point
	 */
	public ActiveTile getTile(Point p) {
		return getTile(p.getX(), p.getY());
	}

	/**
	 * Gets the tile value as point x, y
	 */
	public TileValue getTileValue(int x, int y) {
		if (!withinWorld(x, y)) {
			return null;
		}
		TileValue t = tileType[x][y];
		if (t == null) {
			t = TileValue.create(0, new byte[6]);
			tileType[x][y] = t;
		}
		return t;
	}

	/**
	 * Checks if the given npc is on the server
	 */
	public boolean hasNpc(Npc n) {
		return npcs.contains(n);
	}

	/**
	 * Checks if the given player is on the server
	 */
	public boolean hasPlayer(Player p) {
		return players.contains(p);
	}

	/**
	 * Checks if the given player is logged in
	 */
	public boolean isLoggedIn(long usernameHash) {
		Player friend = getPlayer(usernameHash);
		if (friend != null) {
			return friend.loggedIn();
		}
		return false;
	}

	/**
	 * Updates the map to include a new door
	 */
	public void registerDoor(GameObject o) {
		if (o.getDoorDef().getDoorType() != 1) {
			return;
		}
		int dir = o.getDirection();
		int x = o.getX(), y = o.getY();
		MutableTileValue t = new MutableTileValue(getTileValue(x, y));
		if (dir == 0) {
			t.objectValue |= 1;
			MutableTileValue t1 = new MutableTileValue(getTileValue(x, y - 1));
			t1.objectValue |= 4;
			setTileValue(x, y - 1, t1.toTileValue());
		} else if (dir == 1) {
			t.objectValue |= 2;
			MutableTileValue t1 = new MutableTileValue(getTileValue(x - 1, y));
			t1.objectValue |= 8;
			setTileValue(x - 1, y, t1.toTileValue());
		} else if (dir == 2) {
			t.objectValue |= 0x10;
		} else if (dir == 3) {
			t.objectValue |= 0x20;
		}
		setTileValue(x, y, t.toTileValue());
	}

	/**
	 * Registers an object with the world
	 */
	public void registerGameObject(GameObject o) {
		switch (o.getType()) {
		case 0:
			registerObject(o);
			break;
		case 1:
			registerDoor(o);
			break;
		}
	}

	/**
	 * Registers an item to be removed after 3 minutes
	 */
	public void registerItem(final Item i) {
		try {
			if (i.getLoc() == null) {
				delayedEventHandler.add(new DelayedEvent(null, 180000) {

					public void run() {
						ActiveTile tile = getTile(i.getLocation());
						if (tile.hasItem(i)) {
							unregisterItem(i);
						}
						matchRunning = false;
					}
				});
			}
		} catch (Exception e) {
			i.remove();
			e.printStackTrace();
		}
	}

	/**
	 * Registers an npc with the world
	 */
	public void registerNpc(Npc n) {
		NPCLoc npc = n.getLoc();
		if (npc.startX < npc.minX || npc.startX > npc.maxX
				|| npc.startY < npc.minY || npc.startY > npc.maxY
				|| (getTileValue(npc.startX, npc.startY).mapValue & 64) != 0) {
			Logger.println("Fucked Npc: <id>" + npc.id + "</id><startX>"
					+ npc.startX + "</startX><startY>" + npc.startY
					+ "</startY>");
		}
		npcs.add(n);
	}

	/**
	 * Updates the map to include a new object
	 */
	public void registerObject(GameObject o) {
		if (o.getGameObjectDef().getType() != 1
				&& o.getGameObjectDef().getType() != 2) {
			return;
		}
		int dir = o.getDirection();
		int width, height;
		if (dir == 0 || dir == 4) {
			width = o.getGameObjectDef().getWidth();
			height = o.getGameObjectDef().getHeight();
		} else {
			height = o.getGameObjectDef().getWidth();
			width = o.getGameObjectDef().getHeight();
		}
		for (int x = o.getX(); x < o.getX() + width; x++) {
			for (int y = o.getY(); y < o.getY() + height; y++) {
				MutableTileValue t = new MutableTileValue(getTileValue(x, y));
				if (o.getGameObjectDef().getType() == 1) {
					t.objectValue |= 0x40;
				} else if (dir == 0) {
					t.objectValue |= 2;
					MutableTileValue t1 = new MutableTileValue(getTileValue(
							x - 1, y));
					t1.objectValue |= 8;
					setTileValue(x - 1, y, t1.toTileValue());
				} else if (dir == 2) {
					t.objectValue |= 4;
					MutableTileValue t1 = new MutableTileValue(getTileValue(x,
							y + 1));
					t1.objectValue |= 1;
					setTileValue(x, y + 1, t1.toTileValue());
				} else if (dir == 4) {
					t.objectValue |= 8;
					MutableTileValue t1 = new MutableTileValue(getTileValue(
							x + 1, y));
					t1.objectValue |= 2;
					setTileValue(x + 1, y, t1.toTileValue());
				} else if (dir == 6) {
					t.objectValue |= 1;
					MutableTileValue t1 = new MutableTileValue(getTileValue(x,
							y - 1));
					t1.objectValue |= 4;
					setTileValue(x, y - 1, t1.toTileValue());
				}
				setTileValue(x, y, t.toTileValue());
			}
		}

	}

	/**
	 * Registers a player with the world and informs other players on their
	 * login
	 */
	public void registerPlayer(Player p) {
		if (players.contains(p)) {
			Logger.println("IMPORTANT. Players array already contains player:  "
					+ p.getUsername() + ". I don't think this should happen ;c");
		}
		p.setInitialized();
		players.add(p);
	}

	/**
	 * Inserts a new shop into the world
	 */
	public void registerShop(final Shop shop) {
		shops.add(shop);
	}

	public void sendBroadcastMessage(Player p, String user, String message) {
		p.getActionSender().sendMessage("%#adm#" + user + ": @gre@" + message);

	}

	public void sendBroadcastMessage(String user, String message) {
		for (Player p : getPlayers()) {
			p.getActionSender().sendMessage(
					"%#adm#" + user + ": @gre@" + message);
		}
	}

	public void sendBroadcastMessage(String user, String message,
			boolean modonly) {
		for (Player p : getPlayers()) {
			if (p.isPMod()) {
				p.getActionSender().sendMessage(
						"%#adm#" + user + ": @gre@" + message);
			}
		}
	}

	/**
	 * Sets the ClientUpdater instance
	 */
	public void setClientUpdater(ClientUpdater clientUpdater) {
		this.clientUpdater = clientUpdater;
	}

	/**
	 * Sets the DelayedEventHandler instance
	 */
	public void setDelayedEventHandler(DelayedEventHandler delayedEventHandler) {
		this.delayedEventHandler = delayedEventHandler;
	}

	public void seteventlev(int lev) {
		eventlev = lev;
	}

	public void seteventx(int x) {
		eventx = x;
	}

	public void seteventy(int y) {
		eventy = y;
	}

	/**
	 * adds or removes the given entity from the relivant tiles
	 */
	public void setLocation(Entity entity, Point oldPoint, Point newPoint) {
		ActiveTile t;
		if (oldPoint != null) {
			t = getTile(oldPoint);
			t.remove(entity);
		}
		if (newPoint != null) {
			t = getTile(newPoint);
			t.add(entity);
		}
	}

	/**
	 * Sets the instance of the server
	 */
	public void setServer(Server server) {
		this.server = server;
	}

	/**
	 * Removes a door from the map
	 */
	public void unregisterDoor(GameObject o) {
		if (o.getDoorDef().getDoorType() != 1) {
			return;
		}
		int dir = o.getDirection();
		int x = o.getX(), y = o.getY();
		MutableTileValue t = new MutableTileValue(getTileValue(x, y));

		if (dir == 0) {
			t.objectValue &= 0xfffe;
			MutableTileValue t1 = new MutableTileValue(getTileValue(x, y - 1));
			t1.objectValue &= 65535 - 4;
			setTileValue(x, y - 1, t1.toTileValue());
		} else if (dir == 1) {
			t.objectValue &= 0xfffd;
			MutableTileValue t1 = new MutableTileValue(getTileValue(x - 1, y));
			t1.objectValue &= 65535 - 8;
			setTileValue(x - 1, y, t1.toTileValue());
		} else if (dir == 2) {
			t.objectValue &= 0xffef;
		} else if (dir == 3) {
			t.objectValue &= 0xffdf;
		}
		setTileValue(x, y, t.toTileValue());
	}

	/**
	 * Removes an object from the server
	 */
	public void unregisterGameObject(GameObject o) {
		o.remove();
		setLocation(o, o.getLocation(), null);
		switch (o.getType()) {
		case 0:
			unregisterObject(o);
			break;
		case 1:
			unregisterDoor(o);
			break;
		}
	}

	/**
	 * Removes an item from the server
	 */
	public void unregisterItem(Item i) {
		i.remove();
		setLocation(i, i.getLocation(), null);
	}

	/**
	 * Removes an npc from the server
	 */
	public void unregisterNpc(Npc n) {
		if (hasNpc(n)) {
			npcs.remove(n);
		}
		setLocation(n, n.getLocation(), null);
	}

	/**
	 * Removes an object from the map
	 */
	public void unregisterObject(GameObject o) {
		if (o.getGameObjectDef().getType() != 1
				&& o.getGameObjectDef().getType() != 2) {
			return;
		}
		int dir = o.getDirection();
		int width, height;
		if (dir == 0 || dir == 4) {
			width = o.getGameObjectDef().getWidth();
			height = o.getGameObjectDef().getHeight();
		} else {
			height = o.getGameObjectDef().getWidth();
			width = o.getGameObjectDef().getHeight();
		}
		for (int x = o.getX(); x < o.getX() + width; x++) {
			for (int y = o.getY(); y < o.getY() + height; y++) {
				MutableTileValue t = new MutableTileValue(getTileValue(x, y));

				if (o.getGameObjectDef().getType() == 1) {
					t.objectValue &= 0xffbf;
				} else if (dir == 0) {
					t.objectValue &= 0xfffd;
					MutableTileValue t1 = new MutableTileValue(getTileValue(
							x - 1, y));
					t1.objectValue &= 65535 - 8;
					setTileValue(x - 1, y, t1.toTileValue());
				} else if (dir == 2) {
					t.objectValue &= 0xfffb;
					MutableTileValue t1 = new MutableTileValue(getTileValue(x,
							y + 1));
					t1.objectValue &= 65535 - 1;
					setTileValue(x, y + 1, t1.toTileValue());
				} else if (dir == 4) {
					t.objectValue &= 0xfff7;
					MutableTileValue t1 = new MutableTileValue(getTileValue(
							x + 1, y));
					t1.objectValue &= 65535 - 2;
					setTileValue(x + 1, y, t1.toTileValue());
				} else if (dir == 6) {
					t.objectValue &= 0xfffe;
					MutableTileValue t1 = new MutableTileValue(getTileValue(x,
							y - 1));
					t1.objectValue &= 65535 - 4;
					setTileValue(x, y - 1, t1.toTileValue());
				}
				setTileValue(x, y, t.toTileValue());
			}
		}
	}

	/**
	 * Removes a player from the server and saves their account
	 */
	public void unregisterPlayer(Player p) {
		p.setLoggedIn(false);
		p.resetAll();
		p.save();
		Mob opponent = p.getOpponent();
		if (opponent != null) {
			p.resetCombat(CombatState.ERROR);
			opponent.resetCombat(CombatState.ERROR);
		}
		server.getLoginConnector().getActionSender()
				.playerLogout(p.getUsernameHash());
		delayedEventHandler.removePlayersEvents(p);
		players.remove(p);
		setLocation(p, p.getLocation(), null);
	}

	/**
	 * Are the given coords within the world boundaries
	 */
	public boolean withinWorld(int x, int y) {
		return x >= 0 && x < MAX_WIDTH && y >= 0 && y < MAX_HEIGHT;
	}

	public void setTileValue(int x, int y, TileValue tileValue) {
		tileType[x][y] = tileValue;
	}
}
