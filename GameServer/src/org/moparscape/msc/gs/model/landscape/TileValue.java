package org.moparscape.msc.gs.model.landscape;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TileValue {

	private static final List<TileValue> tiles = new CopyOnWriteArrayList<TileValue>();

	public static TileValue create(int diagWallVal, byte[] v) {
		TileValue curr = new TileValue(diagWallVal, v);
		int index = tiles.indexOf(curr);
		if (index == -1) {
			tiles.add(curr);
			return curr;
		}
		return tiles.get(index);
	}

	private TileValue(int diagWallVal, byte[] v) {
		if (v.length != 6) {
			throw new IndexOutOfBoundsException("Must have a size of 6");
		}

		this.diagWallVal = diagWallVal;
		this.horizontalWallVal = v[0];
		this.mapValue = v[1];
		this.objectValue = v[2];
		this.overlay = v[3];
		this.verticalWallVal = v[4];
		this.elevation = v[5];
	}

	public final int diagWallVal;
	public final byte horizontalWallVal;
	public final byte mapValue;
	public final byte objectValue;
	public final byte overlay;
	public final byte verticalWallVal;
	public final byte elevation;

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof TileValue)) {
			return false;
		}

		TileValue t = (TileValue) o;

		if (this.diagWallVal == t.diagWallVal
				&& this.horizontalWallVal == t.horizontalWallVal
				&& this.mapValue == t.mapValue
				&& this.objectValue == t.objectValue
				&& this.overlay == t.overlay
				&& this.verticalWallVal == t.verticalWallVal
				&& this.elevation == t.elevation) {
			return true;
		}

		return false;
	}
}