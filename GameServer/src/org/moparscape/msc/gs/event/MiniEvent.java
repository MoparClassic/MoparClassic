package org.moparscape.msc.gs.event;

import org.moparscape.msc.gs.model.Player;

public abstract class MiniEvent extends SingleEvent {

	public MiniEvent(Player owner) {
		super(owner, 500);
	}

	public MiniEvent(Player owner, int delay) {
		super(owner, delay);
	}

	public MiniEvent(Player owner, int delay, Object[] arg) {
		super(owner, delay, arg);
	}

	public abstract void action();

}