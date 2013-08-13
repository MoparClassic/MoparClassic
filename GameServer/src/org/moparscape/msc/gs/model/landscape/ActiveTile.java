package org.moparscape.msc.gs.model.landscape;

import java.util.LinkedList;
import java.util.List;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.config.Formulae;
import org.moparscape.msc.gs.model.Entity;
import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.Item;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.tools.DataConversions;

public class ActiveTile {

	/**
	 * World instance
	 */
	private static World world = Instance.getWorld();
	/**
	 * A list of all items currently on this tile
	 */
	private List<Item> items = new LinkedList<Item>();
	/**
	 * A list of all npcs currently on this tile
	 */
	private List<Npc> npcs = new LinkedList<Npc>();
	/**
	 * The object currently on this tile (can only have 1 at a time)
	 */
	private GameObject object = null;
	/**
	 * A list of all players currently on this tile
	 */
	private List<Player> players = new LinkedList<Player>();
	/**
	 * The x and y coordinates of this tile
	 */
	private int x, y;

	/**
	 * Constructs a new tile at the given coordinates
	 */
	public ActiveTile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean remove = false;

	/**
	 * Add an entity to the tile
	 */
	public void add(Entity entity) {
		if (entity instanceof Player) {
			players.add((Player) entity);
		} else if (entity instanceof Npc) {
			npcs.add((Npc) entity);
		} else if (entity instanceof Item) {
			items.add((Item) entity);
		} else if (entity instanceof GameObject) {
			if (object != null) {
				remove = true;
				world.unregisterGameObject(object);
				remove = false;
			}
			object = (GameObject) entity;
		}
	}

	public GameObject getGameObject() {
		return object;
	}

	public List<Item> getItems() {
		return items;
	}

	public List<Npc> getNpcs() {
		return npcs;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean hasGameObject() {
		return object != null;
	}

	public boolean hasItem(Item item) {
		return items.contains(item);
	}

	public boolean hasItems() {
		return items != null && items.size() > 0;
	}

	public boolean hasNpcs() {
		return npcs != null && npcs.size() > 0;
	}

	public boolean hasPlayers() {
		return players != null && players.size() > 0;
	}

	public boolean specificArea() {
		boolean t = DataConversions.inPointArray(Formulae.noremoveTiles,
				new Point(this.getX(), this.getY()));
		return t;
	}

	/**
	 * Remove an entity from the tile
	 */
	public void remove(Entity entity) {
		if (entity instanceof Player) {
			players.remove(entity);
			if (!this.hasGameObject() && !this.hasItems() && !this.hasNpcs()
					&& !this.hasPlayers() && !this.specificArea()) {
				Instance.getWorld().tiles[this.getX()][this.getY()] = null;
			}
		} else if (entity instanceof Npc) {
			npcs.remove(entity);
			if (!this.hasGameObject() && !this.hasItems() && !this.hasNpcs()
					&& !this.hasPlayers() && !this.specificArea()) {
				Instance.getWorld().tiles[this.getX()][this.getY()] = null;
			}
		} else if (entity instanceof Item) {
			items.remove(entity);
			if (!this.hasGameObject() && !this.hasItems() && !this.hasNpcs()
					&& !this.hasPlayers() && !this.specificArea()) {
				Instance.getWorld().tiles[this.getX()][this.getY()] = null;
			}
		} else if (entity instanceof GameObject) {
			object = null;

			if (!this.hasGameObject() && !this.hasItems() && !this.hasNpcs()
					&& !this.hasPlayers() && !remove) {
				Instance.getWorld().tiles[this.getX()][this.getY()] = null;
			}
		}
	}

	public void cleanItself() {
		if (!this.hasGameObject() && !this.hasItems() && !this.hasNpcs()
				&& !this.hasPlayers() && !this.specificArea()) {
			Instance.getWorld().tiles[this.getX()][this.getY()] = null;
		}
	}
}
