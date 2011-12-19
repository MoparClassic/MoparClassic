package org.moparscape.msc.gs.external;

import java.util.List;
import java.util.Map;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.model.TelePoint;
import org.moparscape.msc.gs.persistence.DataStore;

/**
 * This class handles the loading of entities from the conf files, and provides
 * methods for relaying these entities to the user.
 */
public class EntityHandler {
	private static Map<Integer, AgilityCourseDef> agilityCourses;
	private static Map<Integer, AgilityDef> agilityObjects;
	private static Map<Integer, ItemArrowHeadDef> arrowHeads;
	private static Map<Integer, ItemBowStringDef> bowString;
	private static Map<Integer, CerterDef> certers;
	private static Map<Integer, ItemDartTipDef> dartTips;

	private static DoorDef[] doors;
	private static Map<Integer, FiremakingDef> firemaking;
	private static GameObjectDef[] gameObjects;
	private static Map<Integer, ItemGemDef> gems;
	private static ItemHerbSecond[] herbSeconds;
	private static Map<Integer, int[]> itemAffectedTypes;
	private static Map<Integer, ItemCookingDef> itemCooking;
	private static ItemCraftingDef[] itemCrafting;
	private static Map<Integer, Integer> itemEdibleHeals;
	private static Map<Integer, ItemHerbDef> itemHerb;
	private static ItemDef[] items;
	private static Map<Integer, ItemSmeltingDef> itemSmelting;
	private static ItemSmithingDef[] itemSmithing;
	private static Map<Integer, ItemUnIdentHerbDef> itemUnIdentHerb;
	private static Map<Integer, ItemWieldableDef> itemWieldable;
	private static List<InvItem>[] keyChestLoots;
	private static Map<Integer, ItemLogCutDef> logCut;

	private static NPCDef[] npcs;
	private static Map<Integer, ObjectFishingDef[]> objectFishing;

	private static Map<Integer, ObjectMiningDef> objectMining;
	private static Map<Point, TelePoint> objectTelePoints;
	private static Map<Integer, ObjectWoodcuttingDef> objectWoodcutting;
	private static PrayerDef[] prayers;
	private static Map<Integer, Integer> spellAggressiveLvl;
	private static SpellDef[] spells;
	private static TileDef[] tiles;

	static {
		DataStore dataStore = Instance.getDataStore();
		try {
			doors = dataStore.loadDoorDefs();
			gameObjects = dataStore.loadGameObjectDefs();
			npcs = dataStore.loadNPCDefs();
			for (NPCDef n : npcs) {
				if (n.isAttackable()) {
					n.respawnTime -= (n.respawnTime / 3);
				}
			}
			prayers = dataStore.loadPrayerDefs();
			items = dataStore.loadItemDefs();
			spells = dataStore.loadSpellDefs();
			tiles = dataStore.loadTileDefs();
			keyChestLoots = dataStore.loadKeyChestLoots();
			herbSeconds = dataStore.loadItemHerbSeconds();
			dartTips = dataStore.loadDartTips();
			gems = dataStore.loadGemDefs();
			logCut = dataStore.loadItemLogCutDefs();
			bowString = dataStore.loadItemBowStringDefs();
			arrowHeads = dataStore.loadItemArrowHeadDefs();
			firemaking = dataStore.loadFiremakingDefs();
			itemAffectedTypes = dataStore.loadItemAffectedTypes();
			itemWieldable = dataStore.loadItemWieldableDefs();
			itemUnIdentHerb = dataStore.loadItemUnIdentHerbDefs();
			itemHerb = dataStore.loadItemHerbDefs();
			itemEdibleHeals = dataStore.loadItemEdibleHeals();
			itemCooking = dataStore.loadItemCookingDefs();
			itemSmelting = dataStore.loadItemSmeltingDefs();
			itemSmithing = dataStore.loadItemSmithingDefs();
			itemCrafting = dataStore.loadItemCraftingDefs();
			objectMining = dataStore.loadObjectMiningDefs();
			objectWoodcutting = dataStore.loadObjectWoodcuttingDefs();
			objectFishing = dataStore.loadObjectFishDefs();
			spellAggressiveLvl = dataStore.loadSpellAgressiveLevel();
			objectTelePoints = dataStore.loadTelePoints();
			certers = dataStore.loadCerterDefs();
			agilityObjects = dataStore.loadAgilityDefs();
			agilityCourses = dataStore.loadAgilityCourseDefs();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		dataStore.dispose();
	}

	/**
	 * @param id
	 *            the agility courses's start ID
	 * @return the AgilityCourseDef with the given start ID
	 */
	public static AgilityCourseDef getAgilityCourseDef(int id) {
		return agilityCourses.get(id);
	}

	/**
	 * @param id
	 *            the agility object's ID
	 * @return the AgilityDef with the given object ID
	 */
	public static AgilityDef getAgilityDef(int id) {
		return agilityObjects.get(id);
	}

	/**
	 * @param id
	 *            the npcs ID
	 * @return the CerterDef for the given npc
	 */
	public static CerterDef getCerterDef(int id) {
		return certers.get(id);
	}

	/**
	 * @return the ItemCraftingDef for the requested item
	 */
	public static ItemCraftingDef getCraftingDef(int id) {
		if (id < 0 || id >= itemCrafting.length) {
			return null;
		}
		return itemCrafting[id];
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the DoorDef with the given ID
	 */
	public static DoorDef getDoorDef(int id) {
		if (id < 0 || id >= doors.length) {
			return null;
		}
		return doors[id];
	}

	/**
	 * @return the FiremakingDef for the given log
	 */
	public static FiremakingDef getFiremakingDef(int id) {
		return firemaking.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the GameObjectDef with the given ID
	 */
	public static GameObjectDef getGameObjectDef(int id) {
		if (id < 0 || id >= gameObjects.length) {
			return null;
		}
		return gameObjects[id];
	}

	/**
	 * @param the
	 *            items type
	 * @return the types of items affected
	 */
	public static int[] getItemAffectedTypes(int type) {
		return itemAffectedTypes.get(type);
	}

	/**
	 * @return the ItemArrowHeadDef for the given arrow
	 */
	public static ItemArrowHeadDef getItemArrowHeadDef(int id) {
		return arrowHeads.get(id);
	}

	/**
	 * @return the ItemBowStringDef for the given bow
	 */
	public static ItemBowStringDef getItemBowStringDef(int id) {
		return bowString.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ItemCookingDef with the given ID
	 */
	public static ItemCookingDef getItemCookingDef(int id) {
		return itemCooking.get(id);
	}

	/**
	 * @return the ItemDartTipDef for the given tip
	 */
	public static ItemDartTipDef getItemDartTipDef(int id) {
		return dartTips.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ItemDef with the given ID
	 */
	public static ItemDef getItemDef(int id) {
		if (id < 0 || id >= items.length) {
			return null;
		}
		return items[id];
	}

	/**
	 * @param the
	 *            items id
	 * @return the amount eating the item should heal
	 */
	public static int getItemEdibleHeals(int id) {
		Integer heals = itemEdibleHeals.get(id);
		if (heals != null) {
			return heals.intValue();
		}
		return 0;
	}

	/**
	 * @return the ItemGemDef for the given gem
	 */
	public static ItemGemDef getItemGemDef(int id) {
		return gems.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ItemHerbDef with the given ID
	 */
	public static ItemHerbDef getItemHerbDef(int id) {
		return itemHerb.get(id);
	}

	/**
	 * @return the ItemHerbSecond for the given second ingredient
	 */
	public static ItemHerbSecond getItemHerbSecond(int secondID,
			int unfinishedID) {
		for (ItemHerbSecond def : herbSeconds) {
			if (def.getSecondID() == secondID
					&& def.getUnfinishedID() == unfinishedID) {
				return def;
			}
		}
		return null;
	}

	/**
	 * @return the ItemLogCutDef for the given log
	 */
	public static ItemLogCutDef getItemLogCutDef(int id) {
		return logCut.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ItemSmeltingDef with the given ID
	 */
	public static ItemSmeltingDef getItemSmeltingDef(int id) {
		return itemSmelting.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ItemUnIdentHerbDef with the given ID
	 */
	public static ItemUnIdentHerbDef getItemUnIdentHerbDef(int id) {
		return itemUnIdentHerb.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ItemWieldableDef with the given ID
	 */
	public static ItemWieldableDef getItemWieldableDef(int id) {
		return itemWieldable.get(id);
	}

	public static List<?>[] getKeyChestLoots() {
		return keyChestLoots;
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the NPCDef with the given ID
	 */
	public static NPCDef getNpcDef(int id) {
		if (id < 0 || id >= npcs.length) {
			return null;
		}
		return npcs[id];
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ObjectFishingDef with the given ID
	 */
	public static ObjectFishingDef getObjectFishingDef(int id, int click) {
		ObjectFishingDef[] defs = objectFishing.get(id);
		if (defs == null) {
			return null;
		}
		return defs[click];
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ObjectMiningDef with the given ID
	 */
	public static ObjectMiningDef getObjectMiningDef(int id) {
		return objectMining.get(id);
	}

	/**
	 * @param the
	 *            point we are currently at
	 * @return the point we should be teleported to
	 */
	public static Point getObjectTelePoint(Point location, String command) {
		TelePoint point = objectTelePoints.get(location);
		if (point == null) {
			return null;
		}
		if (command == null || point.getCommand().equalsIgnoreCase(command)) {
			return point;
		}
		return null;
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ObjectWoodcuttingDef with the given ID
	 */
	public static ObjectWoodcuttingDef getObjectWoodcuttingDef(int id) {
		return objectWoodcutting.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the PrayerDef with the given ID
	 */
	public static PrayerDef getPrayerDef(int id) {
		if (id < 0 || id >= prayers.length) {
			return null;
		}
		return prayers[id];
	}

	/**
	 * @return the ItemSmithingDef for the requested item
	 */
	public static ItemSmithingDef getSmithingDef(int id) {
		if (id < 0 || id >= itemSmithing.length) {
			return null;
		}
		return itemSmithing[id];
	}

	/**
	 * @return the ItemSmithingDef for the requested item
	 */
	public static ItemSmithingDef getSmithingDefbyID(int itemID) {
		for (ItemSmithingDef i : itemSmithing) {
			if (i.itemID == itemID)
				return i;
		}
		return null;
	}

	/**
	 * @param the
	 *            spells id
	 * @return the lvl of the spell (for calculating what it hits)
	 */
	public static int getSpellAggressiveLvl(int id) {
		Integer lvl = spellAggressiveLvl.get(id);
		if (lvl != null) {
			return lvl.intValue();
		}
		return 0;
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the SpellDef with the given ID
	 */
	public static SpellDef getSpellDef(int id) {
		if (id < 0 || id >= spells.length) {
			return null;
		}
		return spells[id];
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the TileDef with the given ID
	 */
	public static TileDef getTileDef(int id) {
		if (id < 0 || id >= tiles.length) {
			return null;
		}
		return tiles[id];
	}
}
