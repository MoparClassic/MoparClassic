package org.moparscape.msc.gs.persistence.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moparscape.msc.config.Config;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JsonDataStore implements DataStore {

	protected JsonDataStore() {
		// To conform to the contract specified by the DataStore interface.
	}

	private Map<Object, File> files = new HashMap<Object, File>();
	{
		files.put("PacketHandler", wrap("PacketHandler"));
		files.put("LSPacketHandler", wrap("LSPacketHandler"));
		files.put("ObjectTelePoint", wrap(loc(extra("ObjectTelePoint"))));
		files.put("Shop", wrap(loc("Shop")));
		files.put("NPCCerter", wrap(def(extra("NPCCerter"))));
		files.put("GameObjectLoc", wrap(loc("GameObjectLoc")));
		files.put("ItemLoc", wrap(loc("ItemLoc")));
		files.put("NPCLoc", wrap(loc("NPCLoc")));
		files.put(TileDef[].class, wrap(def("TileDef")));
		files.put(GameObjectDef[].class, wrap(def("GameObjectDef")));
		files.put(DoorDef[].class, wrap(def("DoorDef")));
		files.put(ItemDef[].class, wrap(def("ItemDef")));
		files.put(PrayerDef[].class, wrap(def("PrayerDef")));
		files.put(SpellDef[].class, wrap(def("SpellDef")));
		files.put(NPCDef[].class, wrap(def("NPCDef")));
		files.put(ItemCraftingDef[].class, wrap(def(extra("ItemCraftingDef"))));
		files.put(ItemHerbSecondDef[].class, wrap(def(extra("ItemHerbSecond"))));
		files.put("ItemDartTipDef", wrap(def(extra("ItemDartTipDef"))));
		files.put("ItemGemDef", wrap(def(extra("ItemGemDef"))));
		files.put("ItemLogCutDef", wrap(def(extra("ItemLogCutDef"))));
		files.put("ItemBowStringDef", wrap(def(extra("ItemBowStringDef"))));
		files.put("ItemArrowHeadDef", wrap(def(extra("ItemArrowHeadDef"))));
		files.put("FiremakingDef", wrap(def(extra("FiremakingDef"))));
		files.put("ItemAffectedType", wrap(def(extra("ItemAffectedType"))));
		files.put("ItemWieldableDef", wrap(def(extra("ItemWieldableDef"))));
		files.put("ItemUnidentHerbDef", wrap(def(extra("ItemUnidentHerbDef"))));
		files.put("ItemHerbDef", wrap(def(extra("ItermHerbDef"))));
		files.put("ItemEdibleHeal", wrap(def(extra("ItemEdibleHeal"))));
		files.put("ItemCookingDef", wrap(def(extra("ItemCookingDef"))));
		files.put(ItemSmithingDef[].class, wrap(def(extra("ItemSmithingDef"))));
		files.put("ItemSmeltingDef", wrap(def(extra("ItemSmeltingDef"))));
		files.put("ObjectMining", wrap(def(extra("ObjectMining"))));
		files.put("ObjectWoodcutting", wrap(def(extra("ObjectWoodcutting"))));
		files.put("ObjectFish", wrap(def(extra("ObjectFish"))));
		files.put("SpellAgressiveLevel",
				wrap(def(extra("SpellAgressiveLevel"))));
		files.put("AgilityDef", wrap(def(extra("AgilityDef"))));
		files.put("AgilityCourseDef", wrap(def(extra("AgilityCourseDef"))));
		files.put("KeyChestLoot", wrap(def(extra("KeyChestLoot"))));
	}

	private File wrap(String file) { // Shortens code and helps prevent typos
		return new File(Config.CONF_DIR, file + ".json");
	}

	private String loc(String f) { // Shortens code and helps prevent typos
		return "loc" + File.separator + f;
	}

	private String def(String f) { // Shortens code and helps prevent typos
		return "def" + File.separator + f;
	}

	private String extra(String f) { // Shortens code and helps prevent typos
		return "extra" + File.separator + f;
	}

	private Gson gson = new GsonBuilder().setPrettyPrinting()
			.excludeFieldsWithModifiers(Modifier.TRANSIENT)
			.generateNonExecutableJson().create();

	private <T> T load(Class<?> cls) throws Exception {
		return load(files.get(cls), cls);
	}

	private <T> T load(Object ident, Class<?> cls) throws Exception {
		return load(files.get(ident), cls);
	}

	@SuppressWarnings("unchecked")
	private <T> T load(Object ident, Type t) throws Exception {
		return (T) gson.fromJson(new FileReader(files.get(ident)), t);
	}

	@SuppressWarnings("unchecked")
	private <T> T load(File f, Class<?> cls) throws Exception {
		return (T) gson.fromJson(new FileReader(f), cls);
	}

	private <T> void save(T data) throws Exception {
		save(files.get(data.getClass()), data);
	}

	private <T> void save(Object ident, T data) throws Exception {
		save(files.get(ident), data);
	}

	private <T> void save(File file, T data) throws Exception {
		if (!new File(file.getParent()).exists()) {
			new File(file.getParent()).mkdirs();
		}
		FileWriter fw = new FileWriter(file);
		fw.write(gson.toJson(data));
		fw.close();
	}

	@Override
	public PacketHandlerDef[] loadPacketHandlerDefs() throws Exception {
		return load("PacketHandler", PacketHandlerDef[].class);
	}

	@Override
	public void savePacketHandlerDefs(PacketHandlerDef[] defs) throws Exception {
		save("PacketHandler", defs);
	}

	@Override
	public PacketHandlerDef[] loadLSPacketHandlerDefs() throws Exception {
		return load("LSPacketHandler", PacketHandlerDef[].class);
	}

	@Override
	public void saveLSPacketHandlerDefs(PacketHandlerDef[] defs)
			throws Exception {
		save("LSPacketHandler", defs);
	}

	@Override
	public Map<Point, TelePoint> loadTelePoints() throws Exception {
		return load("ObjectTelePoint", Map.class);
	}

	@Override
	public void saveTelePoints(Map<Point, TelePoint> points) throws Exception {
		save("ObjectTelePoint", points);
	}

	@Override
	public List<ShopDef> loadShops() throws Exception {
		return load("Shop", new TypeToken<List<ShopDef>>() {
		}.getType());
	}

	@Override
	public void saveShops(List<ShopDef> shops) throws Exception {
		save("Shop", shops);
	}

	@Override
	public Map<Integer, CerterDef> loadCerterDefs() throws Exception {
		return load("NPCCerter", Map.class);
	}

	@Override
	public void saveCerterDefs(Map<Integer, CerterDef> certers)
			throws Exception {
		save("NPCCerter", certers);
	}

	@Override
	public List<GameObjectLoc> loadGameObjectLocs() throws Exception {
		return load("GameObjectLoc", new TypeToken<List<GameObjectLoc>>() {
		}.getType());
	}

	@Override
	public void saveGameObjectLocs(List<GameObjectLoc> locs) throws Exception {
		save("GameObjectLoc", locs);
	}

	@Override
	public List<ItemLoc> loadItemLocs() throws Exception {
		return load("ItemLoc", new TypeToken<List<ItemLoc>>() {
		}.getType());
	}

	@Override
	public void saveItemLocs(List<ItemLoc> locs) throws Exception {
		save("ItemLoc", locs);
	}

	@Override
	public List<NPCLoc> loadNPCLocs() throws Exception {
		return load("NPCLoc", new TypeToken<List<NPCLoc>>() {
		}.getType());
	}

	@Override
	public void saveNPCLocs(List<NPCLoc> locs) throws Exception {
		save("NPCLoc", locs);
	}

	@Override
	public TileDef[] loadTileDefs() throws Exception {
		return load(TileDef[].class);
	}

	@Override
	public void saveTileDefs(TileDef[] defs) throws Exception {
		save(defs);
	}

	@Override
	public GameObjectDef[] loadGameObjectDefs() throws Exception {
		return load(GameObjectDef[].class);
	}

	@Override
	public void saveGameObjectDefs(GameObjectDef[] defs) throws Exception {
		save(defs);
	}

	@Override
	public DoorDef[] loadDoorDefs() throws Exception {
		return load(DoorDef[].class);
	}

	@Override
	public void saveDoorDefs(DoorDef[] defs) throws Exception {
		save(defs);
	}

	@Override
	public ItemDef[] loadItemDefs() throws Exception {
		return load(ItemDef[].class);
	}

	@Override
	public void saveItemDefs(ItemDef[] defs) throws Exception {
		save(defs);
	}

	@Override
	public PrayerDef[] loadPrayerDefs() throws Exception {
		return load(PrayerDef[].class);
	}

	@Override
	public void savePrayerDefs(PrayerDef[] defs) throws Exception {
		save(defs);
	}

	@Override
	public SpellDef[] loadSpellDefs() throws Exception {
		return load(SpellDef[].class);
	}

	@Override
	public void saveSpellDefs(SpellDef[] defs) throws Exception {
		save(defs);
	}

	@Override
	public NPCDef[] loadNPCDefs() throws Exception {
		return load(NPCDef[].class);
	}

	@Override
	public void saveNPCDefs(NPCDef[] defs) throws Exception {
		save(defs);
	}

	@Override
	public ItemCraftingDef[] loadItemCraftingDefs() throws Exception {
		return load(ItemCraftingDef[].class);
	}

	@Override
	public void saveItemCraftingDefs(ItemCraftingDef[] defs) throws Exception {
		save(defs);
	}

	@Override
	public ItemHerbSecondDef[] loadItemHerbSeconds() throws Exception {
		return load(ItemHerbSecondDef[].class);
	}

	@Override
	public void saveItemHerbSeconds(ItemHerbSecondDef[] seconds)
			throws Exception {
		save(seconds);
	}

	@Override
	public Map<Integer, ItemDartTipDef> loadItemDartTipDefs() throws Exception {
		return load("ItemDartTipDef", Map.class);
	}

	@Override
	public void saveItemDartTipDefs(Map<Integer, ItemDartTipDef> defs)
			throws Exception {
		save("ItemDartTipDef", defs);
	}

	@Override
	public Map<Integer, ItemGemDef> loadGemDefs() throws Exception {
		return load("ItemGemDef", Map.class);
	}

	@Override
	public void saveGemDefs(Map<Integer, ItemGemDef> defs) throws Exception {
		save("ItemGemDef", defs);
	}

	@Override
	public Map<Integer, ItemLogCutDef> loadItemLogCutDefs() throws Exception {
		return load("ItemLogCutDef", Map.class);
	}

	@Override
	public void saveItemLogCutDefs(Map<Integer, ItemLogCutDef> defs)
			throws Exception {
		save("ItemLogCutDef", defs);
	}

	@Override
	public Map<Integer, ItemBowStringDef> loadItemBowStringDefs()
			throws Exception {
		return load("ItemBowStringDef", Map.class);
	}

	@Override
	public void saveItemBowStringDefs(Map<Integer, ItemBowStringDef> defs)
			throws Exception {
		save("ItemBowStringDef", defs);
	}

	@Override
	public Map<Integer, ItemArrowHeadDef> loadItemArrowHeadDefs()
			throws Exception {
		return load("ItemArrowHeadDef", Map.class);
	}

	@Override
	public void saveItemArrowHeadDefs(Map<Integer, ItemArrowHeadDef> defs)
			throws Exception {
		save("ItemArrowHeadDef", defs);
	}

	@Override
	public Map<Integer, FiremakingDef> loadFiremakingDefs() throws Exception {
		return load("FiremakingDef", Map.class);
	}

	@Override
	public void saveFiremakingDefs(Map<Integer, FiremakingDef> defs)
			throws Exception {
		save("FiremakingDef", defs);
	}

	@Override
	public Map<Integer, int[]> loadItemAffectedTypes() throws Exception {
		return load("ItemAffectedType", Map.class);
	}

	@Override
	public void saveItemAffectedTypes(Map<Integer, int[]> types)
			throws Exception {
		save("ItemAffectedType", types);
	}

	@Override
	public Map<Integer, ItemWieldableDef> loadItemWieldableDefs()
			throws Exception {
		return load("ItemWieldableDef", Map.class);
	}

	@Override
	public void saveItemWieldableDefs(Map<Integer, ItemWieldableDef> defs)
			throws Exception {
		save("ItemWieldableDef", defs);
	}

	@Override
	public Map<Integer, ItemUnIdentHerbDef> loadItemUnIdentHerbDefs()
			throws Exception {
		return load("ItemUnidentHerbDef", Map.class);
	}

	@Override
	public void saveItemUnIdentHerbDefs(Map<Integer, ItemUnIdentHerbDef> defs)
			throws Exception {
		save("ItemUnidentHerbDef", defs);
	}

	@Override
	public Map<Integer, ItemHerbDef> loadItemHerbDefs() throws Exception {
		return load("ItemHerbDef", Map.class);
	}

	@Override
	public void saveItemHerbDefs(Map<Integer, ItemHerbDef> defs)
			throws Exception {
		save("ItemHerbDef", defs);
	}

	@Override
	public Map<Integer, Integer> loadItemEdibleHeals() throws Exception {
		return load("ItemEdibleHeal", Map.class);
	}

	@Override
	public void saveItemEdibleHeals(Map<Integer, Integer> defs)
			throws Exception {
		save("ItemEdibleHeal", defs);
	}

	@Override
	public Map<Integer, ItemCookingDef> loadItemCookingDefs() throws Exception {
		return load("ItemCookingDef", Map.class);
	}

	@Override
	public void saveItemCookingDefs(Map<Integer, ItemCookingDef> defs)
			throws Exception {
		save("ItemCookingDef", defs);
	}

	@Override
	public Map<Integer, ItemSmeltingDef> loadItemSmeltingDefs()
			throws Exception {
		return load("ItemSmeltingDef", Map.class);
	}

	@Override
	public void saveItemSmeltingDefs(Map<Integer, ItemSmeltingDef> defs)
			throws Exception {
		save("ItemSmeltingDef", defs);
	}

	@Override
	public ItemSmithingDef[] loadItemSmithingDefs() throws Exception {
		return load(ItemSmithingDef[].class);
	}

	@Override
	public void saveItemSmithingDefs(ItemSmithingDef[] defs) throws Exception {
		save("ItemSmithingDef", defs);
	}

	@Override
	public Map<Integer, ObjectMiningDef> loadObjectMiningDefs()
			throws Exception {
		return load("ObjectMining", Map.class);
	}

	@Override
	public void saveObjectMiningDefs(Map<Integer, ObjectMiningDef> defs)
			throws Exception {
		save("ObjectMining", defs);
	}

	@Override
	public Map<Integer, ObjectWoodcuttingDef> loadObjectWoodcuttingDefs()
			throws Exception {
		return load("ObjectWoodcutting", Map.class);
	}

	@Override
	public void saveObjectWoodcuttingDefs(
			Map<Integer, ObjectWoodcuttingDef> defs) throws Exception {
		save("ObjectWoodcutting", defs);
	}

	@Override
	public Map<Integer, ObjectFishingDef[]> loadObjectFishDefs()
			throws Exception {
		return load("ObjectFish", Map.class);
	}

	@Override
	public void saveObjectFishingDefs(Map<Integer, ObjectFishingDef> defs)
			throws Exception {
		save("ObjectFish", defs);
	}

	@Override
	public Map<Integer, Integer> loadSpellAgressiveLevel() throws Exception {
		return load("SpellAgressiveLevel", Map.class);
	}

	@Override
	public void saveSpellAgressiveLevel(Map<Integer, Integer> defs)
			throws Exception {
		save("SpellAgressiveLevel", defs);
	}

	@Override
	public Map<Integer, AgilityDef> loadAgilityDefs() throws Exception {
		return load("AgilityDef", Map.class);
	}

	@Override
	public void saveAgilityDefs(Map<Integer, AgilityDef> defs) throws Exception {
		save("AgilityDef", defs);
	}

	@Override
	public Map<Integer, AgilityCourseDef> loadAgilityCourseDefs()
			throws Exception {
		return load("AgilityCourseDef", Map.class);
	}

	@Override
	public void saveAgilityCourseDef(Map<Integer, AgilityCourseDef> defs)
			throws Exception {
		save("AgilityCourseDef", defs);
	}

	@Override
	public List<InvItem>[] loadKeyChestLoots() throws Exception {
		return load("KeyChestLoot", List[].class);
	}

	@Override
	public void saveKeyChestLoots(List<InvItem>[] loots) throws Exception {
		save("KeyChestLoot", loots);
	}

	@Override
	public void dispose() {
		gson = null;
	}

}