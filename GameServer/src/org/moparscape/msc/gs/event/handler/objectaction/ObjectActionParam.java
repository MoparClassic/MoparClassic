package org.moparscape.msc.gs.event.handler.objectaction;

import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.Player;

public class ObjectActionParam {
	public final GameObject object;
	public final int click;
	public final Player player;

	public ObjectActionParam(Player p, GameObject obj, int click) {
		this.player = p;
		this.object = obj;
		this.click = click;
	}
}
