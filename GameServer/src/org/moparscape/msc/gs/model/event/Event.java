package org.moparscape.msc.gs.model.event;

public abstract class Event<T, U> {
	public abstract T fire(U param);
}
