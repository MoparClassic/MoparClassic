package org.moparscape.msc.gs.plugins.dependencies;

import java.util.ArrayList;

import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.plugins.listeners.ObjectListener;
import org.moparscape.msc.gs.util.JarUtil;
import org.moparscape.msc.gs.util.Logger;

/**
 * Initiates plugins that implements some listeners
 * 
 * @author xEnt
 * 
 */
public class PluginHandler {

	/**
	 * Our PluginHandler
	 */
	public static PluginHandler handler = null;
	/**
	 * every plugin is stored in here as a 'Class'
	 */
	public static ArrayList<Class<?>> allClasses = new ArrayList<Class<?>>();
	/**
	 * All plugins using an object listener is stored here
	 */
	private ArrayList<ObjectListener> objListeners = new ArrayList<ObjectListener>();

	public static ArrayList<Class<?>> getAllClasses() {
		return allClasses;
	}

	public static void setAllClasses(ArrayList<Class<?>> allClasses) {
		PluginHandler.allClasses = allClasses;
	}

	public ArrayList<ObjectListener> getObjListeners() {
		return objListeners;
	}

	public void setObjListeners(ArrayList<ObjectListener> objListeners) {
		this.objListeners = objListeners;
	}

	/**
	 * Singleton, initiates and returns.
	 * 
	 * @return - the PluginHandler
	 */
	public static PluginHandler getPluginHandler() {
		if (handler == null)
			handler = new PluginHandler();
		return handler;
	}

	/**
	 * Initiates the plugins.
	 */
	public void initPlugins() {
		try {
			try {
				getClassesFromFileJarFile(
						"org.moparscape.msc.gs.plugins.plugs",
						System.getProperty("user.dir"));
				getClassesFromFileJarFile(
						"org.moparscape.msc.gs.plugins.plugs.skills",
						System.getProperty("user.dir"));
				getClassesFromFileJarFile("org.moparscape.msc.gs.plugins.ai",
						System.getProperty("user.dir"));
				for (Class<?> c : allClasses) {
					Object cl;
					cl = c.newInstance();
					if (cl instanceof ObjectListener) {
						ObjectListener obj = (ObjectListener) cl;
						objListeners.add(obj);
					}
				}

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Logger.println(allClasses.size() + " Plugins Loaded");
	}

	/**
	 * Fires off the action to the plugins
	 * 
	 * @param obj
	 *            - the object model
	 * @param cmd
	 *            - the command String
	 * @param player
	 *            - the player model
	 */
	public boolean handleObjectAction(GameObject obj, String cmd, Player player) {
		for (ObjectListener ol : objListeners) {
			for (int i : ol.getAssociatedIDS()) {
				if (i == obj.getID()) {
					if (ol.onObjectAction(obj, cmd, player)) // if true, it's
						// handled.
						return true;
				}

			}
		}
		return false;
	}

	/**
	 * Just gets all the classes inside the Plugins/plug folder, leeched from
	 * Java forums.
	 * 
	 * @param pckgname
	 * @param baseDirPath
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static void getClassesFromFileJarFile(String pckgname,
			String baseDirPath) throws ClassNotFoundException {
		
		for (Class<?> c : JarUtil.getClassesFromFileJarFile(pckgname, baseDirPath)) {
			allClasses.add(c);
		}

	}

}
