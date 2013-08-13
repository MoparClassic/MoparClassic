package org.moparscape.msc.gs.persistence.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.jcip.annotations.NotThreadSafe;

import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.model.TelePoint;
import org.moparscape.msc.gs.model.definition.entity.GameObjectDef;
import org.moparscape.msc.gs.model.definition.entity.GameObjectLoc;
import org.moparscape.msc.gs.model.definition.entity.ItemDef;
import org.moparscape.msc.gs.model.definition.entity.ItemLoc;
import org.moparscape.msc.gs.model.definition.entity.NPCDef;
import org.moparscape.msc.gs.model.definition.entity.NPCLoc;
import org.moparscape.msc.gs.model.definition.extra.CerterDef;
import org.moparscape.msc.gs.model.definition.extra.DoorDef;
import org.moparscape.msc.gs.model.definition.extra.ShopDef;
import org.moparscape.msc.gs.model.definition.extra.TileDef;
import org.moparscape.msc.gs.model.definition.skill.AgilityCourseDef;
import org.moparscape.msc.gs.model.definition.skill.AgilityDef;
import org.moparscape.msc.gs.model.definition.skill.FiremakingDef;
import org.moparscape.msc.gs.model.definition.skill.ItemArrowHeadDef;
import org.moparscape.msc.gs.model.definition.skill.ItemBowStringDef;
import org.moparscape.msc.gs.model.definition.skill.ItemCookingDef;
import org.moparscape.msc.gs.model.definition.skill.ItemCraftingDef;
import org.moparscape.msc.gs.model.definition.skill.ItemDartTipDef;
import org.moparscape.msc.gs.model.definition.skill.ItemGemDef;
import org.moparscape.msc.gs.model.definition.skill.ItemHerbDef;
import org.moparscape.msc.gs.model.definition.skill.ItemHerbSecondDef;
import org.moparscape.msc.gs.model.definition.skill.ItemLogCutDef;
import org.moparscape.msc.gs.model.definition.skill.ItemSmeltingDef;
import org.moparscape.msc.gs.model.definition.skill.ItemSmithingDef;
import org.moparscape.msc.gs.model.definition.skill.ItemUnIdentHerbDef;
import org.moparscape.msc.gs.model.definition.skill.ItemWieldableDef;
import org.moparscape.msc.gs.model.definition.skill.ObjectFishingDef;
import org.moparscape.msc.gs.model.definition.skill.ObjectMiningDef;
import org.moparscape.msc.gs.model.definition.skill.ObjectWoodcuttingDef;
import org.moparscape.msc.gs.model.definition.skill.PrayerDef;
import org.moparscape.msc.gs.model.definition.skill.SpellDef;
import org.moparscape.msc.gs.persistence.DataStore;
import org.moparscape.msc.gs.phandler.PacketHandlerDef;
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
	public PacketHandlerDef[] loadPacketHandlerDefs() {
		return (PacketHandlerDef[]) load("PacketHandlers.xml");
	}

	@Override
	public PacketHandlerDef[] loadLSPacketHandlerDefs() {
		return (PacketHandlerDef[]) load("LSPacketHandlers.xml");
	}

	@Override
	public Map<Point, TelePoint> loadTelePoints() {
		return (Map<Point, TelePoint>) load("locs/extras/ObjectTelePoints.xml.gz");
	}

	@Override
	public List<ShopDef> loadShops() {
		return (List<ShopDef>) load("locs/Shops.xml.gz");
	}

	@Override
	public Map<Integer, CerterDef> loadCerterDefs() {
		return (Map<Integer, CerterDef>) load("defs/extras/NpcCerters.xml.gz");
	}

	@Override
	public List<GameObjectLoc> loadGameObjectLocs() {
		return (List<GameObjectLoc>) load("locs/GameObjectLoc.xml.gz");
	}

	@Override
	public List<ItemLoc> loadItemLocs() {
		return (List<ItemLoc>) load("locs/ItemLoc.xml.gz");
	}

	@Override
	public List<NPCLoc> loadNPCLocs() {
		return (List<NPCLoc>) load("locs/NpcLoc.xml.gz");
	}

	@Override
	public TileDef[] loadTileDefs() {
		return (TileDef[]) load("defs/TileDef.xml.gz");
	}

	@Override
	public GameObjectDef[] loadGameObjectDefs() {
		return (GameObjectDef[]) load("defs/GameObjectDef.xml.gz");
	}

	@Override
	public DoorDef[] loadDoorDefs() {
		return (DoorDef[]) load("defs/DoorDef.xml.gz");
	}

	@Override
	public ItemDef[] loadItemDefs() {
		return (ItemDef[]) load("defs/ItemDef.xml.gz");
	}

	@Override
	public PrayerDef[] loadPrayerDefs() {
		return (PrayerDef[]) load("defs/PrayerDef.xml.gz");
	}

	@Override
	public SpellDef[] loadSpellDefs() {
		return (SpellDef[]) load("defs/SpellDef.xml.gz");
	}

	@Override
	public NPCDef[] loadNPCDefs() {
		return (NPCDef[]) load("defs/NPCDef.xml.gz");
	}

	@Override
	public ItemCraftingDef[] loadItemCraftingDefs() {
		return (ItemCraftingDef[]) load("defs/extras/ItemCraftingDef.xml.gz");
	}

	@Override
	public ItemHerbSecondDef[] loadItemHerbSeconds() {
		return (ItemHerbSecondDef[]) load("defs/extras/ItemHerbSecond.xml.gz");
	}

	@Override
	public Map<Integer, ItemDartTipDef> loadItemDartTipDefs() {
		return (Map<Integer, ItemDartTipDef>) load("defs/extras/ItemDartTipDef.xml.gz");
	}

	@Override
	public Map<Integer, ItemGemDef> loadGemDefs() {
		return (Map<Integer, ItemGemDef>) load("defs/extras/ItemGemDef.xml.gz");
	}

	@Override
	public Map<Integer, ItemLogCutDef> loadItemLogCutDefs() {
		return (Map<Integer, ItemLogCutDef>) load("defs/extras/ItemLogCutDef.xml.gz");
	}

	@Override
	public Map<Integer, ItemBowStringDef> loadItemBowStringDefs() {
		return (Map<Integer, ItemBowStringDef>) load("defs/extras/ItemBowStringDef.xml.gz");
	}

	@Override
	public Map<Integer, ItemArrowHeadDef> loadItemArrowHeadDefs() {
		return (Map<Integer, ItemArrowHeadDef>) load("defs/extras/ItemArrowHeadDef.xml.gz");
	}

	@Override
	public Map<Integer, FiremakingDef> loadFiremakingDefs() {
		return (Map<Integer, FiremakingDef>) load("defs/extras/FiremakingDef.xml.gz");
	}

	@Override
	public Map<Integer, int[]> loadItemAffectedTypes() {
		return (Map<Integer, int[]>) load("defs/extras/ItemAffectedTypes.xml.gz");
	}

	@Override
	public Map<Integer, ItemWieldableDef> loadItemWieldableDefs() {
		return (Map<Integer, ItemWieldableDef>) load("defs/extras/ItemWieldableDef.xml.gz");
	}

	@Override
	public Map<Integer, ItemUnIdentHerbDef> loadItemUnIdentHerbDefs() {
		return (Map<Integer, ItemUnIdentHerbDef>) load("defs/extras/ItemUnIdentHerbDef.xml.gz");
	}

	@Override
	public Map<Integer, ItemHerbDef> loadItemHerbDefs() {
		return (Map<Integer, ItemHerbDef>) load("defs/extras/ItemHerbDef.xml.gz");
	}

	@Override
	public Map<Integer, Integer> loadItemEdibleHeals() {
		return (Map<Integer, Integer>) load("defs/extras/ItemEdibleHeals.xml.gz");
	}

	@Override
	public Map<Integer, ItemCookingDef> loadItemCookingDefs() {
		return (Map<Integer, ItemCookingDef>) load("defs/extras/ItemCookingDef.xml.gz");
	}

	@Override
	public Map<Integer, ItemSmeltingDef> loadItemSmeltingDefs() {
		return (Map<Integer, ItemSmeltingDef>) load("defs/extras/ItemSmeltingDef.xml.gz");
	}

	@Override
	public ItemSmithingDef[] loadItemSmithingDefs() {
		return (ItemSmithingDef[]) load("defs/extras/ItemSmithingDef.xml.gz");
	}

	@Override
	public Map<Integer, ObjectMiningDef> loadObjectMiningDefs() {
		return (Map<Integer, ObjectMiningDef>) load("defs/extras/ObjectMining.xml.gz");
	}

	@Override
	public Map<Integer, ObjectWoodcuttingDef> loadObjectWoodcuttingDefs() {
		return (Map<Integer, ObjectWoodcuttingDef>) load("defs/extras/ObjectWoodcutting.xml.gz");
	}

	@Override
	public Map<Integer, ObjectFishingDef[]> loadObjectFishDefs() {
		return (Map<Integer, ObjectFishingDef[]>) load("defs/extras/ObjectFishing.xml.gz");
	}

	@Override
	public Map<Integer, Integer> loadSpellAgressiveLevel() {
		return (Map<Integer, Integer>) load("defs/extras/SpellAggressiveLvl.xml.gz");
	}

	@Override
	public Map<Integer, AgilityDef> loadAgilityDefs() {
		return (Map<Integer, AgilityDef>) load("defs/extras/AgilityDef.xml.gz");
	}

	@Override
	public Map<Integer, AgilityCourseDef> loadAgilityCourseDefs() {
		return (Map<Integer, AgilityCourseDef>) load("defs/extras/AgilityCourseDef.xml.gz");
	}

	@Override
	public List<InvItem>[] loadKeyChestLoots() {
		return (List<InvItem>[]) load("defs/extras/KeyChestLoot.xml.gz");
	}
	
	@Override
	public void dispose() {

	}

	@Override
	public void savePacketHandlerDefs(PacketHandlerDef[] defs) throws Exception {
		write("PacketHandlers.xml", defs);
	}

	@Override
	public void saveLSPacketHandlerDefs(PacketHandlerDef[] defs)
			throws Exception {
		write("LSPacketHanlders.xml", defs);
	}

	@Override
	public void saveTelePoints(Map<Point, TelePoint> points) throws Exception {
		write("locs/extras/ObjectTelePoints.xml.gz", points);
	}

	@Override
	public void saveShops(List<ShopDef> shops) throws Exception {
		write("locs/Shops.xml.gz", shops);
	}

	@Override
	public void saveCerterDefs(Map<Integer, CerterDef> certers)
			throws Exception {
		write("defs/extras/NpcCerters.xml.gz", certers);
	}

	@Override
	public void saveGameObjectLocs(List<GameObjectLoc> locs) throws Exception {
		write("locs/GameObjectLocs.xml.gz", locs);
	}

	@Override
	public void saveItemLocs(List<ItemLoc> locs) throws Exception {
		write("locs/ItemLoc.xml.gz", locs);
	}

	@Override
	public void saveNPCLocs(List<NPCLoc> locs) throws Exception {
		write("locs/NpcLoc.xml.gz", locs);
	}

	@Override
	public void saveTileDefs(TileDef[] defs) throws Exception {
		write("defs/TileDef.xml.gz", defs);
	}

	@Override
	public void saveGameObjectDefs(GameObjectDef[] defs) throws Exception {
		write("defs/GameObjectDef.xml.gz", defs);
	}

	@Override
	public void saveDoorDefs(DoorDef[] defs) throws Exception {
		write("defs/DoorDef.xml.gz", defs);
	}

	@Override
	public void saveItemDefs(ItemDef[] defs) throws Exception {
		write("defs/ItemDef.xml.gz", defs);
	}

	@Override
	public void savePrayerDefs(PrayerDef[] defs) throws Exception {
		write("defs/PrayerDef.xml.gz", defs);
	}

	@Override
	public void saveSpellDefs(SpellDef[] defs) throws Exception {
		write("defs/SpellDef.xml.gz", defs);
	}

	@Override
	public void saveNPCDefs(NPCDef[] defs) throws Exception {
		write("defs/NPCDef.xml.gz", defs);
	}

	@Override
	public void saveItemCraftingDefs(ItemCraftingDef[] defs) throws Exception {
		write("defs/extras/ItemCraftingDef.xml.gz", defs);
	}

	@Override
	public void saveItemHerbSeconds(ItemHerbSecondDef[] seconds) throws Exception {
		write("defs/extras/ItemHerbSecond.xml.gz", seconds);
	}

	@Override
	public void saveItemDartTipDefs(Map<Integer, ItemDartTipDef> defs)
			throws Exception {
		write("defs/extras/ItemDartTipDef.xml.gz", defs);
	}

	@Override
	public void saveGemDefs(Map<Integer, ItemGemDef> defs) throws Exception {
		write("def/extras/ItemGemDef.xml.gz", defs);
	}

	@Override
	public void saveItemLogCutDefs(Map<Integer, ItemLogCutDef> defs)
			throws Exception {
		write("def/extras/ItemLogCutDefs.xml.gz", defs);
	}

	@Override
	public void saveItemBowStringDefs(Map<Integer, ItemBowStringDef> defs)
			throws Exception {
		write("defs/extras/ItemBowStringDef.xml.gz", defs);
	}

	@Override
	public void saveItemArrowHeadDefs(Map<Integer, ItemArrowHeadDef> defs)
			throws Exception {
		write("defs/extras/ItemArrowHeadDef.xml.gz", defs);
	}

	@Override
	public void saveFiremakingDefs(Map<Integer, FiremakingDef> defs)
			throws Exception {
		write("defs/extras/FiremakingDef.xml.gz", defs);
	}

	@Override
	public void saveItemAffectedTypes(Map<Integer, int[]> types)
			throws Exception {
		write("defs/extras/ItemAffectedTypes.xml.gz", types);
	}

	@Override
	public void saveItemWieldableDefs(Map<Integer, ItemWieldableDef> defs)
			throws Exception {
		write("defs/extras/ItemWieldableDef.xml.gz", defs);
	}

	@Override
	public void saveItemUnIdentHerbDefs(Map<Integer, ItemUnIdentHerbDef> defs)
			throws Exception {
		write("defs/extras/Item.UnIdentHerbDef.xml.gz", defs);
	}

	@Override
	public void saveItemHerbDefs(Map<Integer, ItemHerbDef> defs)
			throws Exception {
		write("defs/extras/ItemHerbDef.xml.gz", defs);
	}

	@Override
	public void saveItemEdibleHeals(Map<Integer, Integer> defs)
			throws Exception {
		write("defs/extras/ItemEdibleHeals.xml.gz", defs);
	}

	@Override
	public void saveItemCookingDefs(Map<Integer, ItemCookingDef> defs)
			throws Exception {
		write("defs/extras/ItemCookingDef.xml.gz", defs);
	}

	@Override
	public void saveItemSmeltingDefs(Map<Integer, ItemSmeltingDef> defs)
			throws Exception {
		write("defs/extras/ItemSmeltingDef.xml.gz", defs);
	}

	@Override
	public void saveItemSmithingDefs(ItemSmithingDef[] defs) throws Exception {
		write("defs/extras/ItemSmithingDef.xml.gz", defs);
	}

	@Override
	public void saveObjectMiningDefs(Map<Integer, ObjectMiningDef> defs)
			throws Exception {
		write("defs/extras/ObjectMiningDef.xml.gz", defs);
	}

	@Override
	public void saveObjectWoodcuttingDefs(
			Map<Integer, ObjectWoodcuttingDef> defs) throws Exception {
		write("defs/extras/ObjectWoodcuttingDef.xml.gz", defs);
	}

	@Override
	public void saveObjectFishingDefs(Map<Integer, ObjectFishingDef> defs)
			throws Exception {
		write("defs/extras/ObjectFishingDef.xml.gz", defs);
	}

	@Override
	public void saveSpellAgressiveLevel(Map<Integer, Integer> defs)
			throws Exception {
		write("defs/extras/SpellAgressiveLvl.xml.gz", defs);
	}

	@Override
	public void saveAgilityDefs(Map<Integer, AgilityDef> defs) throws Exception {
		write("defs/extras/AgilityDef.xml.gz", defs);
	}

	@Override
	public void saveAgilityCourseDef(Map<Integer, AgilityCourseDef> defs)
			throws Exception {
		write("defs/extras/AgilityCourseDef.xml.gz", defs);
	}

	@Override
	public void saveKeyChestLoots(List<InvItem>[] loots) throws Exception {
		write("defs/extras/KeyChestLoot.xml.gz", loots);
	}
}
