package org.moparscape.msc.gs.model;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.model.landscape.ActiveTile;
import org.moparscape.msc.gs.model.landscape.TileValue;

public class PathHandler {
	/**
	 * World instance
	 */
	private static final World world = Instance.getWorld();

	/**
	 * Attempts to create a path to the given coordinates
	 */
	public static Path makePath(int startX, int startY, int x1, int y1, int x2,
			int y2, boolean flag) {
		return null;
	}

	/**
	 * The waypoint in the path we are currently at
	 */
	private int curWaypoint;
	/**
	 * The mob that this path belongs to
	 */
	private Mob mob;

	/**
	 * The path we are walking
	 */
	private Path path;

	/**
	 * Constructs a new PathHandler belonging to the given Mob
	 */
	public PathHandler(Mob m) {
		mob = m;
		resetPath();
	}

	/**
	 * Are we are the start of the path?
	 */
	protected boolean atStart() {
		return mob.getX() == path.getStartX() && mob.getY() == path.getStartY();
	}

	/**
	 * Checks if we are at the given waypoint
	 */
	protected boolean atWaypoint(int waypoint) {
		return path.getWaypointX(waypoint) == mob.getX()
				&& path.getWaypointY(waypoint) == mob.getY();
	}

	private int[] cancelCoords() {
		resetPath();
		return new int[] { -1, -1 };
	}

	/**
	 * Checks if we have reached the end of our path
	 */
	public boolean finishedPath() {
		if (path == null) {
			return true;
		}
		if (path.length() > 0) {
			return atWaypoint(path.length() - 1);
		} else {
			return atStart();
		}
	}

	/**
	 * Gets the next coordinate in the right direction
	 */
	protected int[] getNextCoords(int startX, int destX, int startY, int destY) {
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
				return cancelCoords();
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
				return cancelCoords();
			}

			// If only one direction is blocked, but it blocks both tiles
			if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) {
				return cancelCoords();
			}

			return coords;
		} catch (Exception e) {
			return cancelCoords();
		}
	}

	private boolean isBlocking(byte val, byte bit) {
		if (path != null && path.isNoClip())
			return false;

		if ((val & bit) != 0) { // There is a wall in the way
			return true;
		}
		if ((val & 16) != 0) { // There is a diagonal wall here: \
			return true;
		}
		if ((val & 32) != 0) { // There is a diagonal wall here: /
			return true;
		}
		if ((val & 64) != 0) { // This tile is unwalkable
			return true;
		}
		return false;
	}

	public boolean isBlocking(int x, int y, int bit) {
		if (path != null && path.isNoClip())
			return false;
		if (mob instanceof Player) {
			Player p = (Player) mob;
			if (p.isMod())
				return false;
		}
		TileValue t = world.getTileValue(x, y);
		return isBlocking(t.mapValue, (byte) bit)
				|| isBlocking(t.objectValue, (byte) bit) || isMobBlocking(x, y);
	}

	private boolean isMobBlocking(int x, int y) {
		ActiveTile t = world.getTile(x, y);
		if (mob instanceof Player) {
			if (t.hasNpcs()) {
				for (Npc n : t.getNpcs()) {
					if (n.getDef().isAggressive()
							&& !n.getLocation().inWilderness())
						return true;
				}
			}
		}
		if (mob instanceof Npc) {
			Npc n = (Npc) mob;
			if (n.getChasing() != null)
				if (t.hasPlayers() && t.getPlayers().contains(n.getChasing()))
					if (x == n.getChasing().getX()
							&& y == n.getChasing().getY())
						return false;
			if (t.hasNpcs() || (t.hasPlayers() && n.getChasing() != null))
				return true;
		}
		return false;
	}

	/**
	 * Resets the path (stops movement)
	 */
	protected void resetPath() {
		path = null;
		curWaypoint = -1;
	}

	//
	/**
	 * Updates our position to the next in the path
	 */
	protected void setNextPosition() {
		int[] newCoords = { -1, -1 };
		if (curWaypoint == -1) {
			if (atStart()) {
				curWaypoint = 0;
			} else {
				newCoords = getNextCoords(mob.getX(), path.getStartX(),
						mob.getY(), path.getStartY());
			}
		}
		if (curWaypoint > -1) {
			if (atWaypoint(curWaypoint)) {
				curWaypoint++;
			}
			if (curWaypoint < path.length()) {
				newCoords = getNextCoords(mob.getX(),
						path.getWaypointX(curWaypoint), mob.getY(),
						path.getWaypointY(curWaypoint));
			} else {
				resetPath();
			}
		}
		if (newCoords[0] > -1 && newCoords[1] > -1) {
			mob.setLocation(Point.location(newCoords[0], newCoords[1]));
		}
	}

	/**
	 * Creates a new path and sets us walking it
	 */
	public void setPath(int startX, int startY, byte[] waypointXoffsets,
			byte[] waypointYoffsets) {
		setPath(new Path(startX, startY, waypointXoffsets, waypointYoffsets));
	}

	/**
	 * Sets us on the given path
	 */
	public void setPath(Path path) {
		curWaypoint = -1;
		this.path = path;
	}

	/**
	 * Updates the point in the path to the next one assuming we are not
	 * finished
	 */
	public void updatePosition() {
		if (!finishedPath()) {
			setNextPosition();
		}
	}

}
