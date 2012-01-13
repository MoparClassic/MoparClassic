package org.moparscape.msc.gs.persistence;

import java.util.List;
import java.util.Map;

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
import org.moparscape.msc.gs.phandler.PacketHandlerDefinition;

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

	public PacketHandlerDefinition[] loadPacketHandlerDefs() throws Exception;

	public void savePacketHandlerDefs(PacketHandlerDefinition[] defs) throws Exception;

	public PacketHandlerDefinition[] loadLSPacketHandlerDefs() throws Exception;

	public void saveLSPacketHandlerDefs(PacketHandlerDefinition[] defs)
			throws Exception;

	public NpcHandlerDefinition[] loadNpcHandlers() throws Exception;

	public void saveNpcHandlers(NpcHandlerDefinition[] defs) throws Exception;

	public Map<Point, TelePoint> loadTelePoints() throws Exception;

	public void saveTelePoints(Map<Point, TelePoint> points) throws Exception;

	public List<Shop> loadShops() throws Exception;

	public void saveShops(List<Shop> shops) throws Exception;

	public Map<Integer, CerterDefinition> loadCerterDefs() throws Exception;

	public void saveCerterDefs(Map<Integer, CerterDefinition> certers)
			throws Exception;

	public List<GameObjectLocationDefinition> loadGameObjectLocs() throws Exception;

	public void saveGameObjectLocs(List<GameObjectLocationDefinition> locs) throws Exception;

	public List<ItemLocationDefinition> loadItemLocs() throws Exception;

	public void saveItemLocs(List<ItemLocationDefinition> locs) throws Exception;

	public List<NPCLocationDefinition> loadNPCLocs() throws Exception;

	public void saveNPCLocs(List<NPCLocationDefinition> locs) throws Exception;

	public TileDefinition[] loadTileDefs() throws Exception;

	public void saveTileDefs(TileDefinition[] defs) throws Exception;

	public GameObjectDefinition[] loadGameObjectDefs() throws Exception;

	public void saveGameObjectDefs(GameObjectDefinition[] defs) throws Exception;

	public DoorDefinition[] loadDoorDefs() throws Exception;

	public void saveDoorDefs(DoorDefinition[] defs) throws Exception;

	public ItemDefinition[] loadItemDefs() throws Exception;

	public void saveItemDefs(ItemDefinition[] defs) throws Exception;

	public PrayerDefinition[] loadPrayerDefs() throws Exception;

	public void savePrayerDefs(PrayerDefinition[] defs) throws Exception;

	public SpellDefinition[] loadSpellDefs() throws Exception;

	public void saveSpellDefs(SpellDefinition[] defs) throws Exception;

	public NPCDefinition[] loadNPCDefs() throws Exception;

	public void saveNPCDefs(NPCDefinition[] defs) throws Exception;

	public ItemCraftingDefinition[] loadItemCraftingDefs() throws Exception;

	public void saveItemCraftingDefs(ItemCraftingDefinition[] defs) throws Exception;

	public ItemHerbSecondDefinition[] loadItemHerbSeconds() throws Exception;

	public void saveItemHerbSeconds(ItemHerbSecondDefinition[] seconds) throws Exception;

	public Map<Integer, ItemDartTipDefinition> loadItemDartTipDefs() throws Exception;

	public void saveItemDartTipDefs(Map<Integer, ItemDartTipDefinition> defs)
			throws Exception;

	public Map<Integer, ItemGemDefinition> loadGemDefs() throws Exception;

	public void saveGemDefs(Map<Integer, ItemGemDefinition> defs) throws Exception;

	public Map<Integer, ItemLogCutDefinition> loadItemLogCutDefs() throws Exception;

	public void saveItemLogCutDefs(Map<Integer, ItemLogCutDefinition> defs)
			throws Exception;

	public Map<Integer, ItemBowStringDefinition> loadItemBowStringDefs()
			throws Exception;

	public void saveItemBowStringDefs(Map<Integer, ItemBowStringDefinition> defs)
			throws Exception;

	public Map<Integer, ItemArrowHeadDefinition> loadItemArrowHeadDefs()
			throws Exception;

	public void saveItemArrowHeadDefs(Map<Integer, ItemArrowHeadDefinition> defs)
			throws Exception;

	public Map<Integer, FiremakingDefinition> loadFiremakingDefs() throws Exception;

	public void saveFiremakingDefs(Map<Integer, FiremakingDefinition> defs)
			throws Exception;

	public Map<Integer, int[]> loadItemAffectedTypes() throws Exception;
	
	public void saveItemAffectedTypes(Map<Integer, int[]> types) throws Exception;

	public Map<Integer, ItemWieldableDefinition> loadItemWieldableDefs()
			throws Exception;
	
	public void saveItemWieldableDefs(Map<Integer, ItemWieldableDefinition> defs) throws Exception;

	public Map<Integer, ItemUnIdentHerbDefinition> loadItemUnIdentHerbDefs()
			throws Exception;
	
	public void saveItemUnIdentHerbDefs(Map<Integer, ItemUnIdentHerbDefinition> defs) throws Exception;

	public Map<Integer, ItemHerbDefinition> loadItemHerbDefs() throws Exception;
	
	public void saveItemHerbDefs(Map<Integer, ItemHerbDefinition> defs) throws Exception;

	public Map<Integer, Integer> loadItemEdibleHeals() throws Exception;
	
	public void saveItemEdibleHeals(Map<Integer, Integer> defs) throws Exception;

	public Map<Integer, ItemCookingDefinition> loadItemCookingDefs() throws Exception;
	
	public void saveItemCookingDefs(Map<Integer, ItemCookingDefinition> defs) throws Exception;

	public Map<Integer, ItemSmeltingDefinition> loadItemSmeltingDefs()
			throws Exception;
	
	public void saveItemSmeltingDefs(Map<Integer, ItemSmeltingDefinition> defs) throws Exception;

	public ItemSmithingDefinition[] loadItemSmithingDefs() throws Exception;
	
	public void saveItemSmithingDefs(ItemSmithingDefinition[] defs) throws Exception;

	public Map<Integer, ObjectMiningDefinition> loadObjectMiningDefs()
			throws Exception;

	public void saveObjectMiningDefs(Map<Integer, ObjectMiningDefinition> defs) throws Exception;
	
	public Map<Integer, ObjectWoodcuttingDefinition> loadObjectWoodcuttingDefs()
			throws Exception;
	
	public void saveObjectWoodcuttingDefs(Map<Integer, ObjectWoodcuttingDefinition> defs) throws Exception;

	public Map<Integer, ObjectFishingDefinition[]> loadObjectFishDefs()
			throws Exception;
	
	public void saveObjectFishingDefs(Map<Integer, ObjectFishingDefinition> defs) throws Exception;

	public Map<Integer, Integer> loadSpellAgressiveLevel() throws Exception;
	
	public void saveSpellAgressiveLevel(Map<Integer, Integer> defs) throws Exception;

	public Map<Integer, AgilityDefinition> loadAgilityDefs() throws Exception;
	
	public void saveAgilityDefs(Map<Integer, AgilityDefinition> defs) throws Exception;

	public Map<Integer, AgilityCourseDefinition> loadAgilityCourseDefs()
			throws Exception;
	
	public void saveAgilityCourseDef(Map<Integer, AgilityCourseDefinition> defs) throws Exception;

	public List<InvItem>[] loadKeyChestLoots() throws Exception;
	
	public void saveKeyChestLoots(List<InvItem>[] loots) throws Exception;

	public Map<Integer, ItemDartTipDefinition> loadDartTips() throws Exception;

	public void dispose();

}
