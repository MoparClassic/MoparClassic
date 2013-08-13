package org.moparscape.msc.gs.model.event;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Chain<T, U> {

	protected final List<T> links = new CopyOnWriteArrayList<T>();

	public final Chain<T, U> addFirst(T link) {
		links.add(0, link);
		return this;
	}

	public final Chain<T, U> addLast(T link) {
		links.add(link);
		return this;
	}

	public final Chain<T, U> addAt(int index, T link) {
		links.add(index, link);
		return this;
	}

	public final void trigger(U param) {
		Object cont = param;
		Iterator<T> itr = links.iterator();
		while (cont != null && itr.hasNext()) {
			cont = fire(itr.next(), param);
		}
	}

	public abstract Object fire(T next, U param);
}
