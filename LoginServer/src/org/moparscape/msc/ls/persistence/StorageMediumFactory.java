package org.moparscape.msc.ls.persistence;

public class StorageMediumFactory {

	public static StorageMedium createMedium(String className) throws Exception {
		return Class.forName(className).asSubclass(StorageMedium.class)
				.newInstance();
	}

}
