package org.moparscape.msc.gs.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moparscape.msc.gs.external.*;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.model.Shop;
import org.moparscape.msc.gs.model.TelePoint;
import org.moparscape.msc.gs.npchandler.NpcHandlerDef;
import org.moparscape.msc.gs.phandler.PacketHandlerDef;

/**
 * Any retrieval of unchanging data should be done through this interface.
 * 
 * @author Joe Pritzel
 * 
 */
public interface DataStore {
	public PacketHandlerDef[] loadPacketHandlerDefs();

	public PacketHandlerDef[] loadLSPacketHandlerDefs();

	public NpcHandlerDef[] loadNpcHandlers();

	public Map<Point, TelePoint> loadTelePoints();

	public List<Shop> loadShops();

	public Map<Integer, CerterDef> loadCerterDefs();

	public List<GameObjectLoc> loadGameObjectLocs();

	public List<ItemLoc> loadItemLocs();

	public List<NPCLoc> loadNPCLocs();

	public TileDef[] loadTileDefs();

	public GameObjectDef[] loadGameObjectDefs();

	public DoorDef[] loadDoorDefs();

	public ItemDef[] loadItemDefs();

	public PrayerDef[] loadPrayerDefs();

	public SpellDef[] loadSpellDefs();

	public NPCDef[] loadNPCDefs();

	public ItemCraftingDef[] loadItemCraftingDefs();

	public ItemHerbSecond[] loadItemHerbSeconds();

	public Map<Integer, ItemDartTipDef> loadItemDartTipDefs();

	public Map<Integer, ItemGemDef> loadGemDefs();

	public Map<Integer, ItemLogCutDef> loadItemLogCutDefs();

	public Map<Integer, ItemBowStringDef> loadItemBowStringDefs();

	public Map<Integer, ItemArrowHeadDef> loadItemArrowHeadDefs();

	public Map<Integer, FiremakingDef> loadFiremakingDefs();

	public Map<Integer, int[]> loadItemAffectedTypes();

	public Map<Integer, ItemWieldableDef> loadItemWieldableDefs();

	public Map<Integer, ItemUnIdentHerbDef> loadItemUnIdentHerbDefs();

	public Map<Integer, ItemHerbDef> loadItemHerbDefs();

	public Map<Integer, Integer> loadItemEdibleHeals();

	public Map<Integer, ItemCookingDef> loadItemCookingDefs();

	public Map<Integer, ItemSmeltingDef> loadItemSmeltingDefs();

	public ItemSmithingDef[] loadItemSmithingDefs();

	public Map<Integer, ObjectMiningDef> loadObjectMiningDefs();

	public Map<Integer, ObjectWoodcuttingDef> loadObjectWoodcuttingDefs();

	public Map<Integer, ObjectFishingDef[]> loadObjectFishDefs();

	public Map<Integer, Integer> loadSpellAgressiveLevel();

	public Map<Integer, AgilityDef> loadAgilityDefs();

	public Map<Integer, AgilityCourseDef> loadAgilityCourseDefs();

	public List<InvItem>[] loadKeyChestLoots();

	public HashMap<Integer, ItemDartTipDef> loadDartTips();

	public void dispose();

}
