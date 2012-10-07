package org.moparscape.msc.gs.event;

import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.model.Mob;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Path;
import org.moparscape.msc.gs.model.definition.entity.NPCLoc;

public abstract class WalkMobToMobEvent extends DelayedEvent {
	protected Mob affectedMob;
	private NPCLoc loc = null;
	protected Mob owner;
	private int radius;
	private long startTime = 0L;

	public WalkMobToMobEvent(Mob owner, Mob affectedMob, int radius) {
		super(null, 500);

		if (owner.isRemoved()) {
			super.matchRunning = false;
			return;
		}

		if (owner instanceof Npc) {
			Npc npc = (Npc) owner;
			loc = npc.getLoc();

			if (affectedMob.getX() < (loc.minX() - 4)
					|| affectedMob.getX() > (loc.maxX() + 4)
					|| affectedMob.getY() < (loc.minY() - 4)
					|| affectedMob.getY() > (loc.maxY() + 4)) {
				super.matchRunning = false;
				return;
			}
		}

		this.owner = owner;
		owner.setPath(new Path(owner.getX(), owner.getY(), affectedMob.getX(),
				affectedMob.getY()));

		this.affectedMob = affectedMob;
		this.radius = radius;

		if (owner.withinRange(affectedMob, radius)) {
			arrived();
			super.matchRunning = false;
			return;
		}

		startTime = GameEngine.getTime();
	}

	public abstract void arrived();

	public void failed() {
	}

	public Mob getAffectedMob() {
		return affectedMob;
	}

	public final void run() {
		if (owner.isRemoved()) {
			super.matchRunning = false;
			return;
		}
		
		if (loc != null) {
			if (affectedMob.getX() < (loc.minX() - 4)
					|| affectedMob.getX() > (loc.maxX() + 4)
					|| affectedMob.getY() < (loc.minY() - 4)
					|| affectedMob.getY() > (loc.maxY() + 4)) {
				super.matchRunning = false;
				failed();
				return;
			}
		}

		if (owner.withinRange(affectedMob, radius))
			arrived();
		else if (affectedMob.hasMoved()){
			owner.resetPath();
			owner.setPath(new Path(owner.getX(), owner.getY(), affectedMob.getX(), affectedMob.getY()));
			return; // Target is moving.. correcting path
		}
		else if (owner.hasMoved()) {
			return; // We're still moving
		}
		else {
			if (GameEngine.getTime() - startTime <= 10000) // Make NPCs
			// give a 10
			// second
			// chase
			{
				if (owner.nextTo(affectedMob) && owner.finishedPath()) {
					return;
				}

				if (owner.isBusy())
					return;

				owner.setPath(new Path(owner.getX(), owner.getY(), affectedMob
						.getX(), affectedMob.getY()));
				return;
			} else
				failed();
		}

		super.matchRunning = false;
	}
}
