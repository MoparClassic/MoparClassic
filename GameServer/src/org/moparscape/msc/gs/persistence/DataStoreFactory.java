package org.moparscape.msc.gs.persistence;

public class DataStoreFactory {

	public static DataStore create(String className) throws Exception {
		return Class.forName(className).asSubclass(DataStore.class)
				.newInstance();
	}

}
