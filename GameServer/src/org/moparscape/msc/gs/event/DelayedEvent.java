package org.moparscape.msc.gs.event;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.core.DelayedEventHandler;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;

public abstract class DelayedEvent {
	public static final World world = Instance.getWorld();
	protected int delay = 500;
	protected final DelayedEventHandler handler = Instance.getWorld()
			.getDelayedEventHandler();
	private long lastRun = GameEngine.getTime();
	protected Player owner;
	protected boolean matchRunning = true;
	public Object[] args = null;

	// f2p
	public DelayedEvent(Player owner, int delay) {
		this.owner = owner;
		this.delay = delay;
	}

	public DelayedEvent(Player owner, int delay, Object[] arg) {
		args = arg;
		this.owner = owner;
		this.delay = delay;
	}

	public boolean belongsTo(Player player) {
		return owner != null && owner.equals(player);
	}

	public int getDelay() {
		return delay;
	}

	public Object getIdentifier() {
		return null;
	}

	public Player getOwner() {
		return owner;
	}

	public boolean hasOwner() {
		return owner != null;
	}

	public boolean is(DelayedEvent e) {
		return (e.getIdentifier() != null && e.getIdentifier().equals(
				getIdentifier()));
	}

	public abstract void run();

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public void setLastRun(long time) {
		lastRun = time;
	}

	public final boolean shouldRemove() {
		return !matchRunning;
	}

	public final boolean shouldRun() {
		return matchRunning && GameEngine.getTime() - lastRun >= delay;
	}

	public final void stop() {
		matchRunning = false;
	}

	public int timeTillNextRun() {
		int time = (int) (delay - (GameEngine.getTime() - lastRun));
		return time < 0 ? 0 : time;
	}

	public final void updateLastRun() {
		lastRun = GameEngine.getTime();
	}
}
