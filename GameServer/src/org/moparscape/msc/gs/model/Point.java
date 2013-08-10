package org.moparscape.msc.gs.model;

import org.moparscape.msc.gs.config.Formulae;

public class Point {
	public static Point location(int x, int y) {
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException(
					"Point may not contain non negative values x:" + x + " y:"
							+ y);
		}
		return new Point(x, y);
	}

	protected int x;

	protected int y;

	protected Point() {
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public final boolean equals(Object o) {
		if (o instanceof Point) {
			return this.x == ((Point) o).x && this.y == ((Point) o).y;
		}
		return false;
	}

	public String getDescription() {
		if (inModRoom()) {
			return "Mod Room";
		}
		int wild = wildernessLevel();
		if (wild > 0) {
			return "Wilderness lvl-" + wild;
		}
		return "Unknown";
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	public int hashCode() {
		return x << 16 | y;
	}

	public boolean inBounds(int x1, int y1, int x2, int y2) {
		return x >= x1 && x <= x2 && y >= y1 && y <= y2;
	}

	public boolean inModRoom() {
		return inBounds(64, 1639, 80, 1643);
	}

	public boolean inWilderness() {
		return wildernessLevel() > 0;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public int wildernessLevel() {
		int wild = 2203 - (y + (1776 - (944 * Formulae.getHeight(this))));
		if (x + 2304 >= 2640) {
			wild = -50;
		}
		if (wild > 0) {
			return 1 + wild / 6;
		}
		return 0;
	}

	public static boolean inWilderness(int x, int y) {
		int wild = 2203 - (y + (1776 - (944 * (int) (y / 944))));
		if (x + 2304 >= 2640) {
			wild = -50;
		}
		if (wild > 0) {
			return (1 + wild / 6) >= 1;
		}
		return false;
	}

	public boolean atAltar() {
		if (x >= 321 && y >= 183 && x <= 334 && y <= 197)
			return true;
		return false;
	}

	public boolean nearAltar() {
		if (x >= 321 - 4 && y >= 183 - 4 && x <= 334 + 4 && y <= 193 + 4)
			return true;
		return false;
	}
}
