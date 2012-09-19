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

	public V put(K key, V value) {
		return cache.put(key, value);
	}
	
	public V remove(K key) {
		return cache.remove(key);
	}
	
	public V remove(K key, V value) {
		V v = cache.get(key);
		if(v.equals(value)) {
			return cache.remove(value);
		}
		return null;
	}

}
