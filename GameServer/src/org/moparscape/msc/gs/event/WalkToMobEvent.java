package org.moparscape.msc.gs.event;

import org.moparscape.msc.gs.model.Mob;
import org.moparscape.msc.gs.model.Player;

public abstract class WalkToMobEvent extends DelayedEvent {
	protected Mob affectedMob;
	private int radius;

	public WalkToMobEvent(Player owner, Mob affectedMob, int radius) {
		super(owner, 600);
		this.affectedMob = affectedMob;
		this.radius = radius;
		if (owner.withinRange(affectedMob, radius)) {
			arrived();
			super.matchRunning = false;
		}
	}

	public abstract void arrived();

	public void failed() {
	} // Not abstract as isn't required

	public Mob getAffectedMob() {
		return affectedMob;
	}

	public final void run() {
		if (owner.withinRange(affectedMob, radius)) {
			arrived();
		} else if (owner.hasMoved()) {
			return; // We're still moving
		} else {
			failed();
		}
		super.matchRunning = false;
	}

}