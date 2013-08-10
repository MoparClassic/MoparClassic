package org.moparscape.msc.gs.model;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.config.Formulae;

public class Entity {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	protected int id;

	protected int index;

	protected Point location;

	public final int getID() {
		return id;
	}

	public final int getIndex() {
		return index;
	}

	public final Point getLocation() {
		return location;
	}

	public final int getX() {
		return location.getX();
	}

	public final int getY() {
		return location.getY();
	}

	private boolean isBlocking(Entity e, int x, int y, int bit) {
		return isMapBlocking(e, x, y, (byte) bit)
				|| isObjectBlocking(e, x, y, (byte) bit);
	}

	private boolean isMapBlocking(Entity e, int x, int y, byte bit) {
		byte val = world.getTileValue(x, y).mapValue;
		if ((val & bit) != 0) { // There is a wall in the way
			return true;
		}
		if ((val & 16) != 0) { // There is a diagonal wall here: \
			return true;
		}
		if ((val & 32) != 0) { // There is a diagonal wall here: /
			return true;
		}
		if ((val & 64) != 0
				&& (e instanceof Npc || e instanceof Player
						|| (e instanceof Item && !((Item) e).isOn(x, y)) || (e instanceof GameObject && !((GameObject) e)
						.isOn(x, y)))) { // There
			// is
			// an
			// object
			// here,
			// doesn't
			// block items (ontop of it) or the
			// object itself though
			return true;
		}
		return false;
	}

	private boolean isObjectBlocking(Entity e, int x, int y, byte bit) {
		byte val = world.getTileValue(x, y).objectValue;
		if ((val & bit) != 0
				&& !Formulae.doorAtFacing(e, x, y, Formulae.bitToDoorDir(bit))
				&& !Formulae.objectAtFacing(e, x, y,
						Formulae.bitToObjectDir(bit))) { // There
			// is
			// a
			// wall
			// in
			// the
			// way
			return true;
		}
		if ((val & 16) != 0 && !Formulae.doorAtFacing(e, x, y, 2)
				&& !Formulae.objectAtFacing(e, x, y, 3)) { // There
			// is
			// a
			// diagonal wall
			// here: \
			return true;
		}
		if ((val & 32) != 0 && !Formulae.doorAtFacing(e, x, y, 3)
				&& !Formulae.objectAtFacing(e, x, y, 1)) { // There
			// is
			// a
			// diagonal wall
			// here: /
			return true;
		}
		if ((val & 64) != 0
				&& (e instanceof Npc || e instanceof Player
						|| (e instanceof Item && !((Item) e).isOn(x, y)) || (e instanceof GameObject && !((GameObject) e)
						.isOn(x, y)))) { // There
			// is
			// an
			// object
			// here,
			// doesn't
			// block items (ontop of it) or the
			// object itself though
			return true;
		}
		return false;
	}

	public int[] nextStep(int myX, int myY, Entity e) {
		if (myX == e.getX() && myY == e.getY()) {
			return new int[] { myX, myY };
		}
		int newX = myX, newY = myY;
		boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;

		if (myX > e.getX()) {
			myXBlocked = isBlocking(e, myX - 1, myY, 8); // Check right tiles
			// left wall
			newX = myX - 1;
		} else if (myX < e.getX()) {
			myXBlocked = isBlocking(e, myX + 1, myY, 2); // Check left tiles
			// right wall
			newX = myX + 1;
		}

		if (myY > e.getY()) {
			myYBlocked = isBlocking(e, myX, myY - 1, 4); // Check top tiles
			// bottom wall
			newY = myY - 1;
		} else if (myY < e.getY()) {
			myYBlocked = isBlocking(e, myX, myY + 1, 1); // Check bottom tiles
			// top wall
			newY = myY + 1;
		}

		// If both directions are blocked OR we are going straight and the
		// direction is blocked
		if ((myXBlocked && myYBlocked) || (myXBlocked && myY == newY)
				|| (myYBlocked && myX == newX)) {
			return null;
		}

		if (newX > myX) {
			newXBlocked = isBlocking(e, newX, newY, 2); // Check dest tiles
			// right wall
		} else if (newX < myX) {
			newXBlocked = isBlocking(e, newX, newY, 8); // Check dest tiles left
			// wall
		}

		if (newY > myY) {
			newYBlocked = isBlocking(e, newX, newY, 1); // Check dest tiles top
			// wall
		} else if (newY < myY) {
			newYBlocked = isBlocking(e, newX, newY, 4); // Check dest tiles
			// bottom wall
		}

		// If both directions are blocked OR we are going straight and the
		// direction is blocked
		if ((newXBlocked && newYBlocked) || (newXBlocked && myY == newY)
				|| (myYBlocked && myX == newX)) {
			return null;
		}

		// If only one direction is blocked, but it blocks both tiles
		if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) {
			return null;
		}

		return new int[] { newX, newY };
	}

	public final boolean nextTo(Entity e) {
		int[] currentCoords = { getX(), getY() };
		while (currentCoords[0] != e.getX() || currentCoords[1] != e.getY()) {
			currentCoords = nextStep(currentCoords[0], currentCoords[1], e);
			if (currentCoords == null) {
				return false;
			}
		}
		return true;
	}

	public final void setID(int newid) {
		id = newid;
	}

	public final void setIndex(int newIndex) {
		index = newIndex;
	}

	public void setLocation(Point p) {
		if (this instanceof Player && location != null && Config.f2pWildy) {
			Player pl = (Player) this;
			if (pl != null && getX() > 0 && getY() > 0) {
				if (!Point.inWilderness(getX(), getY())
						&& Point.inWilderness(p.getX(), p.getY())) {
					pl.p2pWildy();
				}
			}
		}

		world.setLocation(this, location, p);
		location = p;
	}

	public final boolean withinRange(Entity e, int radius) {
		return withinRange(e.getLocation(), radius);
	}

	public final boolean withinRange(Point p, int radius) {
		int xDiff = Math.abs(location.getX() - p.getX());
		int yDiff = Math.abs(location.getY() - p.getY());
		return xDiff <= radius && yDiff <= radius;
	}

}
