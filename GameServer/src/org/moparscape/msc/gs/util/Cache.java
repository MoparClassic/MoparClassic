package org.moparscape.msc.gs.util;

import java.util.concurrent.Callable;

import com.google.common.cache.CacheBuilder;

/**
 * A basic cache backed by a {@link LRUMap}.
 * 
 * @author CodeForFame
 * 
 */
public class Cache<K, V> {

	private com.google.common.cache.Cache<K, V> cache;

	public Cache() {
		this(100);
	}

	public Cache(int size) {
		cache = CacheBuilder.newBuilder().maximumSize(size).build();
	}

	public V get(K key) {
		return cache.getIfPresent(key);
	}

	public V put(K key, final V value) {
		try {
			return cache.get(key, new Callable<V>() {
				@Override
				public V call() {
					return value;
				}
			});
		} catch (Exception e) {
			cache.put(key, value);
			return null;
		}
	}

	public V remove(K key) {
		V v = get(key);
		cache.invalidate(key);
		return v;
	}

	public V remove(K key, V value) {
		V v = get(key);
		if (v.equals(value)) {
			return remove(key);
		}
		return null;
	}

}