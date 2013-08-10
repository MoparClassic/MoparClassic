package org.moparscape.msc.gs.persistence.impl;

import java.lang.reflect.Constructor;

import org.moparscape.msc.gs.persistence.DataStore;
import org.moparscape.msc.gs.util.JarUtil;
import org.moparscape.msc.gs.util.ModuleUtil;

public class DataStoreFactory {

	public static DataStore create(String className) throws Exception {
		try {
			return Class.forName(className).asSubclass(DataStore.class)
					.newInstance();
		} catch (ClassNotFoundException e) {
			Class<?> clss = JarUtil.loadClassFromJar(
					ModuleUtil.getClassLoader(), className,
					ModuleUtil.moduleFolder.getPath());
			Class<? extends DataStore> c = clss.asSubclass(DataStore.class);
			Constructor<? extends DataStore> con = c.getDeclaredConstructor();
			con.setAccessible(true);
			return con.newInstance(new Object[0]);
		}
	}
}
