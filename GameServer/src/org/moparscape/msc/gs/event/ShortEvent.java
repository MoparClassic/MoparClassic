package org.moparscape.msc.gs.event;

import org.moparscape.msc.gs.model.Player;

public abstract class ShortEvent extends SingleEvent {

	public ShortEvent(Player owner) {
		super(owner, 1500);
	}

	public abstract void action();

}