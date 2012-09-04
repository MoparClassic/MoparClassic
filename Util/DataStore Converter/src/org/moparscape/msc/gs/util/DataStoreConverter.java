package org.moparscape.msc.gs.util;

import java.lang.reflect.Method;
import org.moparscape.msc.gs.persistence.DataStore;
import org.moparscape.msc.gs.persistence.impl.DataStoreFactory;

public class DataStoreConverter {

	public static void main(String[] args) throws Exception {
		convert(DataStoreFactory.create(args[0]),
				DataStoreFactory.create(args[1]));
	}

	private static void convert(DataStore old, DataStore curr) throws Exception {
		Method[] oldM = old.getClass().getDeclaredMethods();
		Method[] currM = curr.getClass().getDeclaredMethods();
		for (Method on : oldM) {
			if (on.getName().startsWith("load") && !on.getName().equals("load")) {
				Method match = findSaveFromLoad(on.getName(), currM);
				match.invoke(curr, on.invoke(old));
			}
		}

	}

	private static Method findSaveFromLoad(String first, Method[] possibles) {
		String matched = "save" + first.substring(4);
		for (Method m : possibles) {
			if (m.getName().equals(matched)) {
				return m;
			} else if (matched.equals("saveAgilityCourseDefs")
					&& m.getName().equals("saveAgilityCourseDef")) {
				return m;
			} else if (matched.equals("saveObjectFishDefs")
					&& m.getName().equals("saveObjectFishingDefs")) {
				return m;
			}
		}
		return null;
	}
}
