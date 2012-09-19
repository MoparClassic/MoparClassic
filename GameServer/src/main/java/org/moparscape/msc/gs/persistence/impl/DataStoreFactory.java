package org.moparscape.msc.gs.persistence.impl;

import org.moparscape.msc.gs.persistence.DataStore;

public class DataStoreFactory {

	public static DataStore create(String className) throws Exception {
		return Class.forName(className).asSubclass(DataStore.class)
				.newInstance();
	}

}
