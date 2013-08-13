package org.moparscape.msc.gs.event.handler.objectaction;

import java.awt.Point;

import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.event.Event;

public abstract class ObjectEvent extends Event<Boolean, ObjectActionParam> {

	protected GameObject o;
	protected int click;
	protected String command;
	protected Player player;

	@Override
	public Boolean fire(ObjectActionParam param) {
		o = param.object;
		click = param.click;
		player = param.player;
		command = (click == 0 ? o.getGameObjectDef().getCommand1() : o
				.getGameObjectDef().getCommand2()).toLowerCase();
		return fire();
	}

	public abstract boolean fire();

	public boolean isAt(int x, int y) {
		return o.getX() == x && o.getY() == y;
	}

	public boolean isAt(Point p) {
		return isAt(p.x, p.y);
	}

}
