package org.moparscape.msc.gs.persistence;

import java.util.List;
import java.util.Map;

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
import org.moparscape.msc.gs.phandler.PacketHandlerDef;

/**
 * Any retrieval of unchanging data should be done through this interface. All
 * implementations should be only accessable by the
 * org.moparscape.msc.gs.persistence.impl package.<br>
 * Implementations should also use JCIP annotations to specify their degree of
 * thread saftey.
 * 
 * @author Joe Pritzel
 * 
 */
public abstract interface DataStore {

	public PacketHandlerDef[] loadPacketHandlerDefs() throws Exception;

	public void savePacketHandlerDefs(PacketHandlerDef[] defs) throws Exception;

	public PacketHandlerDef[] loadLSPacketHandlerDefs() throws Exception;

	public void saveLSPacketHandlerDefs(PacketHandlerDef[] defs)
			throws Exception;

	public Map<Point, TelePoint> loadTelePoints() throws Exception;

	public void saveTelePoints(Map<Point, TelePoint> points) throws Exception;

	public List<ShopDef> loadShops() throws Exception;

	public void saveShops(List<ShopDef> shops) throws Exception;

	public Map<Integer, CerterDef> loadCerterDefs() throws Exception;

	public void saveCerterDefs(Map<Integer, CerterDef> certers)
			throws Exception;

	public List<GameObjectLoc> loadGameObjectLocs() throws Exception;

	public void saveGameObjectLocs(List<GameObjectLoc> locs) throws Exception;

	public List<ItemLoc> loadItemLocs() throws Exception;

	public void saveItemLocs(List<ItemLoc> locs) throws Exception;

	public List<NPCLoc> loadNPCLocs() throws Exception;

	public void saveNPCLocs(List<NPCLoc> locs) throws Exception;

	public TileDef[] loadTileDefs() throws Exception;

	public void saveTileDefs(TileDef[] defs) throws Exception;

	public GameObjectDef[] loadGameObjectDefs() throws Exception;

	public void saveGameObjectDefs(GameObjectDef[] defs) throws Exception;

	public DoorDef[] loadDoorDefs() throws Exception;

	public void saveDoorDefs(DoorDef[] defs) throws Exception;

	public ItemDef[] loadItemDefs() throws Exception;

	public void saveItemDefs(ItemDef[] defs) throws Exception;

	public PrayerDef[] loadPrayerDefs() throws Exception;

	public void savePrayerDefs(PrayerDef[] defs) throws Exception;

	public SpellDef[] loadSpellDefs() throws Exception;

	public void saveSpellDefs(SpellDef[] defs) throws Exception;

	public NPCDef[] loadNPCDefs() throws Exception;

	public void saveNPCDefs(NPCDef[] defs) throws Exception;

	public ItemCraftingDef[] loadItemCraftingDefs() throws Exception;

	public void saveItemCraftingDefs(ItemCraftingDef[] defs) throws Exception;

	public ItemHerbSecondDef[] loadItemHerbSeconds() throws Exception;

	public void saveItemHerbSeconds(ItemHerbSecondDef[] seconds)
			throws Exception;

	public Map<Integer, ItemDartTipDef> loadItemDartTipDefs() throws Exception;

	public void saveItemDartTipDefs(Map<Integer, ItemDartTipDef> defs)
			throws Exception;

	public Map<Integer, ItemGemDef> loadGemDefs() throws Exception;

	public void saveGemDefs(Map<Integer, ItemGemDef> defs) throws Exception;

	public Map<Integer, ItemLogCutDef> loadItemLogCutDefs() throws Exception;

	public void saveItemLogCutDefs(Map<Integer, ItemLogCutDef> defs)
			throws Exception;

	public Map<Integer, ItemBowStringDef> loadItemBowStringDefs()
			throws Exception;

	public void saveItemBowStringDefs(Map<Integer, ItemBowStringDef> defs)
			throws Exception;

	public Map<Integer, ItemArrowHeadDef> loadItemArrowHeadDefs()
			throws Exception;

	public void saveItemArrowHeadDefs(Map<Integer, ItemArrowHeadDef> defs)
			throws Exception;

	public Map<Integer, FiremakingDef> loadFiremakingDefs() throws Exception;

	public void saveFiremakingDefs(Map<Integer, FiremakingDef> defs)
			throws Exception;

	public Map<Integer, int[]> loadItemAffectedTypes() throws Exception;

	public void saveItemAffectedTypes(Map<Integer, int[]> types)
			throws Exception;

	public Map<Integer, ItemWieldableDef> loadItemWieldableDefs()
			throws Exception;

	public void saveItemWieldableDefs(Map<Integer, ItemWieldableDef> defs)
			throws Exception;

	public Map<Integer, ItemUnIdentHerbDef> loadItemUnIdentHerbDefs()
			throws Exception;

	public void saveItemUnIdentHerbDefs(Map<Integer, ItemUnIdentHerbDef> defs)
			throws Exception;

	public Map<Integer, ItemHerbDef> loadItemHerbDefs() throws Exception;

	public void saveItemHerbDefs(Map<Integer, ItemHerbDef> defs)
			throws Exception;

	public Map<Integer, Integer> loadItemEdibleHeals() throws Exception;

	public void saveItemEdibleHeals(Map<Integer, Integer> defs)
			throws Exception;

	public Map<Integer, ItemCookingDef> loadItemCookingDefs() throws Exception;

	public void saveItemCookingDefs(Map<Integer, ItemCookingDef> defs)
			throws Exception;

	public Map<Integer, ItemSmeltingDef> loadItemSmeltingDefs()
			throws Exception;

	public void saveItemSmeltingDefs(Map<Integer, ItemSmeltingDef> defs)
			throws Exception;

	public ItemSmithingDef[] loadItemSmithingDefs() throws Exception;

	public void saveItemSmithingDefs(ItemSmithingDef[] defs) throws Exception;

	public Map<Integer, ObjectMiningDef> loadObjectMiningDefs()
			throws Exception;

	public void saveObjectMiningDefs(Map<Integer, ObjectMiningDef> defs)
			throws Exception;

	public Map<Integer, ObjectWoodcuttingDef> loadObjectWoodcuttingDefs()
			throws Exception;

	public void saveObjectWoodcuttingDefs(
			Map<Integer, ObjectWoodcuttingDef> defs) throws Exception;

	public Map<Integer, ObjectFishingDef[]> loadObjectFishDefs()
			throws Exception;

	public void saveObjectFishingDefs(Map<Integer, ObjectFishingDef> defs)
			throws Exception;

	public Map<Integer, Integer> loadSpellAgressiveLevel() throws Exception;

	public void saveSpellAgressiveLevel(Map<Integer, Integer> defs)
			throws Exception;

	public Map<Integer, AgilityDef> loadAgilityDefs() throws Exception;

	public void saveAgilityDefs(Map<Integer, AgilityDef> defs) throws Exception;

	public Map<Integer, AgilityCourseDef> loadAgilityCourseDefs()
			throws Exception;

	public void saveAgilityCourseDef(Map<Integer, AgilityCourseDef> defs)
			throws Exception;

	public List<InvItem>[] loadKeyChestLoots() throws Exception;

	public void saveKeyChestLoots(List<InvItem>[] loots) throws Exception;

	public void dispose();

}
