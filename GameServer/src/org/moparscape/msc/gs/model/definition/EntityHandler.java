package org.moparscape.msc.gs.model.definition;

import java.util.List;
import java.util.Map;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.model.TelePoint;
import org.moparscape.msc.gs.model.definition.entity.GameObjectDefinition;
import org.moparscape.msc.gs.model.definition.entity.ItemDefinition;
import org.moparscape.msc.gs.model.definition.entity.NPCDefinition;
import org.moparscape.msc.gs.model.definition.extra.CerterDefinition;
import org.moparscape.msc.gs.model.definition.extra.DoorDefinition;
import org.moparscape.msc.gs.model.definition.extra.TileDefinition;
import org.moparscape.msc.gs.model.definition.skill.AgilityCourseDefinition;
import org.moparscape.msc.gs.model.definition.skill.AgilityDefinition;
import org.moparscape.msc.gs.model.definition.skill.FiremakingDefinition;
import org.moparscape.msc.gs.model.definition.skill.ItemArrowHeadDefinition;
import org.moparscape.msc.gs.model.definition.skill.ItemBowStringDefinition;
import org.moparscape.msc.gs.model.definition.skill.ItemCookingDefinition;
import org.moparscape.msc.gs.model.definition.skill.ItemCraftingDefinition;
import org.moparscape.msc.gs.model.definition.skill.ItemDartTipDefinition;
import org.moparscape.msc.gs.model.definition.skill.ItemGemDefinition;
import org.moparscape.msc.gs.model.definition.skill.ItemHerbDefinition;
import org.moparscape.msc.gs.model.definition.skill.ItemHerbSecondDefinition;
import org.moparscape.msc.gs.model.definition.skill.ItemLogCutDefinition;
import org.moparscape.msc.gs.model.definition.skill.ItemSmeltingDefinition;
import org.moparscape.msc.gs.model.definition.skill.ItemSmithingDefinition;
import org.moparscape.msc.gs.model.definition.skill.ItemUnIdentHerbDefinition;
import org.moparscape.msc.gs.model.definition.skill.ItemWieldableDefinition;
import org.moparscape.msc.gs.model.definition.skill.ObjectFishingDefinition;
import org.moparscape.msc.gs.model.definition.skill.ObjectMiningDefinition;
import org.moparscape.msc.gs.model.definition.skill.ObjectWoodcuttingDefinition;
import org.moparscape.msc.gs.model.definition.skill.PrayerDefinition;
import org.moparscape.msc.gs.model.definition.skill.SpellDefinition;
import org.moparscape.msc.gs.persistence.DataStore;

/**
 * This class handles the loading of entities from the conf files, and provides
 * methods for relaying these entities to the user.
 */
public class EntityHandler {
	private static Map<Integer, AgilityCourseDefinition> agilityCourses;
	private static Map<Integer, AgilityDefinition> agilityObjects;
	private static Map<Integer, ItemArrowHeadDefinition> arrowHeads;
	private static Map<Integer, ItemBowStringDefinition> bowString;
	private static Map<Integer, CerterDefinition> certers;
	private static Map<Integer, ItemDartTipDefinition> dartTips;

	private static DoorDefinition[] doors;
	private static Map<Integer, FiremakingDefinition> firemaking;
	private static GameObjectDefinition[] gameObjects;
	private static Map<Integer, ItemGemDefinition> gems;
	private static ItemHerbSecondDefinition[] herbSeconds;
	private static Map<Integer, int[]> itemAffectedTypes;
	private static Map<Integer, ItemCookingDefinition> itemCooking;
	private static ItemCraftingDefinition[] itemCrafting;
	private static Map<Integer, Integer> itemEdibleHeals;
	private static Map<Integer, ItemHerbDefinition> itemHerb;
	private static ItemDefinition[] items;
	private static Map<Integer, ItemSmeltingDefinition> itemSmelting;
	private static ItemSmithingDefinition[] itemSmithing;
	private static Map<Integer, ItemUnIdentHerbDefinition> itemUnIdentHerb;
	private static Map<Integer, ItemWieldableDefinition> itemWieldable;
	private static List<InvItem>[] keyChestLoots;
	private static Map<Integer, ItemLogCutDefinition> logCut;

	private static NPCDefinition[] npcs;
	private static Map<Integer, ObjectFishingDefinition[]> objectFishing;

	private static Map<Integer, ObjectMiningDefinition> objectMining;
	private static Map<Point, TelePoint> objectTelePoints;
	private static Map<Integer, ObjectWoodcuttingDefinition> objectWoodcutting;
	private static PrayerDefinition[] prayers;
	private static Map<Integer, Integer> spellAggressiveLvl;
	private static SpellDefinition[] spells;
	private static TileDefinition[] tiles;

	static {
		DataStore dataStore = Instance.getDataStore();
		try {
			doors = dataStore.loadDoorDefs();
			gameObjects = dataStore.loadGameObjectDefs();
			npcs = dataStore.loadNPCDefs();
			for (NPCDefinition n : npcs) {
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
	public static AgilityCourseDefinition getAgilityCourseDef(int id) {
		return agilityCourses.get(id);
	}

	/**
	 * @param id
	 *            the agility object's ID
	 * @return the AgilityDef with the given object ID
	 */
	public static AgilityDefinition getAgilityDef(int id) {
		return agilityObjects.get(id);
	}

	/**
	 * @param id
	 *            the npcs ID
	 * @return the CerterDef for the given npc
	 */
	public static CerterDefinition getCerterDef(int id) {
		return certers.get(id);
	}

	/**
	 * @return the ItemCraftingDef for the requested item
	 */
	public static ItemCraftingDefinition getCraftingDef(int id) {
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
	public static DoorDefinition getDoorDef(int id) {
		if (id < 0 || id >= doors.length) {
			return null;
		}
		return doors[id];
	}

	/**
	 * @return the FiremakingDef for the given log
	 */
	public static FiremakingDefinition getFiremakingDef(int id) {
		return firemaking.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the GameObjectDef with the given ID
	 */
	public static GameObjectDefinition getGameObjectDef(int id) {
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
	public static ItemArrowHeadDefinition getItemArrowHeadDef(int id) {
		return arrowHeads.get(id);
	}

	/**
	 * @return the ItemBowStringDef for the given bow
	 */
	public static ItemBowStringDefinition getItemBowStringDef(int id) {
		return bowString.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ItemCookingDef with the given ID
	 */
	public static ItemCookingDefinition getItemCookingDef(int id) {
		return itemCooking.get(id);
	}

	/**
	 * @return the ItemDartTipDef for the given tip
	 */
	public static ItemDartTipDefinition getItemDartTipDef(int id) {
		return dartTips.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ItemDef with the given ID
	 */
	public static ItemDefinition getItemDef(int id) {
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
	public static ItemGemDefinition getItemGemDef(int id) {
		return gems.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ItemHerbDef with the given ID
	 */
	public static ItemHerbDefinition getItemHerbDef(int id) {
		return itemHerb.get(id);
	}

	/**
	 * @return the ItemHerbSecond for the given second ingredient
	 */
	public static ItemHerbSecondDefinition getItemHerbSecond(int secondID,
			int unfinishedID) {
		for (ItemHerbSecondDefinition def : herbSeconds) {
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
	public static ItemLogCutDefinition getItemLogCutDef(int id) {
		return logCut.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ItemSmeltingDef with the given ID
	 */
	public static ItemSmeltingDefinition getItemSmeltingDef(int id) {
		return itemSmelting.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ItemUnIdentHerbDef with the given ID
	 */
	public static ItemUnIdentHerbDefinition getItemUnIdentHerbDef(int id) {
		return itemUnIdentHerb.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the ItemWieldableDef with the given ID
	 */
	public static ItemWieldableDefinition getItemWieldableDef(int id) {
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
	public static NPCDefinition getNpcDef(int id) {
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
	public static ObjectFishingDefinition getObjectFishingDef(int id, int click) {
		ObjectFishingDefinition[] defs = objectFishing.get(id);
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
	public static ObjectMiningDefinition getObjectMiningDef(int id) {
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
	public static ObjectWoodcuttingDefinition getObjectWoodcuttingDef(int id) {
		return objectWoodcutting.get(id);
	}

	/**
	 * @param id
	 *            the entities ID
	 * @return the PrayerDef with the given ID
	 */
	public static PrayerDefinition getPrayerDef(int id) {
		if (id < 0 || id >= prayers.length) {
			return null;
		}
		return prayers[id];
	}

	/**
	 * @return the ItemSmithingDef for the requested item
	 */
	public static ItemSmithingDefinition getSmithingDef(int id) {
		if (id < 0 || id >= itemSmithing.length) {
			return null;
		}
		return itemSmithing[id];
	}

	/**
	 * @return the ItemSmithingDef for the requested item
	 */
	public static ItemSmithingDefinition getSmithingDefbyID(int itemID) {
		for (ItemSmithingDefinition i : itemSmithing) {
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
	public static SpellDefinition getSpellDef(int id) {
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
	public static TileDefinition getTileDef(int id) {
		if (id < 0 || id >= tiles.length) {
			return null;
		}
		return tiles[id];
	}
}
