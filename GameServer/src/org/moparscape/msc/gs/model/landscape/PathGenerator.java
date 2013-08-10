package org.moparscape.msc.gs.model.landscape;

import org.moparscape.msc.gs.Instance;

/**
 * 
 * @author xEnt Class generates a path and checks if the path is valid or not.
 */
public class PathGenerator {

	/**
	 * If the tile has a wall on it, here is a list of the wall id's allowed to
	 * shoot thru Use the landscape editor to record their ID's
	 */
	public static final int[][] ALLOWED_WALL_ID_TYPES = { { 5, 6, 42, 14 }, // VERTICAL
			// //
			// WALL
			// ID's
			{ 5, 6, 42, 14 }, // HORIZONTAL WALL ID's
			{ 229, 5 } // DIAGONAL WALL ID's
	};

	private int destX;

	private int destY;

	private int ourX;

	private int ourY;

	private int stuckX;

	private int stuckY;

	public PathGenerator(int x, int y, int destx, int desty) {
		ourX = x;
		ourY = y;
		destX = destx;
		destY = desty;
	}

	private int[] cancelCoords(int x, int y) {
		stuckX = x;
		stuckY = y;
		return new int[] { -1, -1 };
	}

	private int[] getNextCoords(int startX, int destX, int startY, int destY) {
		try {
			int[] coords = { startX, startY };
			boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;
			if (startX > destX) {
				myXBlocked = isBlocking(startX - 1, startY, 8); // Check right
				// tiles left
				// wall
				coords[0] = startX - 1;
			} else if (startX < destX) {
				myXBlocked = isBlocking(startX + 1, startY, 2); // Check left
				// tiles right
				// wall
				coords[0] = startX + 1;
			}

			if (startY > destY) {
				myYBlocked = isBlocking(startX, startY - 1, 4); // Check top
				// tiles bottom
				// wall
				coords[1] = startY - 1;
			} else if (startY < destY) {
				myYBlocked = isBlocking(startX, startY + 1, 1); // Check bottom
				// tiles top
				// wall
				coords[1] = startY + 1;
			}

			// If both directions are blocked OR we are going straight and the
			// direction is blocked
			if ((myXBlocked && myYBlocked) || (myXBlocked && startY == destY)
					|| (myYBlocked && startX == destX)) {
				return cancelCoords(coords[0], coords[1]);
			}

			if (coords[0] > startX) {
				newXBlocked = isBlocking(coords[0], coords[1], 2); // Check dest
				// tiles
				// right
				// wall
			} else if (coords[0] < startX) {
				newXBlocked = isBlocking(coords[0], coords[1], 8); // Check dest
				// tiles
				// left wall
			}

			if (coords[1] > startY) {
				newYBlocked = isBlocking(coords[0], coords[1], 1); // Check dest
				// tiles top
				// wall
			} else if (coords[1] < startY) {
				newYBlocked = isBlocking(coords[0], coords[1], 4); // Check dest
				// tiles
				// bottom
				// wall
			}

			// If both directions are blocked OR we are going straight and the
			// direction is blocked
			if ((newXBlocked && newYBlocked)
					|| (newXBlocked && startY == coords[1])
					|| (myYBlocked && startX == coords[0])) {

				return cancelCoords(coords[0], coords[1]);
			}

			// If only one direction is blocked, but it blocks both tiles
			if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) {
				return cancelCoords(coords[0], coords[1]);
			}

			return coords;
		} catch (Exception e) {
			return cancelCoords(-1, -1);
		}
	}

	private boolean isBlocking(byte val, byte bit) {
		if ((val & bit) != 0) { // There is a wall in the way
			return true;
		}
		if ((val & 16) != 0) { // There is a diagonal wall here: \
			return true;
		}
		if ((val & 32) != 0) { // There is a diagonal wall here: /
			return true;
		}
		if ((val & 64) != 0) { // This tile is un walkable
			return true;
		}
		return false;
	}

	private boolean isBlocking(int x, int y, int bit) {
		TileValue t = Instance.getWorld().getTileValue(x, y);
		ActiveTile tile = Instance.getWorld().getTile(x, y);
		if (tile.hasGameObject()) {
			if (tile.getGameObject().getGameObjectDef().name
					.equalsIgnoreCase("tree")) {
				return true;
			}
		} else
			tile.cleanItself();
		if (t.overlay == 2 || t.overlay == 11) // water & lava
			return false;
		return isBlocking(t.mapValue, (byte) bit);
	}

	/**
	 * 
	 * @return - if the path is valid to shoot a projectile too
	 */
	public boolean isValid() {
		return !isWallInbetween();
	}

	/**
	 * I've added the wall data to TileValue, now we can allow certain walls to
	 * be shot through.
	 */
	private boolean isWallAllowed(int x, int y) {
		TileValue t = Instance.getWorld().getTileValue(x, y);
		if (t != null) {
			for (int i = 0; i < ALLOWED_WALL_ID_TYPES[0].length; i++)
				if (ALLOWED_WALL_ID_TYPES[0][i] == (t.verticalWallVal & 0xff))
					return true;

			for (int i = 0; i < ALLOWED_WALL_ID_TYPES[1].length; i++)
				if (ALLOWED_WALL_ID_TYPES[1][i] == (t.horizontalWallVal & 0xff))
					return true;

			for (int i = 0; i < ALLOWED_WALL_ID_TYPES[2].length; i++)
				if (ALLOWED_WALL_ID_TYPES[2][i] == t.diagWallVal)
					return true;
		}
		return false;
	}

	/**
	 * @author xEnt Calculations to check if a wall is in between your target
	 *         your ranging/maging at
	 */
	private boolean isWallInbetween() {

		int enemyX = destX;
		int enemyY = destY;

		int newX = ourX;
		int newY = ourY;
		int[] coords;
		int count = 0;
		while (true) {
			count++;
			if (count > 1) // this should not happen
				break; // in case something goes wrong, let's not tie up the CPU

			coords = getNextCoords(newX, enemyX, newY, enemyY);
			newX = coords[0];
			newY = coords[1];
			if (newX == -1)
				return !isWallAllowed(stuckX, stuckY);

			if (newX == enemyX && newY == enemyY)
				return false;
		}
		return false; // should not happen.
	}

}
