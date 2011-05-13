package org.moparscape.msc.gs.model.snapshot;


/**
 * Snapshot abstract class
 * @author Pets
 *
 */
public abstract class Snapshot {

	private long eventTime;
	protected String owner;

	public Snapshot(String owner) {
		this.owner = owner;
		this.eventTime = System.currentTimeMillis();

	}

	public long getTimestamp() {
		return eventTime;
	}

	public String getOwner() {
		return owner;
	}
}
