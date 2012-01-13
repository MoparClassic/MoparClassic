package org.moparscape.msc.gs.persistence.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.jcip.annotations.NotThreadSafe;

import org.moparscape.msc.config.Config;
import org.moparscape.msc.gs.external.*;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.model.Shop;
import org.moparscape.msc.gs.model.TelePoint;
import org.moparscape.msc.gs.model.definition.entity.GameObjectDefinition;
import org.moparscape.msc.gs.model.definition.entity.GameObjectLocationDefinition;
import org.moparscape.msc.gs.model.definition.entity.ItemDefinition;
import org.moparscape.msc.gs.model.definition.entity.ItemLocationDefinition;
import org.moparscape.msc.gs.model.definition.entity.NPCDefinition;
import org.moparscape.msc.gs.model.definition.entity.NPCLocationDefinition;
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
import org.moparscape.msc.gs.npchandler.NpcHandlerDefinition;
import org.moparscape.msc.gs.persistence.DataStore;
import org.moparscape.msc.gs.phandler.PacketHandlerDefinition;
import org.moparscape.msc.gs.util.Logger;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * A DataStore that parses XML using XStream.
 * 
 * @author Joe Pritzel
 * 
 */
@NotThreadSafe
@SuppressWarnings("unchecked")
public class XMLUsingXStream implements DataStore {

	protected XMLUsingXStream() {
		// To conform to the contract specified by the DataStore interface.
	}

	private static final XStream xstream = new XStream();

	static {
		try {
			setupAliases();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object load(String filename) {
		filename = filename.replaceAll(Pattern.quote("/"),
				Matcher.quoteReplacement(File.separator));
		try {
			InputStream is = new FileInputStream(new File(Config.CONF_DIR,
					filename));
			if (filename.endsWith(".gz")) {
				is = new GZIPInputStream(is);
			}
			Object rv = xstream.fromXML(is);
			return rv;
		} catch (IOException ioe) {
			Logger.error("Filename = " + filename);
			Logger.error(ioe);
		}
		return null;
	}

	public static void setupAliases() {
		try {
			Properties aliases = new Properties();
			FileInputStream fis = new FileInputStream(new File(Config.CONF_DIR,
					"aliases.xml"));
			aliases.loadFromXML(fis);
			for (Enumeration<?> e = aliases.propertyNames(); e
					.hasMoreElements();) {
				String alias = (String) e.nextElement();
				Class<?> c = Class.forName((String) aliases.get(alias));
				xstream.alias(alias, c);
			}
		} catch (Exception ioe) {
			Logger.error(ioe);
		}
	}

	public static void write(String filename, Object o) {
		filename = filename.replaceAll(Pattern.quote("/"),
				Matcher.quoteReplacement(File.separator));
		try {
			OutputStream os = new FileOutputStream(new File(Config.CONF_DIR,
					filename));
			if (filename.endsWith(".gz")) {
				os = new GZIPOutputStream(os);
			}
			xstream.toXML(o, os);
		} catch (IOException ioe) {
			Logger.error(ioe);
		}
	}

	@Override
	public PacketHandlerDefinition[] loadPacketHandlerDefs() {
		return (PacketHandlerDefinition[]) load("PacketHandlers.xml");
	}

	@Override
	public PacketHandlerDefinition[] loadLSPacketHandlerDefs() {
		return (PacketHandlerDefinition[]) load("LSPacketHandlers.xml");
	}

	@Override
	public NpcHandlerDefinition[] loadNpcHandlers() {
		return (NpcHandlerDefinition[]) load("NpcHandlers.xml");
	}

	@Override
	public Map<Point, TelePoint> loadTelePoints() {
		return (Map<Point, TelePoint>) load("locs/extras/ObjectTelePoints.xml.gz");
	}

	@Override
	public List<Shop> loadShops() {
		return (List<Shop>) load("locs/Shops.xml.gz");
	}

	@Override
	public Map<Integer, CerterDefinition> loadCerterDefs() {
		return (Map<Integer, CerterDefinition>) load("defs/extras/NpcCerters.xml.gz");
	}

	@Override
	public List<GameObjectLocationDefinition> loadGameObjectLocs() {
		return (List<GameObjectLocationDefinition>) load("locs/GameObjectLoc.xml.gz");
	}

	@Override
	public List<ItemLocationDefinition> loadItemLocs() {
		return (List<ItemLocationDefinition>) load("locs/ItemLoc.xml.gz");
	}

	@Override
	public List<NPCLocationDefinition> loadNPCLocs() {
		return (List<NPCLocationDefinition>) load("locs/NpcLoc.xml.gz");
	}

	@Override
	public TileDefinition[] loadTileDefs() {
		return (TileDefinition[]) load("defs/TileDef.xml.gz");
	}

	@Override
	public GameObjectDefinition[] loadGameObjectDefs() {
		return (GameObjectDefinition[]) load("defs/GameObjectDef.xml.gz");
	}

	@Override
	public DoorDefinition[] loadDoorDefs() {
		return (DoorDefinition[]) load("defs/DoorDef.xml.gz");
	}

	@Override
	public ItemDefinition[] loadItemDefs() {
		return (ItemDefinition[]) load("defs/ItemDef.xml.gz");
	}

	@Override
	public PrayerDefinition[] loadPrayerDefs() {
		return (PrayerDefinition[]) load("defs/PrayerDef.xml.gz");
	}

	@Override
	public SpellDefinition[] loadSpellDefs() {
		return (SpellDefinition[]) load("defs/SpellDef.xml.gz");
	}

	@Override
	public NPCDefinition[] loadNPCDefs() {
		return (NPCDefinition[]) load("defs/NPCDef.xml.gz");
	}

	@Override
	public ItemCraftingDefinition[] loadItemCraftingDefs() {
		return (ItemCraftingDefinition[]) load("defs/extras/ItemCraftingDef.xml.gz");
	}

	@Override
	public ItemHerbSecondDefinition[] loadItemHerbSeconds() {
		return (ItemHerbSecondDefinition[]) load("defs/extras/ItemHerbSecond.xml.gz");
	}

	@Override
	public Map<Integer, ItemDartTipDefinition> loadItemDartTipDefs() {
		return (Map<Integer, ItemDartTipDefinition>) load("defs/extras/ItemDartTipDef.xml.gz");
	}

	@Override
	public Map<Integer, ItemGemDefinition> loadGemDefs() {
		return (Map<Integer, ItemGemDefinition>) load("defs/extras/ItemGemDef.xml.gz");
	}

	@Override
	public Map<Integer, ItemLogCutDefinition> loadItemLogCutDefs() {
		return (Map<Integer, ItemLogCutDefinition>) load("defs/extras/ItemLogCutDef.xml.gz");
	}

	@Override
	public Map<Integer, ItemBowStringDefinition> loadItemBowStringDefs() {
		return (Map<Integer, ItemBowStringDefinition>) load("defs/extras/ItemBowStringDef.xml.gz");
	}

	@Override
	public Map<Integer, ItemArrowHeadDefinition> loadItemArrowHeadDefs() {
		return (Map<Integer, ItemArrowHeadDefinition>) load("defs/extras/ItemArrowHeadDef.xml.gz");
	}

	@Override
	public Map<Integer, FiremakingDefinition> loadFiremakingDefs() {
		return (Map<Integer, FiremakingDefinition>) load("defs/extras/FiremakingDef.xml.gz");
	}

	@Override
	public Map<Integer, int[]> loadItemAffectedTypes() {
		return (Map<Integer, int[]>) load("defs/extras/ItemAffectedTypes.xml.gz");
	}

	@Override
	public Map<Integer, ItemWieldableDefinition> loadItemWieldableDefs() {
		return (Map<Integer, ItemWieldableDefinition>) load("defs/extras/ItemWieldableDef.xml.gz");
	}

	@Override
	public Map<Integer, ItemUnIdentHerbDefinition> loadItemUnIdentHerbDefs() {
		return (Map<Integer, ItemUnIdentHerbDefinition>) load("defs/extras/ItemUnIdentHerbDef.xml.gz");
	}

	@Override
	public Map<Integer, ItemHerbDefinition> loadItemHerbDefs() {
		return (Map<Integer, ItemHerbDefinition>) load("defs/extras/ItemHerbDef.xml.gz");
	}

	@Override
	public Map<Integer, Integer> loadItemEdibleHeals() {
		return (Map<Integer, Integer>) load("defs/extras/ItemEdibleHeals.xml.gz");
	}

	@Override
	public Map<Integer, ItemCookingDefinition> loadItemCookingDefs() {
		return (Map<Integer, ItemCookingDefinition>) load("defs/extras/ItemCookingDef.xml.gz");
	}

	@Override
	public Map<Integer, ItemSmeltingDefinition> loadItemSmeltingDefs() {
		return (Map<Integer, ItemSmeltingDefinition>) load("defs/extras/ItemSmeltingDef.xml.gz");
	}

	@Override
	public ItemSmithingDefinition[] loadItemSmithingDefs() {
		return (ItemSmithingDefinition[]) load("defs/extras/ItemSmithingDef.xml.gz");
	}

	@Override
	public Map<Integer, ObjectMiningDefinition> loadObjectMiningDefs() {
		return (Map<Integer, ObjectMiningDefinition>) load("defs/extras/ObjectMining.xml.gz");
	}

	@Override
	public Map<Integer, ObjectWoodcuttingDefinition> loadObjectWoodcuttingDefs() {
		return (Map<Integer, ObjectWoodcuttingDefinition>) load("defs/extras/ObjectWoodcutting.xml.gz");
	}

	@Override
	public Map<Integer, ObjectFishingDefinition[]> loadObjectFishDefs() {
		return (Map<Integer, ObjectFishingDefinition[]>) load("defs/extras/ObjectFishing.xml.gz");
	}

	@Override
	public Map<Integer, Integer> loadSpellAgressiveLevel() {
		return (Map<Integer, Integer>) load("defs/extras/SpellAggressiveLvl.xml.gz");
	}

	@Override
	public Map<Integer, AgilityDefinition> loadAgilityDefs() {
		return (Map<Integer, AgilityDefinition>) load("defs/extras/AgilityDef.xml.gz");
	}

	@Override
	public Map<Integer, AgilityCourseDefinition> loadAgilityCourseDefs() {
		return (Map<Integer, AgilityCourseDefinition>) load("defs/extras/AgilityCourseDef.xml.gz");
	}

	@Override
	public List<InvItem>[] loadKeyChestLoots() {
		return (List<InvItem>[]) load("defs/extras/KeyChestLoot.xml.gz");
	}

	@Override
	public Map<Integer, ItemDartTipDefinition> loadDartTips() {
		return (HashMap<Integer, ItemDartTipDefinition>) load("defs/extras/ItemDartTipDef.xml.gz");
	}

	@Override
	public void dispose() {

	}

	@Override
	public void savePacketHandlerDefs(PacketHandlerDefinition[] defs) throws Exception {
		write("PacketHandlers.xml", defs);
	}

	@Override
	public void saveLSPacketHandlerDefs(PacketHandlerDefinition[] defs)
			throws Exception {
		write("LSPacketHanlders.xml", defs);
	}

	@Override
	public void saveNpcHandlers(NpcHandlerDefinition[] defs) throws Exception {
		write("NpcHandlers.xml", defs);
	}

	@Override
	public void saveTelePoints(Map<Point, TelePoint> points) throws Exception {
		write("locs/extras/ObjectTelePoints.xml.gz", points);
	}

	@Override
	public void saveShops(List<Shop> shops) throws Exception {
		write("locs/Shops.xml.gz", shops);
	}

	@Override
	public void saveCerterDefs(Map<Integer, CerterDefinition> certers)
			throws Exception {
		write("defs/extras/NpcCerters.xml.gz", certers);
	}

	@Override
	public void saveGameObjectLocs(List<GameObjectLocationDefinition> locs) throws Exception {
		write("locs/GameObjectLocs.xml.gz", locs);
	}

	@Override
	public void saveItemLocs(List<ItemLocationDefinition> locs) throws Exception {
		write("locs/ItemLoc.xml.gz", locs);
	}

	@Override
	public void saveNPCLocs(List<NPCLocationDefinition> locs) throws Exception {
		write("locs/NpcLoc.xml.gz", locs);
	}

	@Override
	public void saveTileDefs(TileDefinition[] defs) throws Exception {
		write("defs/TileDef.xml.gz", defs);
	}

	@Override
	public void saveGameObjectDefs(GameObjectDefinition[] defs) throws Exception {
		write("defs/GameObjectDef.xml.gz", defs);
	}

	@Override
	public void saveDoorDefs(DoorDefinition[] defs) throws Exception {
		write("defs/DoorDef.xml.gz", defs);
	}

	@Override
	public void saveItemDefs(ItemDefinition[] defs) throws Exception {
		write("defs/ItemDef.xml.gz", defs);
	}

	@Override
	public void savePrayerDefs(PrayerDefinition[] defs) throws Exception {
		write("defs/PrayerDef.xml.gz", defs);
	}

	@Override
	public void saveSpellDefs(SpellDefinition[] defs) throws Exception {
		write("defs/SpellDef.xml.gz", defs);
	}

	@Override
	public void saveNPCDefs(NPCDefinition[] defs) throws Exception {
		write("defs/NPCDef.xml.gz", defs);
	}

	@Override
	public void saveItemCraftingDefs(ItemCraftingDefinition[] defs) throws Exception {
		write("defs/extras/ItemCraftingDef.xml.gz", defs);
	}

	@Override
	public void saveItemHerbSeconds(ItemHerbSecondDefinition[] seconds) throws Exception {
		write("defs/extras/ItemHerbSecond.xml.gz", seconds);
	}

	@Override
	public void saveItemDartTipDefs(Map<Integer, ItemDartTipDefinition> defs)
			throws Exception {
		write("defs/extras/ItemDartTipDef.xml.gz", defs);
	}

	@Override
	public void saveGemDefs(Map<Integer, ItemGemDefinition> defs) throws Exception {
		write("def/extras/ItemGemDef.xml.gz", defs);
	}

	@Override
	public void saveItemLogCutDefs(Map<Integer, ItemLogCutDefinition> defs)
			throws Exception {
		write("def/extras/ItemLogCutDefs.xml.gz", defs);
	}

	@Override
	public void saveItemBowStringDefs(Map<Integer, ItemBowStringDefinition> defs)
			throws Exception {
		write("defs/extras/ItemBowStringDef.xml.gz", defs);
	}

	@Override
	public void saveItemArrowHeadDefs(Map<Integer, ItemArrowHeadDefinition> defs)
			throws Exception {
		write("defs/extras/ItemArrowHeadDef.xml.gz", defs);
	}

	@Override
	public void saveFiremakingDefs(Map<Integer, FiremakingDefinition> defs)
			throws Exception {
		write("defs/extras/FiremakingDef.xml.gz", defs);
	}

	@Override
	public void saveItemAffectedTypes(Map<Integer, int[]> types)
			throws Exception {
		write("defs/extras/ItemAffectedTypes.xml.gz", types);
	}

	@Override
	public void saveItemWieldableDefs(Map<Integer, ItemWieldableDefinition> defs)
			throws Exception {
		write("defs/extras/ItemWieldableDef.xml.gz", defs);
	}

	@Override
	public void saveItemUnIdentHerbDefs(Map<Integer, ItemUnIdentHerbDefinition> defs)
			throws Exception {
		write("defs/extras/Item.UnIdentHerbDef.xml.gz", defs);
	}

	@Override
	public void saveItemHerbDefs(Map<Integer, ItemHerbDefinition> defs)
			throws Exception {
		write("defs/extras/ItemHerbDef.xml.gz", defs);
	}

	@Override
	public void saveItemEdibleHeals(Map<Integer, Integer> defs)
			throws Exception {
		write("defs/extras/ItemEdibleHeals.xml.gz", defs);
	}

	@Override
	public void saveItemCookingDefs(Map<Integer, ItemCookingDefinition> defs)
			throws Exception {
		write("defs/extras/ItemCookingDef.xml.gz", defs);
	}

	@Override
	public void saveItemSmeltingDefs(Map<Integer, ItemSmeltingDefinition> defs)
			throws Exception {
		write("defs/extras/ItemSmeltingDef.xml.gz", defs);
	}

	@Override
	public void saveItemSmithingDefs(ItemSmithingDefinition[] defs) throws Exception {
		write("defs/extras/ItemSmithingDef.xml.gz", defs);
	}

	@Override
	public void saveObjectMiningDefs(Map<Integer, ObjectMiningDefinition> defs)
			throws Exception {
		write("defs/extras/ObjectMiningDef.xml.gz", defs);
	}

	@Override
	public void saveObjectWoodcuttingDefs(
			Map<Integer, ObjectWoodcuttingDefinition> defs) throws Exception {
		write("defs/extras/ObjectWoodcuttingDef.xml.gz", defs);
	}

	@Override
	public void saveObjectFishingDefs(Map<Integer, ObjectFishingDefinition> defs)
			throws Exception {
		write("defs/extras/ObjectFishingDef.xml.gz", defs);
	}

	@Override
	public void saveSpellAgressiveLevel(Map<Integer, Integer> defs)
			throws Exception {
		write("defs/extras/SpellAgressiveLvl.xml.gz", defs);
	}

	@Override
	public void saveAgilityDefs(Map<Integer, AgilityDefinition> defs) throws Exception {
		write("defs/extras/AgilityDef.xml.gz", defs);
	}

	@Override
	public void saveAgilityCourseDef(Map<Integer, AgilityCourseDefinition> defs)
			throws Exception {
		write("defs/extras/AgilityCourseDef.xml.gz", defs);
	}

	@Override
	public void saveKeyChestLoots(List<InvItem>[] loots) throws Exception {
		write("defs/extras/KeyChestLoot.xml.gz", loots);
	}
}
