package org.moparscape.msc.gs.model;

import org.moparscape.msc.gs.model.definition.EntityHandler;
import org.moparscape.msc.gs.model.definition.entity.GameObjectDef;
import org.moparscape.msc.gs.model.definition.entity.GameObjectLoc;
import org.moparscape.msc.gs.model.definition.extra.DoorDef;

public class GameObject extends Entity {
	/**
	 * Returns the ID of an item contained in the object.
	 * 
	 * @author Konijn
	 */
	private int containsItem = -1;
	/**
	 * The direction the object points in
	 */
	private int direction;
	/**
	 * Location definition of the object
	 */
	private GameObjectLoc loc = null;
	/**
	 * Set when the item has been destroyed to alert players
	 */
	private boolean removed = false;

	/**
	 * The type of object
	 */
	private int type;

	public GameObject(GameObjectLoc loc) {
		direction = loc.direction;
		type = loc.type;
		this.loc = loc;
		super.setID(loc.id);
		super.setLocation(Point.location(loc.x, loc.y));
	}

	public GameObject(Point location, int id, int direction, int type) {
		this(new GameObjectLoc(id, location.getX(), location.getY(), direction,
				type));
	}

	public int containsItem() {
		return containsItem;
	}

	public void containsItem(int item) {
		containsItem = item;
	}

	public boolean equals(Object o) {
		if (o instanceof GameObject) {
			GameObject go = (GameObject) o;
			return go.getLocation().equals(getLocation())
					&& go.getID() == getID()
					&& go.getDirection() == getDirection()
					&& go.getType() == getType();
		}
		return false;
	}

	public int getDirection() {
		return direction;
	}

	public DoorDef getDoorDef() {
		return EntityHandler.getDoorDef(super.getID());
	}

	public GameObjectDef getGameObjectDef() {
		return EntityHandler.getGameObjectDef(super.getID());
	}

	public GameObjectLoc getLoc() {
		return loc;
	}

	public int getType() {
		return type;
	}

	public boolean isOn(int x, int y) {
		int width, height;
		if (type == 1) {
			width = height = 1;
		} else if (direction == 0 || direction == 4) {
			width = getGameObjectDef().getWidth();
			height = getGameObjectDef().getHeight();
		} else {
			height = getGameObjectDef().getWidth();
			width = getGameObjectDef().getHeight();
		}
		if (type == 0) { // Object
			return x >= getX() && x <= (getX() + width) && y >= getY()
					&& y <= (getY() + height);
		} else { // Door
			return x == getX() && y == getY();
		}
	}

	public boolean isRemoved() {
		return removed;
	}

	public boolean isTelePoint() {
		return EntityHandler.getObjectTelePoint(getLocation(), null) != null;
	}

	public void remove() {
		removed = true;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String toString() {
		return (type == 0 ? "GameObject" : "WallObject") + ":id = " + id
				+ "; dir = " + direction + "; location = "
				+ location.toString() + ";";
	}

}
