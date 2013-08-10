package org.moparscape.msc.gs.util;

import java.util.Iterator;
import java.util.Set;

import org.moparscape.msc.gs.model.Entity;

public class EntityListIterator<E extends Entity> implements Iterator<E> {
	private int curIndex = 0;
	private Object[] entities;
	private EntityList<E> entityList;
	private Integer[] indicies;

	public EntityListIterator(Object[] entities, Set<Integer> indicies,
			EntityList<E> entityList) {
		this.entities = entities;
		this.indicies = indicies.toArray(new Integer[0]);
		this.entityList = entityList;
	}

	public boolean hasNext() {
		return indicies.length != curIndex;
	}

	@SuppressWarnings("unchecked")
	public E next() {
		Object temp = entities[indicies[curIndex]];
		curIndex++;
		return (E) temp;
	}

	public void remove() {
		if (curIndex >= 1) {
			entityList.remove(indicies[curIndex - 1]);
		}
	}

}
