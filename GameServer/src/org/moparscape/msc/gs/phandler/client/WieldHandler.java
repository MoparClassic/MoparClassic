package org.moparscape.msc.gs.phandler.client;

import java.util.List;
import java.util.Map.Entry;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.config.Formulae;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.definition.EntityHandler;
import org.moparscape.msc.gs.model.definition.skill.ItemWieldableDef;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.service.ItemAttributes;
import org.moparscape.msc.gs.util.Logger;

public class WieldHandler implements PacketHandler {

	// TODO: Fix weapon requirements.

	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();
		if (player.isBusy() && !player.inCombat()) {
			return;
		}// F2P
		if (player.isDueling() && player.getDuelSetting(3)) {
			player.getActionSender().sendMessage(
					"Armour is disabled in this duel");
			return;
		}
		player.resetAllExceptDueling();
		int idx = (int) p.readShort();
		if (idx < 0 || idx >= 30) {
			player.setSuspiciousPlayer(true);
			return;
		}// if(true)
		InvItem item = player.getInventory().getSlot(idx);
		if (item == null || !ItemAttributes.isWieldable(item.id)) {
			player.setSuspiciousPlayer(true);
			return;
		}
		if (player.getLocation().inWilderness() && item.getDef().isMembers()
				&& Config.f2pWildy) {
			player.getActionSender().sendMessage(
					"Can't wield a P2P item in wilderness");
			return;
		}

		if ((item.getDef().isMembers() && !World.isMembers())) {
			player.getActionSender().sendMessage(
					"This feature is only avaliable on a members server");
			return;
		}

		switch (pID) {
		case 181:
			if (!item.wielded) {
				try {
					wieldItem(player, item, idx);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case 92:
			if (item.wielded) {
				unWieldItem(player, item, true, idx);
			}
			break;
		}
		player.getActionSender().sendInventory();
		player.getActionSender().sendEquipmentStats();
	}

	public static void unWieldItem(Player player, InvItem item, boolean sound,
			int slot) {
		player.getInventory().setWield(slot, false);
		if (sound) {
			player.getActionSender().sendSound("click");
		}
		ItemWieldableDef def = ItemAttributes.getWieldable(item.id);
		player.updateWornItems(def.getWieldPos(), player.getPlayerAppearance()
				.getSprite(def.getWieldPos()));
	}

	private void wieldItem(Player player, InvItem item, int slot) {
		String youNeed = "";
		ItemWieldableDef def = ItemAttributes.getWieldable(item.id);
		if (def == null) {
			Logger.println("Def = null!!!!");
		}
		for (Entry<Integer, Integer> e : def.getStatsRequired()) {
			if (player.getMaxStat(e.getKey()) < e.getValue()) {
				youNeed += ((Integer) e.getValue()).intValue() + " "
						+ Formulae.statArray[((Integer) e.getKey()).intValue()]
						+ ", ";
			}
		}
		if (!youNeed.equals("")) {
			player.getActionSender().sendMessage(
					"You must have at least "
							+ youNeed.substring(0, youNeed.length() - 2)
							+ " to use this item.");
			return;
		}
		if (Config.members) {
			if (item.id == 594) {

				/*
				 * if (count < World.getQuestManager().getQuests().size() ||
				 * player.getCurStat(Script.MINING) < 50 ||
				 * player.getCurStat(Script.HERBLAW) < 25 ||
				 * player.getCurStat(Script.FISHING) < 53 ||
				 * player.getCurStat(Script.COOKING) < 53 ||
				 * player.getCurStat(Script.CRAFTING) < 31 ||
				 * player.getCurStat(Script.WOODCUT) < 36 ||
				 * player.getCurStat(Script.MAGIC) < 33) {
				 * player.getActionSender().sendMessage(
				 * "You must have completed at least " +
				 * (World.getQuestManager().getQuests() .size()) +
				 * " quests and have these stat reqs:");
				 * player.getActionSender() .sendMessage(
				 * "50 Mining, 25 Herblaw, 53 Fishing, 53 Cooking, 31 Crafting, 36 Woodcutting and 33 Magic"
				 * ); return; }
				 */
			} else if (item.id == 593) {

				/*
				 * if (player.getCurStat(Script.CRAFTING) < 31 ||
				 * player.getCurStat(Script.WOODCUT) < 36) {
				 * player.getActionSender().sendMessage(
				 * "You must have 31 Crafting and 36 Woodcutting"); return; }
				 */
			} else if (item.id == 1288) {
				boolean found = false;
				for (int i = 0; i < 18; i++) {
					if (player.getMaxStat(i) == 99) {
						found = true;
						break;
					}
				}
				if (!found) {
					player.getActionSender()
							.sendMessage(
									"Sorry, you need any skill of level 99 to wield this cape of legends");
					return;
				} else {
					player.getActionSender().sendMessage(
							"You wield the legendary cape like a true legend");
				}
			}
		}

		if (item.id == 407 || item.id == 401) {

			if (player.getCurStat(6) < 31) {
				player.getActionSender().sendMessage(
						"You must have at least 31 magic");
				return;
			}
		}
		if (EntityHandler.getItemWieldableDef(item.id).femaleOnly()
				&& player.isMale()) {
			player.getActionSender().sendMessage(
					"This piece of armor is for a female only.");
			return;
		}
		List<InvItem> items = player.getInventory().getItems();
		int slot1 = 0;
		for (InvItem i : items) {
			if (ItemAttributes.wieldingAffectsItem(item.id, i.id) && i.wielded) {
				unWieldItem(player, i, false, slot1);
			}
			slot1++;
		}
		player.getInventory().setWield(slot, true);
		player.getActionSender().sendSound("click");
		def = ItemAttributes.getWieldable(item.id);
		player.updateWornItems(def.getWieldPos(), def.getSprite());
	}

}