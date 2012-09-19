package org.moparscape.msc.gs.persistence.impl.bun;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class CodecLookupService {
	private static final Map<String, Codec<?>> codecs = new ConcurrentHashMap<String, Codec<?>>();

	public static Codec<?> lookup(String className) {
		return codecs.get(className);
	}

	public static boolean register(String className, Codec<?> codec) {
		synchronized (codecs) {
			Codec<?> prev = codecs.put(className, codec);
			if (prev == null) {
				codecs.put(className, prev);
				return false;
			}
		}
		return true;
	}
}
