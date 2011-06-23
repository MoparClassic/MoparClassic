package org.moparscape.msc.gs.util;

import java.util.Map;

import org.apache.commons.collections.map.LRUMap;

/**
 * A basic cache backed by a {@link LRUMap}.
 * 
 * @author CodeForFame
 * 
 */
public class Cache<K, V> {

	private final Map<K, V> cache;
	
	public Cache() {
		this(100);
	}

	@SuppressWarnings("unchecked")  // Commons and their failure to support generics...
	public Cache(int maxSize) {
		cache = new LRUMap(maxSize);
	}

	public V get(K key) {
		return cache.get(key);
	}

	public V put(K key, V value) {
		return cache.put(key, value);
	}

}