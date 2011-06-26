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
	
	private Map<K, V> cache;

	public Cache() {
		this(100);
	}

	@SuppressWarnings("unchecked")
	public Cache(int size) {
		cache = new LRUMap(size);
	}

	public V get(K key) {
		return cache.get(key);
	}

	public void put(K key, V value) {
		cache.put(key, value);
	}

}
