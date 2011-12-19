package org.moparscape.msc.ls.persistence.impl;

import org.moparscape.msc.ls.persistence.StorageMedium;

public class StorageMediumFactory {

	public static StorageMedium create(String className) throws Exception {
		return Class.forName(className).asSubclass(StorageMedium.class)
				.newInstance();
	}

}
