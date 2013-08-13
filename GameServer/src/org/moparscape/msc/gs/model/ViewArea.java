package org.moparscape.msc.gs.model;

import java.util.ArrayList;
import java.util.List;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.model.landscape.ActiveTile;

public class ViewArea {
	private static World world = Instance.getWorld();
	private Mob mob;

	public ViewArea(Mob mob) {
		this.mob = mob;
	}

	public List<GameObject> getGameObjectsInView() {
		List<GameObject> objects = new ArrayList<GameObject>();
		ActiveTile[][] viewArea = getViewedArea(21, 21, 21, 21);
		for (int x = 0; x < viewArea.length; x++) {
			for (int y = 0; y < viewArea[x].length; y++) {
				ActiveTile t = viewArea[x][y];
				if (t != null) {
					if (t.hasGameObject()) {
						objects.add(t.getGameObject());
					}
				}
			}
		}
		return objects;
	}

	public List<Item> getItemsInView() {
		List<Item> items = new ArrayList<Item>();
		ActiveTile[][] viewArea = getViewedArea(21, 21, 21, 21);
		for (int x = 0; x < viewArea.length; x++) {
			for (int y = 0; y < viewArea[x].length; y++) {
				ActiveTile t = viewArea[x][y];
				if (t != null) {
					items.addAll(t.getItems());
				}
			}
		}
		return items;
	}

	public List<Npc> getNpcsInView() {
		List<Npc> npcs = new ArrayList<Npc>();
		ActiveTile[][] viewArea = getViewedArea(15, 15, 16, 16);
		for (int x = 0; x < viewArea.length; x++)
			for (int y = 0; y < viewArea[x].length; y++) {
				ActiveTile t = viewArea[x][y];
				if (t != null) {
					List<Npc> temp = t.getNpcs();
					if (temp != null) {
						npcs.addAll(temp);
					}
				}
			}
		return npcs;
	}

	public List<Player> getPlayersInView() {
		List<Player> players = new ArrayList<Player>();
		ActiveTile[][] viewArea = getViewedArea(15, 15, 16, 16);
		for (int x = 0; x < viewArea.length; x++)
			for (int y = 0; y < viewArea[x].length; y++) {
				ActiveTile t = viewArea[x][y];
				if (t != null) {
					List<Player> temp = t.getPlayers();
					if (temp != null) {
						players.addAll(temp);
					}
				}
			}
		return players;
	}

	public ActiveTile[][] getViewedArea(int x1, int y1, int x2, int y2) {
		int mobX = mob.getX();
		int mobY = mob.getY();
		int startX, startY, endX, endY;
		startX = mobX - x1;
		if (startX < 0) {
			startX = 0;
		}
		startY = mobY - y1;
		if (startY < 0) {
			startY = 0;
		}
		endX = mobX + x2;
		if (endX >= World.MAX_WIDTH) {
			endX = World.MAX_WIDTH - 1;
		}
		endY = mobY + y2;
		if (endY >= World.MAX_HEIGHT) {
			endY = World.MAX_HEIGHT - 1;
		}
		int xWidth;
		int yWidth;
		if (startX > endX) {
			xWidth = startX - endX;
		} else {
			xWidth = endX - startX;
		}
		if (startY > endY) {
			yWidth = startY - endY;
		} else {
			yWidth = endY - startY;
		}
		ActiveTile[][] temp = new ActiveTile[xWidth][yWidth];
		for (int x = 0; (x + startX) < endX; x++) {
			for (int y = 0; (y + startY) < endY; y++) {
				temp[x][y] = world.tiles[x + startX][y + startY];
			}
		}
		return temp;
	}

}
