package org.moparscape.msc.gs.persistence.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.jcip.annotations.ThreadSafe;

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
import org.moparscape.msc.gs.persistence.impl.bun.CodecLookupService;
import org.moparscape.msc.gs.persistence.impl.bun.FileLookupService;
import org.moparscape.msc.gs.persistence.impl.bun.IO;
import org.moparscape.msc.gs.phandler.PacketHandlerDefinition;

@ThreadSafe
public class BinaryUsingNIO implements DataStore {
	
	protected BinaryUsingNIO() throws Exception {
		throw new Exception("Not yet implemented!");
	}

	@Override
	public PacketHandlerDefinition[] loadPacketHandlerDefs() throws Exception {
		String cls = PacketHandlerDefinition.class.getName();
		File f = FileLookupService.lookup(cls);
		return (PacketHandlerDefinition[]) CodecLookupService.lookup(cls).decode(IO.read(f));
	}

	@Override
	public void savePacketHandlerDefs(PacketHandlerDefinition[] defs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PacketHandlerDefinition[] loadLSPacketHandlerDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveLSPacketHandlerDefs(PacketHandlerDefinition[] defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NpcHandlerDefinition[] loadNpcHandlers() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveNpcHandlers(NpcHandlerDefinition[] defs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Point, TelePoint> loadTelePoints() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveTelePoints(Map<Point, TelePoint> points) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Shop> loadShops() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveShops(List<Shop> shops) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, CerterDefinition> loadCerterDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveCerterDefs(Map<Integer, CerterDefinition> certers)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<GameObjectLocationDefinition> loadGameObjectLocs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveGameObjectLocs(List<GameObjectLocationDefinition> locs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ItemLocationDefinition> loadItemLocs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemLocs(List<ItemLocationDefinition> locs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<NPCLocationDefinition> loadNPCLocs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveNPCLocs(List<NPCLocationDefinition> locs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TileDefinition[] loadTileDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveTileDefs(TileDefinition[] defs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GameObjectDefinition[] loadGameObjectDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveGameObjectDefs(GameObjectDefinition[] defs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DoorDefinition[] loadDoorDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveDoorDefs(DoorDefinition[] defs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemDefinition[] loadItemDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemDefs(ItemDefinition[] defs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PrayerDefinition[] loadPrayerDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void savePrayerDefs(PrayerDefinition[] defs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SpellDefinition[] loadSpellDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveSpellDefs(SpellDefinition[] defs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NPCDefinition[] loadNPCDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveNPCDefs(NPCDefinition[] defs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemCraftingDefinition[] loadItemCraftingDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemCraftingDefs(ItemCraftingDefinition[] defs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemHerbSecondDefinition[] loadItemHerbSeconds() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemHerbSeconds(ItemHerbSecondDefinition[] seconds) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, ItemDartTipDefinition> loadItemDartTipDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemDartTipDefs(Map<Integer, ItemDartTipDefinition> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, ItemGemDefinition> loadGemDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveGemDefs(Map<Integer, ItemGemDefinition> defs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, ItemLogCutDefinition> loadItemLogCutDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemLogCutDefs(Map<Integer, ItemLogCutDefinition> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, ItemBowStringDefinition> loadItemBowStringDefs()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemBowStringDefs(Map<Integer, ItemBowStringDefinition> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, ItemArrowHeadDefinition> loadItemArrowHeadDefs()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemArrowHeadDefs(Map<Integer, ItemArrowHeadDefinition> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, FiremakingDefinition> loadFiremakingDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveFiremakingDefs(Map<Integer, FiremakingDefinition> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, int[]> loadItemAffectedTypes() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemAffectedTypes(Map<Integer, int[]> types)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, ItemWieldableDefinition> loadItemWieldableDefs()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemWieldableDefs(Map<Integer, ItemWieldableDefinition> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, ItemUnIdentHerbDefinition> loadItemUnIdentHerbDefs()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemUnIdentHerbDefs(Map<Integer, ItemUnIdentHerbDefinition> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, ItemHerbDefinition> loadItemHerbDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemHerbDefs(Map<Integer, ItemHerbDefinition> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, Integer> loadItemEdibleHeals() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemEdibleHeals(Map<Integer, Integer> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, ItemCookingDefinition> loadItemCookingDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemCookingDefs(Map<Integer, ItemCookingDefinition> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, ItemSmeltingDefinition> loadItemSmeltingDefs()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemSmeltingDefs(Map<Integer, ItemSmeltingDefinition> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemSmithingDefinition[] loadItemSmithingDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemSmithingDefs(ItemSmithingDefinition[] defs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, ObjectMiningDefinition> loadObjectMiningDefs()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObjectMiningDefs(Map<Integer, ObjectMiningDefinition> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, ObjectWoodcuttingDefinition> loadObjectWoodcuttingDefs()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObjectWoodcuttingDefs(
			Map<Integer, ObjectWoodcuttingDefinition> defs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, ObjectFishingDefinition[]> loadObjectFishDefs()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObjectFishingDefs(Map<Integer, ObjectFishingDefinition> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, Integer> loadSpellAgressiveLevel() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveSpellAgressiveLevel(Map<Integer, Integer> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, AgilityDefinition> loadAgilityDefs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveAgilityDefs(Map<Integer, AgilityDefinition> defs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, AgilityCourseDefinition> loadAgilityCourseDefs()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveAgilityCourseDef(Map<Integer, AgilityCourseDefinition> defs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<InvItem>[] loadKeyChestLoots() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveKeyChestLoots(List<InvItem>[] loots) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Integer, ItemDartTipDefinition> loadDartTips() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
