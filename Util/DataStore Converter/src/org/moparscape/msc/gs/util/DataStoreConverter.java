package org.moparscape.msc.gs.util;

import java.lang.reflect.Method;
import org.moparscape.msc.gs.persistence.DataStore;
import org.moparscape.msc.gs.persistence.impl.DataStoreFactory;

/**
 * Converts files from one {@link DataStore} to another.
 * 
 * @author Joe Pritzel
 * 
 */
public class DataStoreConverter {

	/**
	 * @param args
	 *            - Index 0 must be the old DataStore and index 1 must be the
	 *            new DataStore.
	 * @throws Exception
	 *             - Thrown if any error occurs.
	 */
	public static void main(String[] args) throws Exception {
		convert(DataStoreFactory.create(args[0]),
				DataStoreFactory.create(args[1]));
	}

	/**
	 * Goes through the load methods of the old DataStore and passes the result
	 * to the new DataStore's save methods.
	 * 
	 * @param old
	 *            - The old DataStore.
	 * @param curr
	 *            - The new DataStore.
	 * @throws Exception
	 *             - Thrown if any error occurs.
	 */
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

	/**
	 * Finds the save method that corresponds the the given load method.
	 * 
	 * @param first
	 *            - The name of the method to find a companion for.
	 * @param possibles
	 *            - An array of possible methods.
	 * @return The method that corresponds to the given method.
	 */
	private static Method findSaveFromLoad(String first, Method[] possibles) {
		String matched = "save" + first.substring(4);
		for (Method m : possibles) {
			if (m.getName().equals(matched)) {
				return m;
			}

			// These don't follow the general pattern, but we still need to find
			// them.
			if (matched.equals("saveAgilityCourseDefs")
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
