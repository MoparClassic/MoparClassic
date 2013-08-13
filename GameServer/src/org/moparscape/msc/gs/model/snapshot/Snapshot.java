package org.moparscape.msc.gs.model.snapshot;

import org.moparscape.msc.gs.core.GameEngine;

/**
 * Snapshot abstract class
 * 
 * @author Pets
 * 
 */
public abstract class Snapshot {

	private long eventTime;
	protected String owner;

	public Snapshot(String owner) {
		this.owner = owner;
		this.eventTime = GameEngine.getTimestamp();

	}

	public long getTimestamp() {
		return eventTime;
	}

	public String getOwner() {
		return owner;
	}
}
