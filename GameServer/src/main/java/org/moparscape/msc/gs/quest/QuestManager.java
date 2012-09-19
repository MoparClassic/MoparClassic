package org.moparscape.msc.gs.quest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Item;
import org.moparscape.msc.gs.model.Mob;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.util.Logger;

/**
 * Manages the loading and execution of quests TODO: add quest error handling?
 * 
 * @author punKrockeR
 */
public class QuestManager {
	// Holds loaded quests
	private static Vector<Quest> quests = null;
	// Holds the list of quest ids
	private static Vector<Integer> questIds = null;
	// The quest events handler
	private static final QuestEventManager questEventManager = new QuestEventManager(
			50);
	// The quest events handler condition
	private static boolean running = true;

	/**
	 * Constructs a new quest manager
	 */
	public QuestManager() {
		final Thread eventHandler = new Thread(new Runnable() {
			public void run() {
				while (running) {
					try {
						questEventManager.process();
						try {
							Thread.sleep(100);
						} catch (Exception e) {
						}
					} catch (Exception error) {
						Logger.println("\n\nQUEST MANAGER EVENT HANDLER ERROR:\n\n");
						error.printStackTrace();
					}
				}
			}
		}, "QuestManager.EventHandler");

		eventHandler.start();
	}

	/**
	 * Kills the quest manager
	 */
	public void kill() {
		running = false;
		questEventManager.kill();
	}

	public static ArrayList<Class<?>> questz = new ArrayList<Class<?>>();

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
			questz.add(c);
		}

	}

	/**
	 * @return if the quests were loaded successfully or not
	 */
	public final boolean loadQuests() {
		try {
			long start = GameEngine.getTime();

			if (quests != null)
				quests.clear();
			if (questIds != null)
				questIds.clear();

			/*
			 * File questDir = new
			 * File("src/org.moparscape.msc/gs/plugins/quests/");
			 * 
			 * if(questDir == null || !questDir.exists()) throw new
			 * Exception("Quests directory " + questDir.getAbsolutePath() +
			 * " does not exist!");
			 * 
			 * // List valid .class files File[] questFiles =
			 * questDir.listFiles(new FilenameFilter() {
			 * 
			 * @Override public boolean accept(File dir, String name) { return
			 * name.endsWith(".class") && name.indexOf("$") <= -1; } });
			 */
			getClassesFromFileJarFile("org.moparscape.msc.gs.plugins.quests",
					System.getProperty("user.dir"));
			quests = new Vector<Quest>(questz.size());
			questIds = new Vector<Integer>(questz.size());
			int questCount = 0;

			for (Class<?> quest : questz) {
				try {
					String name = quest.getName(); // Remove .class ext
					// Class c = Class.forName(name);
					Quest script = (Quest) quest.getConstructor().newInstance();
					if (!script.loadQuest()) {
						Logger.println("Broken Quest: " + quest);
						continue;
					}

					name = name.trim().toLowerCase();
					script.init();
					questIds.add(script.getUniqueID());
					quests.add(script);
					questCount++;
				} catch (Exception e2) {
					e2.printStackTrace();
					Logger.println("Quest file \"" + quest.getName()
							+ "\" skipped due to load failure.");
				}
			}

			Logger.println("Loaded " + questCount + " quests successfully ("
					+ (GameEngine.getTime() - start) + "ms)");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * @return all quests
	 */
	@SuppressWarnings("unchecked")
	public Vector<Quest> getQuests() {
		return (Vector<Quest>) quests.clone();
	}

	/**
	 * @return the Quest with the given uid
	 */
	public Quest getQuestById(int id) {
		for (Quest q : quests) {
			if (q.getUniqueID() == id)
				return q;
		}

		return null;
	}

	/**
	 * @return quest ids
	 */
	public Vector<Integer> getQuestIds() {
		return questIds;
	}

	/**
	 * @return how many quests there are
	 */
	public int getQuestCount() {
		return quests.size();
	}

	/**
	 * Triggers the quest action
	 */
	public synchronized void handleAction(final QuestAction action,
			final Object[] args, final Player player) {
		for (final Quest quest : quests)
			questEventManager.add(new QuestEvent(action, args, player, quest));
	}

	/**
	 * Triggers the quest action
	 */
	public void handleAction(QuestAction action, Object arg, Player player) {
		handleAction(action, new Object[] { arg }, player);
	}

	/**
	 * Triggers the quest action if necessary
	 */
	public boolean handleNpcTalk(Npc npc, Player player) {
		boolean handled = false;

		for (Quest quest : quests) {
			if (quest.isNpcAssociated(npc.getID(), player)) {
				handleAction(QuestAction.TALKED_NPC, npc, player);
				handled = true;
			}
		}

		return handled;
	}

	/**
	 * Triggers the quest action if necessary
	 */
	public boolean handleNpcAttack(Npc npc, Player player) {
		boolean handled = false;

		for (Quest quest : quests) {
			if (quest.isNpcAssociated(npc.getID(), player)) {
				handleAction(QuestAction.ATTACKED_NPC, npc, player);
				handled = true;
			}
		}

		return handled;
	}

	/**
	 * Triggers the quest action if necessary
	 */
	public boolean handleItemPickup(InvItem item, Player player) {
		boolean handled = false;

		for (Quest quest : quests) {
			if (quest.isItemAssociated(item.getID(), player)) {
				handleAction(QuestAction.ITEM_PICKED_UP, item, player);
				handled = true;
			}
		}

		return handled;
	}

	/**
	 * Triggers the quest action if necessary
	 */
	public boolean handleUseItemOnObject(InvItem item, GameObject object,
			Player player) {
		boolean handled = false;

		for (Quest quest : quests) {
			Logger.println("Trying " + quest.getName());

			if (quest.isObjectAssociated(object, player)) {
				Logger.println("     Object " + object.getID()
						+ " is associated");
				handleAction(QuestAction.ITEM_USED_ON_OBJECT, new Object[] {
						item, object }, player);
				handled = true;
			}
		}

		return handled;
	}

	/**
	 * Triggers the quest action if necessary
	 */
	public boolean handleUseItemOnItem(InvItem item1, InvItem item2,
			Player player) {
		boolean handled = false;

		for (Quest quest : quests) {
			if (quest.isItemAssociated(item1.getID(), player)
					|| quest.isItemAssociated(item2.getID(), player)) {
				handleAction(QuestAction.ITEM_USED_ON_ITEM, new Object[] {
						item1, item2 }, player);
				handled = true;
			}
		}

		return handled;
	}

	/**
	 * Triggers the quest action if necessary
	 */
	public boolean handleNpcKilled(Npc npc, Player player) {
		boolean handled = false;

		for (Quest quest : quests) {
			if (quest.isNpcAssociated(npc.getID(), player)) {
				handleAction(QuestAction.KILLED_NPC, npc, player);
				handled = true;
			}
		}

		return handled;
	}

	/**
	 * Triggers the quest action if necessary
	 */
	public boolean handleUseItem(InvItem item, Player player) {
		boolean handled = false;

		for (Quest quest : quests) {
			if (quest.isItemAssociated(item.getID(), player)) {
				handleAction(QuestAction.USED_ITEM, item, player);
				handled = true;
			}
		}

		return handled;
	}

	/**
	 * Triggers the quest action if necessary
	 */
	public boolean handleObject(GameObject object, Player player, boolean click) {
		boolean handled = false;

		for (Quest quest : quests) {
			if (quest.isObjectAssociated(object, player)) {
				handleAction(QuestAction.USED_OBJECT, new Object[] { object,
						click }, player);
				handled = true;
			}
		}

		return handled;
	}

	/**
	 * @return if the given npc is associated with a quest
	 */
	public boolean isNpcAssociated(Mob mob, Player player) {
		if (!(mob instanceof Npc))
			return false;

		Npc npc = (Npc) mob;
		boolean associated = false;

		for (Quest quest : quests) {
			if (quest.isNpcAssociated(npc.getID(), player))
				associated = true;
		}

		return associated;
	}

	/**
	 * @return if the given npc is visible
	 */
	public boolean isNpcVisible(Npc npc, Player player) {
		for (Quest quest : quests) {
			if (quest.isNpcAssociated(npc.getID(), player))
				return quest.isNpcVisible(npc, player);
		}

		return true;
	}

	/**
	 * @return if the given npc is shootable
	 */
	public boolean isNpcShootable(Npc npc, Player player) {
		for (Quest quest : quests) {
			if (quest.isNpcAssociated(npc.getID(), player))
				return quest.isNpcShootable(npc, player);
		}

		return true;
	}

	/**
	 * @return if the given item is visible
	 */
	public boolean isItemVisible(Item item, Player player) {
		for (Quest quest : quests) {
			if (quest.isItemAssociated(item.getID(), player))
				return quest.isItemVisible(item, player);
		}

		return true;
	}
}