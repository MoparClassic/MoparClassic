package org.moparscape.msc.gs.plugins.dependencies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.plugins.listeners.ObjectListener;
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
	/**
	 * All npc plugins using some AI go here.
	 */
	private ArrayList<NpcAI> npcAI = new ArrayList<NpcAI>();

	public static ArrayList<Class<?>> getAllClasses() {
		return allClasses;
	}

	public static void setAllClasses(ArrayList<Class<?>> allClasses) {
		PluginHandler.allClasses = allClasses;
	}

	public NpcAI getNpcAIHandler(int id) {
		for (NpcAI ai : getNpcAI()) {
			if (ai.getID() == id)
				return ai;
		}
		return null;
	}

	public ArrayList<NpcAI> getNpcAI() {
		return npcAI;
	}

	public void setNpcAI(ArrayList<NpcAI> npcAI) {
		this.npcAI = npcAI;
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
					} else if (cl instanceof NpcAI) {
						NpcAI ai = (NpcAI) cl;
						npcAI.add(ai);
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
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		String path = pckgname.replace('.', '/') + "/";
		File mF = new File(baseDirPath);
		String[] files = mF.list();
		ArrayList<String> jars = new ArrayList<String>();
		for (int i = 0; i < files.length; i++)
			if (files[i].endsWith(".jar"))
				jars.add(files[i]);

		for (int i = 0; i < jars.size(); i++) {
			try {
				JarFile currentFile = new JarFile(jars.get(i).toString());
				for (Enumeration<?> e = currentFile.entries(); e
						.hasMoreElements();) {
					JarEntry current = (JarEntry) e.nextElement();
					if (current.getName().contains("$"))
						continue;
					if (current.getName().length() > path.length()
							&& current.getName().substring(0, path.length())
									.equals(path)
							&& current.getName().endsWith(".class"))
						classes.add(Class.forName(current.getName()
								.replaceAll("/", ".").replace(".class", "")));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Class<?>[] classesA = new Class<?>[classes.size()];
		classes.toArray(classesA);
		for (Class<?> c : classesA) {
			allClasses.add(c);
		}

	}

}
