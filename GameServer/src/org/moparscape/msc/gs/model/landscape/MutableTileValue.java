package org.moparscape.msc.gs.model.landscape;

public class MutableTileValue {

	public MutableTileValue(TileValue t) {
		this.diagWallVal = t.diagWallVal;
		this.horizontalWallVal = t.horizontalWallVal;
		this.mapValue = t.mapValue;
		this.objectValue = t.objectValue;
		this.verticalWallVal = t.verticalWallVal;
		this.elevation = t.elevation;
	}

	public int diagWallVal;
	public byte horizontalWallVal;
	public byte mapValue;
	public byte objectValue;
	public byte overlay;
	public byte verticalWallVal;
	public byte elevation;

	public TileValue toTileValue() {
		return TileValue.create(diagWallVal, new byte[] { horizontalWallVal,
				mapValue, objectValue, overlay, verticalWallVal, elevation });
	}
}
