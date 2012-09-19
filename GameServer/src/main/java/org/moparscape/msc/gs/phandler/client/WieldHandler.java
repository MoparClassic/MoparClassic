package org.moparscape.msc.gs.phandler.client;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.config.Config;
import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.external.EntityHandler;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.Script;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.quest.Quest;
import org.moparscape.msc.gs.util.Logger;

public class WieldHandler implements PacketHandler {
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
		InvItem item = player.getInventory().get(idx);
		if (item == null || !item.isWieldable()) {
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
			if (!item.isWielded()) {
				wieldItem(player, item);
			}
			break;
		case 92:
			if (item.isWielded()) {
				unWieldItem(player, item, true);
			}
			break;
		}
		player.getActionSender().sendInventory();
		player.getActionSender().sendEquipmentStats();
	}

	public static void unWieldItem(Player player, InvItem item, boolean sound) {
		item.setWield(false);
		if (sound) {
			player.getActionSender().sendSound("click");
		}
		player.updateWornItems(
				item.getWieldableDef().getWieldPos(),
				player.getPlayerAppearance().getSprite(
						item.getWieldableDef().getWieldPos()));
	}

	private void wieldItem(Player player, InvItem item) {
		String youNeed = "";
		for (Entry<Integer, Integer> e : item.getWieldableDef()
				.getStatsRequired()) {
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
			if (item.getID() == 594) {
				int count = 0;
				for (Quest q : World.getQuestManager().getQuests()) {
					if (player.getQuestStage(q.getUniqueID()) == Quest.COMPLETE) {
						count++;
					} else if (q.getUniqueID() == 12) {
						count++;
					}
				}
				Logger.println(count + " - "
						+ World.getQuestManager().getQuests().size());
				if (count < World.getQuestManager().getQuests().size()
						|| player.getCurStat(Script.MINING) < 50
						|| player.getCurStat(Script.HERBLAW) < 25
						|| player.getCurStat(Script.FISHING) < 53
						|| player.getCurStat(Script.COOKING) < 53
						|| player.getCurStat(Script.CRAFTING) < 31
						|| player.getCurStat(Script.WOODCUT) < 36
						|| player.getCurStat(Script.MAGIC) < 33) {
					player.getActionSender().sendMessage(
							"You must have completed at least "
									+ (World.getQuestManager().getQuests()
											.size())
									+ " quests and have these stat reqs:");
					player.getActionSender()
							.sendMessage(
									"50 Mining, 25 Herblaw, 53 Fishing, 53 Cooking, 31 Crafting, 36 Woodcutting and 33 Magic");
					return;
				}
			} else if (item.getID() == 593) {

				if (player.getCurStat(Script.CRAFTING) < 31
						|| player.getCurStat(Script.WOODCUT) < 36) {
					player.getActionSender().sendMessage(
							"You must have 31 Crafting and 36 Woodcutting");
					return;
				}
			} else if (item.getID() == 1288) {
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

		if (item.getID() == 407 || item.getID() == 401) {
			int count = 0;
			for (Quest q : World.getQuestManager().getQuests()) {
				if (player.getQuestStage(q.getUniqueID()) == Quest.COMPLETE) {
					count++;
				} else if (q.getUniqueID() == 12) {
					count++;
				}
			}

			if (player.getCurStat(6) < 31
					|| count < World.getQuestManager().getQuests().size()) {
				player.getActionSender().sendMessage(
						"You must have at least 31 magic & completed "
								+ (World.getQuestManager().getQuests().size())
								+ " quests");
				return;
			}
		}
		if (EntityHandler.getItemWieldableDef(item.getID()).femaleOnly()
				&& player.isMale()) {
			player.getActionSender().sendMessage(
					"This piece of armor is for a female only.");
			return;
		}
		ArrayList<InvItem> items = player.getInventory().getItems();
		for (InvItem i : items) {
			if (item.wieldingAffectsItem(i) && i.isWielded()) {
				unWieldItem(player, i, false);
			}
		}
		item.setWield(true);
		player.getActionSender().sendSound("click");
		player.updateWornItems(item.getWieldableDef().getWieldPos(), item
				.getWieldableDef().getSprite());
	}

}