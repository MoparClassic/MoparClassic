package org.moparscape.msc.gs;

import java.util.Map;

import org.apache.commons.collections.map.LRUMap;

/**
 * A basic cache backed by a {@link LRUMap}.
 * 
 * @author CodeForFame
 * 
 */
public class Cache<K, V> {

	// Shitty commons and their failure to support generics...
	@SuppressWarnings("unchecked")
	private Map<K, V> cache = new LRUMap();

	public V get(K key) {
		return cache.get(key);
	}

	public void put(K key, V value) {
		cache.put(key, value);
	}

}
