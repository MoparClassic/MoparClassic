package org.moparscape.msc.gs.persistence.impl.bun;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class FileLookupService {
	private static Map<String, File> files = new ConcurrentHashMap<String, File>();

	public static File lookup(String className) {
		return files.get(className);
	}

	public static boolean register(String className, File file) {
		synchronized (files) {
			File prev = files.put(className, file);
			if (prev == null) {
				files.put(className, prev);
				return false;
			}
		}
		return true;
	}
}
