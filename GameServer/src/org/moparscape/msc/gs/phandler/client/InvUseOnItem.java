package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.Server;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.config.Formulae;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.event.MiniEvent;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.MenuHandler;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.definition.EntityHandler;
import org.moparscape.msc.gs.model.definition.skill.ItemArrowHeadDef;
import org.moparscape.msc.gs.model.definition.skill.ItemBowStringDef;
import org.moparscape.msc.gs.model.definition.skill.ItemDartTipDef;
import org.moparscape.msc.gs.model.definition.skill.ItemGemDef;
import org.moparscape.msc.gs.model.definition.skill.ItemHerbDef;
import org.moparscape.msc.gs.model.definition.skill.ItemHerbSecondDef;
import org.moparscape.msc.gs.model.definition.skill.ItemLogCutDef;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.tools.DataConversions;

public class InvUseOnItem implements PacketHandler {
	static int[] capes = { 183, 209, 229, 511, 512, 513, 514 };

	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();
	int[] dye = { 238, 239, 272, 282, 515, 516 };
	int[] newCapes = { 183, 512, 229, 513, 511, 514 };

	private boolean attachFeathers(Player player, final InvItem feathers,
			final InvItem item) {
		int amount = 10;
		if (!Config.members) {
			player.getActionSender().sendMessage(
					"This feature is not avaliable in f2p");
			return true;
		}
		if (feathers.amount < amount) {
			amount = feathers.amount;
		}
		if (item.amount < amount) {
			amount = item.amount;
		}
		InvItem newItem;
		int exp;
		ItemDartTipDef tipDef = null;
		if (item.id == 280) {
			newItem = new InvItem(637, amount);
			exp = amount;
		} else if ((tipDef = EntityHandler.getItemDartTipDef(item.id)) != null) {
			newItem = new InvItem(tipDef.getDartID(), amount);
			exp = (int) (tipDef.getExp() * (double) amount);
		} else {
			return false;
		}
		final int amt = amount;
		final int xp = exp;
		final InvItem newItm = newItem;
		Instance.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (owner.getInventory().remove(feathers.id, amt, false)
						&& owner.getInventory().remove(item.id, amt, false)) {
					owner.getActionSender().sendMessage(
							"You attach the feathers to the "
									+ item.getDef().getName());
					owner.getInventory().add(newItm.id, newItm.amount, false);
					owner.incExp(9, xp, true);
					owner.getActionSender().sendStat(9);
					owner.getActionSender().sendInventory();
				}
			}
		});
		return true;
	}

	private boolean combineKeys(Player player, final InvItem firstHalf,
			final InvItem secondHalf) {
		if (secondHalf.id != 527) {
			return false;
		}
		if (player.getInventory().remove(firstHalf.id, firstHalf.amount, false)
				&& player.getInventory().remove(secondHalf.id,
						secondHalf.amount, false)) {
			player.getActionSender().sendMessage(
					"You combine the key halves to make a crystal key.");
			player.getInventory().add(525, 1, false);
			player.getActionSender().sendInventory();
		}
		return true;
	}

	private boolean doArrowHeads(Player player, final InvItem headlessArrows,
			final InvItem arrowHeads) {
		final ItemArrowHeadDef headDef = EntityHandler
				.getItemArrowHeadDef(arrowHeads.id);
		if (!Config.members) {
			player.getActionSender().sendMessage(
					"This feature is not avaliable in f2p");
			return true;
		}

		if (headDef == null) {
			return false;
		}
		if (player.getCurStat(9) < headDef.getReqLevel()) {
			player.getActionSender().sendMessage(
					"You need a fletching level of " + headDef.getReqLevel()
							+ " to attach those.");
			return true;
		}
		int amount = 10;
		if (headlessArrows.amount < amount) {
			amount = headlessArrows.amount;
		}
		if (arrowHeads.amount < amount) {
			amount = arrowHeads.amount;
		}
		final int amt = amount;
		Instance.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (owner.getInventory().remove(headlessArrows.id, amt, false)
						&& owner.getInventory().remove(arrowHeads.id, amt,
								false)) {
					owner.getActionSender().sendMessage(
							"You attach the heads to the arrows");
					owner.getInventory().add(headDef.getArrowID(), amt, false);
					owner.incExp(9, (int) (headDef.getExp() * (double) amt),
							true);
					owner.getActionSender().sendStat(9);
					owner.getActionSender().sendInventory();
				}
			}
		});
		return true;
	}

	private boolean doBowString(Player player, final InvItem bowString,
			final InvItem bow) {
		final ItemBowStringDef stringDef = EntityHandler
				.getItemBowStringDef(bow.id);
		if (!Config.members) {
			player.getActionSender().sendMessage(
					"This feature is not avaliable in f2p");
			return true;
		}

		if (stringDef == null) {
			return false;
		}
		if (player.getCurStat(9) < stringDef.getReqLevel()) {
			player.getActionSender().sendMessage(
					"You need a fletching level of " + stringDef.getReqLevel()
							+ " to do that.");
			return true;
		}
		Instance.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (owner.getInventory().remove(bowString.id, bowString.amount,
						false)
						&& owner.getInventory().remove(bow.id, bow.amount,
								false)) {
					owner.getActionSender().sendMessage(
							"You add the bow string to the bow");
					owner.getInventory().add(stringDef.getBowID(), 1, false);
					owner.incExp(9, stringDef.getExp(), true);
					owner.getActionSender().sendStat(9);
					owner.getActionSender().sendInventory();
				}
			}
		});
		return true;
	}

	private boolean doCutGem(Player player, final InvItem chisel,
			final InvItem gem) {
		final ItemGemDef gemDef = EntityHandler.getItemGemDef(gem.id);
		if (gemDef == null) {
			return false;
		}
		if (player.getCurStat(12) < gemDef.getReqLevel()) {
			player.getActionSender().sendMessage(
					"You need a crafting level of " + gemDef.getReqLevel()
							+ " to cut this gem");
			return true;
		}
		Instance.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (owner.getInventory().remove(gem.id, gem.amount, false)) {
					InvItem cutGem = new InvItem(gemDef.getGemID(), 1);
					owner.getActionSender().sendMessage(
							"You cut the " + cutGem.getDef().getName());
					owner.getActionSender().sendSound("chisel");
					owner.getInventory().add(cutGem.id, cutGem.amount, false);
					owner.incExp(12, gemDef.getExp(), true);
					owner.getActionSender().sendStat(12);
					owner.getActionSender().sendInventory();
				}
			}
		});
		return true;
	}

	private boolean doGlassBlowing(Player player, final InvItem pipe,
			final InvItem glass) {
		if (glass.id != 623) {
			return false;
		}
		if (!Config.members) {
			player.getActionSender().sendMessage(
					"This feature is not avaliable in f2p");
			return true;
		}
		Instance.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				String[] options = new String[] { "Beer Glass", "Vial", "Orb",
						"Cancel" };
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						InvItem result;
						int reqLvl, exp;
						switch (option) {
						case 0:
							result = new InvItem(620, 1);
							reqLvl = 1;
							exp = 18;
							break;
						case 1:
							result = new InvItem(465, 1);
							reqLvl = 33;
							exp = 35;
							break;
						case 2:
							result = new InvItem(611, 1);
							reqLvl = 46;
							exp = 53;
							break;
						default:
							return;
						}
						if (owner.getCurStat(12) < reqLvl) {
							owner.getActionSender().sendMessage(
									"You need a crafting level of " + reqLvl
											+ " to make a "
											+ result.getDef().getName() + ".");
							return;
						}
						if (owner.getInventory().remove(glass.id, glass.amount,
								false)) {
							owner.getActionSender().sendMessage(
									"You make a " + result.getDef().getName());
							owner.getInventory().add(result.id, result.amount,
									false);
							owner.incExp(12, exp, true);
							owner.getActionSender().sendStat(12);
							owner.getActionSender().sendInventory();
						}
					}
				});
				owner.getActionSender().sendMenu(options);
			}
		});
		return true;
	}

	private boolean doGrind(Player player, final InvItem mortar,
			final InvItem item) {
		int newID;
		switch (item.id) {
		case 466: // Unicorn Horn
			newID = 473;
			break;
		case 467: // Blue dragon scale
			newID = 472;
			break;
		default:
			return false;
		}
		if (player.getInventory().remove(item.id, item.amount, false)) {
			player.getActionSender().sendMessage(
					"You grind up the " + item.getDef().getName());
			player.getInventory().add(newID, 1, false);
			player.getActionSender().sendInventory();
		}
		return true;
	}

	private boolean doHerblaw(Player player, final InvItem vial,
			final InvItem herb) {
		final ItemHerbDef herbDef = EntityHandler.getItemHerbDef(herb.id);
		if (herbDef == null) {
			return false;
		}
		if (!Config.members) {
			player.getActionSender().sendMessage(
					"This feature is not avaliable in f2p");
			return true;
		}
		if (player.getCurStat(15) < herbDef.getReqLevel()) {
			player.getActionSender().sendMessage(
					"You need a herblaw level of " + herbDef.getReqLevel()
							+ " to mix those.");
			return true;
		}
		Instance.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (owner.getInventory().remove(vial.id, vial.amount, false)
						&& owner.getInventory().remove(herb.id, herb.amount,
								false)) {
					owner.getActionSender().sendMessage(
							"You add the " + herb.getDef().getName()
									+ " to the water");
					owner.getInventory().add(herbDef.getPotionId(), 1, false);
					// owner.incExp(15, herbDef.getExp(), true);
					// owner.getActionSender().sendStat(15);
					owner.getActionSender().sendInventory();
				}
			}
		});
		return true;
	}

	private boolean doHerbSecond(Player player, final InvItem second,
			final InvItem unfinished, final ItemHerbSecondDef def) {
		if (!Config.members) {
			player.getActionSender().sendMessage(
					"This feature is not avaliable in f2p");
			return true;
		}
		if (unfinished.id != def.getUnfinishedID()) {
			return false;
		}
		if (player.getCurStat(15) < def.getReqLevel()) {
			player.getActionSender().sendMessage(
					"You need a herblaw level of " + def.getReqLevel()
							+ " to mix those");
			return true;
		}
		if (player.getInventory().remove(second.id, second.amount, false)
				&& player.getInventory().remove(unfinished.id,
						unfinished.amount, false)) {
			player.getActionSender().sendMessage(
					"You mix the " + second.getDef().getName() + " with the "
							+ unfinished.getDef().getName());
			player.getInventory().add(def.getPotionID(), 1, false);
			player.incExp(15, def.getExp() * 2, true);
			player.getActionSender().sendStat(15);
			player.getActionSender().sendInventory();
		}
		return true;
	}

	private boolean doLogCut(Player player, final InvItem knife,
			final InvItem log) {
		return doLogCut(player, knife, log,
				((int) Math.ceil(player.getMaxStat(9) / 10)));
	}

	private boolean doLogCut(final Player player, final InvItem knife,
			final InvItem log, int times) {
		final int retries = --times;
		final ItemLogCutDef cutDef = EntityHandler.getItemLogCutDef(log.id);
		if (!Config.members) {
			player.getActionSender().sendMessage(
					"This feature is not avaliable in f2p");
			return true;
		}
		if (cutDef == null) {
			return false;
		}
		Instance.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				String[] options = new String[] { "Arrow shafts", "Shortbow",
						"Longbow", "Cancel" };
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						InvItem result;
						int reqLvl, exp;
						switch (option) {
						case 0:
							result = new InvItem(280, cutDef.getShaftAmount());
							reqLvl = cutDef.getShaftLvl();
							exp = cutDef.getShaftExp();
							break;
						case 1:
							result = new InvItem(cutDef.getShortbowID(), 1);
							reqLvl = cutDef.getShortbowLvl();
							exp = cutDef.getShortbowExp();
							break;
						case 2:
							result = new InvItem(cutDef.getLongbowID(), 1);
							reqLvl = cutDef.getLongbowLvl();
							exp = cutDef.getLongbowExp();
							break;
						default:
							return;
						}
						if (owner.getCurStat(9) < reqLvl) {
							owner.getActionSender().sendMessage(
									"You need a fletching level of " + reqLvl
											+ " to cut that.");
							return;
						}
						if (owner.getInventory().remove(log.id, log.amount,
								false)) {
							owner.getActionSender().sendMessage(
									"You make a " + result.getDef().getName());
							owner.getInventory().add(result.id, result.amount,
									false);
							owner.incExp(9, exp, true);
							owner.getActionSender().sendStat(9);
							owner.getActionSender().sendInventory();
						}
						if (retries > 0) {
							doLogCut(player, knife, log, retries);
						}
					}
				});
				owner.getActionSender().sendMenu(options);
			}
		});
		return true;
	}

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		InvItem item1 = player.getInventory().getSlot(p.readShort());
		InvItem item2 = player.getInventory().getSlot(p.readShort());
		if (item1 == null || item2 == null) {
			player.setSuspiciousPlayer(true);
			return;

		}
		world.addEntryToSnapshots(new Activity(player.getUsername(), player
				.getUsername()
				+ " used item "
				+ item1.getDef().getName()
				+ "("
				+ item1.id
				+ ")"
				+ " [CMD: "
				+ item1.getDef().getCommand()
				+ "] ON A ANOTHER INV ITEM "
				+ item2.getDef().getName()
				+ "("
				+ item2.id
				+ ")"
				+ " [CMD: "
				+ item2.getDef().getCommand()
				+ "] at: " + player.getX() + "/" + player.getY()));

		ItemHerbSecondDef secondDef = null;
		if ((secondDef = EntityHandler.getItemHerbSecond(item1.id, item2.id)) != null
				&& doHerbSecond(player, item1, item2, secondDef)) {
			return;
		} else if ((secondDef = EntityHandler.getItemHerbSecond(item2.id,
				item1.id)) != null
				&& doHerbSecond(player, item2, item1, secondDef)) {
			return;
		}
		// water - empty water
		/*
		 * int[][] jugs = { {50, 21}, {141, 140}, {342, 341} }; if(item1.id ==
		 * 136 || item2.id == 136) { for(int i=0; i < jugs.length; i++) {
		 * if(item2.id == jugs[i][0]) { if(player.getInventory().remove(new
		 * InvItem(136, 1, false) && player.getInventory().remove(new
		 * InvItem(jugs[i][0], 1, false)) { player.getInventory().add(new
		 * InvItem(jugs[i][1])); player.getInventory().add(135));
		 * player.getInventory().add(250));
		 * player.getActionSender().sendInventory();
		 * player.getActionSender().sendMessage("You create pastry dough");
		 * return; } } } }
		 */
		// dish ingred id - uncooked dish id
		int[][] stuff = { { 252, 254 }, { 132, 255 }, { 236, 256 } };
		if (item1.id == 253 || item2.id == 253) {
			for (int i = 0; i < stuff.length; i++) {
				if (stuff[i][0] == item1.id || item2.id == stuff[i][0]) {
					if (player.getInventory().remove(253, 1, false)
							&& player.getInventory().remove(stuff[i][0], 1,
									false)) {
						player.getInventory().add(stuff[i][1], 1, false);
						player.getActionSender().sendMessage(
								"You create an uncooked pie!");
						player.getActionSender().sendInventory();
						return;
					}
				}
			}
		}
		if (item1.id == 238 && item2.id == 239 || item1.id == 239
				&& item2.id == 238) {
			if (player.getInventory().remove(239, 1, false)
					&& player.getInventory().remove(238, 1, false)) {
				player.getInventory().add(282, 1, false);
				player.getActionSender().sendMessage("You mix the Dyes");
				player.getActionSender().sendInventory();
				return;
			}
		}
		// 1 dose on 2 dose str = 3 dose
		if (item1.id == 224 && item2.id == 223 || item1.id == 223
				&& item2.id == 224) {
			if (player.getInventory().remove(224, 1, false)
					&& player.getInventory().remove(223, 1, false)) {
				player.getInventory().add(222, 1, false);
				player.getActionSender().sendMessage(
						"You mix the strength potions");
				player.getActionSender().sendInventory();
				return;
			}
		}
		// 1 dose on 3 dose = 4 dose
		if (item1.id == 224 && item2.id == 222 || item1.id == 222
				&& item2.id == 224) {
			if (player.getInventory().remove(224, 1, false)
					&& player.getInventory().remove(222, 1, false)) {
				player.getInventory().add(221, 1, false);
				player.getActionSender().sendMessage(
						"You mix the strength potions");
				player.getActionSender().sendInventory();
				return;
			}
		}

		// 2 dose on 2 dose = 4 dose
		if (item1.id == 223 && item2.id == 223 || item1.id == 223
				&& item2.id == 223) {
			if (player.getInventory().remove(223, 2, false)) {
				player.getInventory().add(221, 1, false);
				player.getActionSender().sendMessage(
						"You mix the strength potions");
				player.getActionSender().sendInventory();
				return;
			}
		}
		if (item1.id == 224 && item2.id == 224 || item1.id == 224
				&& item2.id == 224) {
			if (player.getInventory().remove(224, 2, false)) {
				player.getInventory().add(223, 1, false);
				player.getActionSender().sendMessage(
						"You mix the strength potions");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.id == 132 && item2.id == 342 || item1.id == 342
				&& item2.id == 132) {
			if (player.getInventory().remove(342, 1, false)
					&& player.getInventory().remove(132, 1, false)) {
				player.getInventory().add(344, 1, false);
				player.getActionSender().sendMessage(
						"You start to create a stew");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.id == 348 && item2.id == 342 || item1.id == 342
				&& item2.id == 348) {
			if (player.getInventory().remove(342, 1, false)
					&& player.getInventory().remove(348, 1, false)) {
				player.getInventory().add(343, 1, false);
				player.getActionSender().sendMessage(
						"You start to create a stew");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.id == 132 && item2.id == 343 || item1.id == 343
				&& item2.id == 132) {
			if (player.getInventory().remove(343, 1, false)
					&& player.getInventory().remove(132, 1, false)) {
				player.getInventory().add(345, 1, false);
				player.getActionSender().sendMessage(
						"Your stew is now ready, but uncooked");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.id == 348 && item2.id == 344 || item1.id == 344
				&& item2.id == 348) {
			if (player.getInventory().remove(344, 1, false)
					&& player.getInventory().remove(348, 1, false)) {
				player.getInventory().add(345, 1, false);
				player.getActionSender().sendMessage(
						"our stew is now ready, but uncooked");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.id == 337 && item2.id == 330 || item1.id == 330
				&& item2.id == 337) {
			if (player.getInventory().remove(337, 1, false)
					&& player.getInventory().remove(330, 1, false)) {
				player.getInventory().add(332, 1, false);
				player.getActionSender().sendMessage(
						"You add chocolate to the cake");
				player.getActionSender().sendInventory();
				return;
			}
		}

		int egg = 19;
		int milk = 22;
		int flour = 136;

		if (item1.id == 338 || item2.id == 338) {
			if (player.getInventory().countId(egg) > -1
					&& player.getInventory().countId(milk) > -1
					&& player.getInventory().countId(flour) > -1) {
				if (player.getInventory().remove(egg, 1, false)
						&& player.getInventory().remove(milk, 1, false)
						&& player.getInventory().remove(flour, 1, false)
						&& player.getInventory().remove(338, 1, false)) {
					player.getInventory().add(135, 1, false);
					player.getInventory().add(339, 1, false);
					player.getActionSender().sendInventory();
					player.getActionSender().sendMessage(
							"You create an uncooked cake");
					return;
				}
			}
		}
		if (item1.id == 238 && item2.id == 239 || item1.id == 239
				&& item2.id == 238) {
			if (player.getInventory().remove(239, 1, false)
					&& player.getInventory().remove(238, 1, false)) {
				player.getInventory().add(282, 1, false);
				player.getActionSender().sendMessage("You mix the Dyes");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.id == 250 && item2.id == 251 || item1.id == 251
				&& item2.id == 250) {
			if (player.getInventory().remove(251, 1, false)
					&& player.getInventory().remove(250, 1, false)) {
				player.getInventory().add(253, 1, false);
				player.getActionSender().sendMessage(
						"You add the pastry dough in the dish");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.id == 238 && item2.id == 272 || item1.id == 272
				&& item2.id == 238) {
			if (player.getInventory().remove(272, 1, false)
					&& player.getInventory().remove(238, 1, false)) {
				player.getInventory().add(516, 1, false);
				player.getActionSender().sendMessage("You mix the Dyes");
				player.getActionSender().sendInventory();
				return;
			}
		}
		if (item1.id == 239 && item2.id == 272 || item1.id == 272
				&& item2.id == 239) {
			if (player.getInventory().remove(272, 1, false)
					&& player.getInventory().remove(239, 1, false)) {
				player.getInventory().add(515, 1, false);
				player.getActionSender().sendMessage("You mix the Dyes");
				player.getActionSender().sendInventory();
				return;
			}
		}

		else if (item1.id == 1276 && item2.id == 1277) {
			if (player.getInventory().remove(1276, 1, false)
					&& player.getInventory().remove(1277, 1, false)) {
				player.getActionSender().sendMessage(
						"You combine the two parts.");
				player.getInventory().add(1278, 1, false);
				player.getActionSender().sendInventory();
				return;
			}
		}// here
		else if (item1.id == 143 && item2.id == 141 || item1.id == 141
				&& item2.id == 143) {
			if (player.getCurStat(7) < 35) {
				player.getActionSender().sendMessage(
						"You need level 35 cooking to do this");
				return;
			}
			if (player.getInventory().remove(141, 1, false)
					&& player.getInventory().remove(143, 1, false)) {
				int rand = Formulae.Rand(0, 4);
				if (rand == 2) {
					player.incExp(7, 55, true);
					player.getInventory().add(180, 1, false);
					player.getActionSender()
							.sendMessage(
									"You mix the grapes, and accidentally create Bad wine!");
				} else {
					player.incExp(7, 110, true);
					player.getInventory().add(142, 1, false);
					player.getActionSender()
							.sendMessage(
									"You mix the grapes with the water and create wine!");
				}
				player.getActionSender().sendStat(7);
				player.getActionSender().sendInventory();
				return;
			}
		}
		for (Integer il : capes) {
			if (il == item1.id) {
				for (int i = 0; i < dye.length; i++) {
					if (dye[i] == item2.id) {
						if (player.getInventory().remove(item1.id, 1, false)
								&& player.getInventory().remove(item2.id, 1,
										false)) {
							player.getActionSender().sendMessage(
									"You dye the Cape");
							player.getInventory().add(newCapes[i], 1, false);
							player.getActionSender().sendInventory();
							return;
						}
					}
				}
			} else if (il == item2.id) {
				for (int i = 0; i < dye.length; i++) {
					if (dye[i] == item1.id) {
						if (player.getInventory().remove(item1.id, 1, false)
								&& player.getInventory().remove(item2.id, 1,
										false)) {
							player.getActionSender().sendMessage(
									"You dye the Cape");
							player.getInventory().add(newCapes[i], 1, false);
							player.getActionSender().sendInventory();
							return;
						}
					}
				}
			}
		}
		if (item1.id == 141 && item2.id == 136 || item1.id == 136
				&& item2.id == 141) {
			player.getActionSender()
					.sendMessage("What would you like to make?");
			String[] optionsz = new String[] { "Bread Dough", "Pizza Base",
					"Pastry Dough" };

			player.setMenuHandler(new MenuHandler(optionsz) {
				public void handleReply(final int option, final String reply) {
					int newid = 0;
					if (option == 0) {
						newid = 137;
					} else if (option == 1) {
						newid = 321;
					} else if (option == 2) {
						newid = 250;
					} else {
						return;
					}
					if (owner.getInventory().remove(141, 1, false)
							&& owner.getInventory().remove(136, 1, false)) {
						owner.getActionSender().sendMessage(
								"You create a " + reply);
						owner.getInventory().add(140, 1, false);
						owner.getInventory().add(135, 1, false);
						owner.getInventory().add(newid, 1, false);
						owner.getActionSender().sendInventory();
					}

				}
			});
			player.getActionSender().sendMenu(optionsz);
			return;
		}

		if (item1.id == 50 && item2.id == 136 || item1.id == 136
				&& item2.id == 50) {
			player.getActionSender()
					.sendMessage("What would you like to make?");
			String[] optionsz = new String[] { "Bread Dough", "Pizza Base",
					"Pastry Dough" };
			player.setMenuHandler(new MenuHandler(optionsz) {
				public void handleReply(final int option, final String reply) {
					int newid = 0;
					if (option == 0) {
						newid = 137;
					} else if (option == 1) {
						newid = 321;
					} else if (option == 2) {
						newid = 250;
					} else {
						return;
					}
					if (owner.getInventory().remove(50, 1, false)
							&& owner.getInventory().remove(136, 1, false)) {
						owner.getActionSender().sendMessage(
								"You create a " + reply);
						owner.getInventory().add(21, 1, false);
						owner.getInventory().add(135, 1, false);
						owner.getInventory().add(newid, 1, false);
						owner.getActionSender().sendInventory();
					}

				}
			});
			player.getActionSender().sendMenu(optionsz);
			return;
		}

		if (item1.id == 273 && item2.id == 282 || item1.id == 282
				&& item2.id == 273) {
			if (player.getInventory().remove(282, 1, false)
					&& player.getInventory().remove(273, 1, false)) {
				player.getInventory().add(274, 1, false);
				player.getActionSender()
						.sendMessage("You dye the goblin armor");
				player.getActionSender().sendInventory();
				return;
			}
		}
		if (item1.id == 273 && item2.id == 272 || item1.id == 272
				&& item2.id == 273) {
			if (player.getInventory().remove(272, 1, false)
					&& player.getInventory().remove(273, 1, false)) {
				player.getInventory().add(275, 1, false);
				player.getActionSender()
						.sendMessage("You dye the goblin armor");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.id == 320 && item2.id == 321 || item1.id == 321
				&& item2.id == 320) {
			if (player.getInventory().remove(321, 1, false)
					&& player.getInventory().remove(320, 1, false)) {
				player.getInventory().add(323, 1, false);
				player.getActionSender().sendMessage(
						"You add the Tomato to the Pizza base");
				player.getActionSender().sendInventory();
				return;
			}
		}
		if (item1.id == 319 && item2.id == 323 || item1.id == 323
				&& item2.id == 319) {
			if (player.getInventory().remove(323, 1, false)
					&& player.getInventory().remove(319, 1, false)) {
				player.getInventory().add(324, 1, false);
				player.getActionSender().sendMessage(
						"You add Cheese on the Unfinished Pizza");
				player.getActionSender().sendInventory();
				return;
			}
		}
		if (item1.id == 325 && item2.id == 352 || item1.id == 352
				&& item2.id == 325) {
			if (player.getCurStat(7) > 54) {
				if (player.getInventory().remove(352, 1, false)
						&& player.getInventory().remove(325, 1, false)) {
					player.getInventory().add(327, 1, false);
					player.incExp(7, 110, true);
					player.getActionSender().sendStat(7);
					player.getActionSender().sendMessage(
							"You create an Anchovie Pizza.");
					player.getActionSender().sendInventory();
					return;
				}
			} else {
				player.getActionSender().sendMessage(
						"You need a cooking level of 55 to do this");
				return;
			}
		}
		if (item1.id == 325 && item2.id == 132 || item1.id == 132
				&& item2.id == 325) {
			if (player.getCurStat(7) > 44) {
				if (player.getInventory().remove(132, 1, false)
						&& player.getInventory().remove(325, 1, false)) {
					player.getInventory().add(326, 1, false);
					player.incExp(7, 110, true);
					player.getActionSender().sendStat(7);
					player.getActionSender().sendMessage(
							"You create a Meat Pizza.");
					player.getActionSender().sendInventory();
					return;
				}
			} else {
				player.getActionSender().sendMessage(
						"You need a cooking level of 44 to do this");
				return;
			}// doHerblaw
		}
		if (item2.id == 1276 && item1.id == 1277) {
			if (player.getInventory().remove(1276, 1, false)
					&& player.getInventory().remove(1277, 1, false)) {
				player.getActionSender().sendMessage(
						"You combine the two parts.");
				player.getInventory().add(1278, 1, false);
				player.getActionSender().sendInventory();
				return;
			}
		} else if (item1.id == 381 && attachFeathers(player, item1, item2)) {
			return;
		} else if (item2.id == 381 && attachFeathers(player, item2, item1)) {
			return;
		} else if (item1.id == 167 && doCutGem(player, item1, item2)) {
			return;
		} else if (item2.id == 167 && doCutGem(player, item2, item1)) {
			return;
		} else if (item1.id == 13 && doLogCut(player, item1, item2)) {
			return;
		} else if (item2.id == 13 && doLogCut(player, item2, item1)) {
			return;
		} else if (item1.id == 464 && doHerblaw(player, item1, item2)) {
			return;
		} else if (item2.id == 464 && doHerblaw(player, item2, item1)) {
			return;
		} else if (item1.id == 676 && doBowString(player, item1, item2)) {
			return;
		} else if (item2.id == 676 && doBowString(player, item2, item1)) {
			return;
		} else if (item1.id == 637 && doArrowHeads(player, item1, item2)) {
			return;
		} else if (item2.id == 637 && doArrowHeads(player, item2, item1)) {
			return;
		} else if (item1.id == 468 && doGrind(player, item1, item2)) {
			return;
		} else if (item2.id == 468 && doGrind(player, item2, item1)) {
			return;
		} else if (item1.id == 207 && useWool(player, item1, item2)) {
			return;
		} else if (item2.id == 207 && useWool(player, item2, item1)) {
			return;
		} else if (item1.id == 39 && makeLeather(player, item1, item2)) {
			return;
		} else if (item2.id == 39 && makeLeather(player, item2, item1)) {
			return;
		} else if (item1.id == 621 && doGlassBlowing(player, item1, item2)) {
			return;
		} else if (item2.id == 621 && doGlassBlowing(player, item2, item1)) {
			return;
		} else if ((item1.id == 50 || item1.id == 141 || item1.id == 342)
				&& useWater(player, item1, item2)) {
			return;
		} else if ((item2.id == 50 || item2.id == 141 || item2.id == 342)
				&& useWater(player, item2, item1)) {
			return;
		}

		else if (item1.id == 526 && combineKeys(player, item1, item2)) {
			return;
		} else if (item2.id == 526 && combineKeys(player, item2, item1)) {
			return;
		} else if ((item1.id == 23 && item2.id == 135)
				|| (item2.id == 23 && item1.id == 135)) {
			if (player.getInventory().remove(23, 1, false)
					&& player.getInventory().remove(135, 1, false)) {
				player.getInventory().add(136, 1, false);
				player.getActionSender().sendInventory();
				player.getActionSender().sendMessage(
						"You pour the flour into the pot.");
				return;
			}
		} else {

			int[][] combinePotions = { { 475, 476, 474 }, // Attack potions.
					{ 478, 479, 477 }, // Stat restore potions
					{ 481, 482, 480 }, // Defense potions
					{ 484, 485, 483 }, // Prayer potion
					{ 487, 488, 486 }, // SAP
					{ 490, 491, 489 }, // Fishing potion
					{ 493, 494, 492 }, // SSP
					{ 496, 497, 495 }, // SDP
					{ 499, 500, 498 } // Range pot
			};

			for (int i = 0; i < combinePotions.length; i++) {
				if ((item1.id == combinePotions[i][0] && item2.id == combinePotions[i][1])
						|| (item2.id == combinePotions[i][0] && item1.id == combinePotions[i][1])) {
					if (!Config.members) {
						player.getActionSender().sendMessage(
								"This feature is not avaliable in f2p");
						return;
					}
					if (player.getInventory().remove(combinePotions[i][0], 1,
							false)
							&& player.getInventory().remove(
									combinePotions[i][1], 1, false)) {
						player.getInventory().add(combinePotions[i][2], 1,
								false);
						player.getActionSender().sendInventory();
						player.getActionSender().sendMessage(
								"You combine the Potions");
						return;
					}
				} else if (item1.id == combinePotions[i][1]
						&& item2.id == combinePotions[i][1]) {
					if (!Config.members) {
						player.getActionSender().sendMessage(
								"This feature is not avaliable in f2p");
						return;
					}
					if (player.getInventory().remove(combinePotions[i][1], 1,
							false)
							&& player.getInventory().remove(
									combinePotions[i][1], 1, false)) {
						if (!Server.isMembers()) {
							player.getActionSender().sendMessage(
									"This feature is not avaliable in f2p");
							return;
						}
						player.getInventory().add(combinePotions[i][0], 1,
								false);
						player.getActionSender().sendInventory();
						player.getActionSender().sendMessage(
								"You combine the Potions");
						return;
					} else if (item1.id == combinePotions[i][0]
							&& item2.id == combinePotions[i][0]) {
						if (!Config.members) {
							player.getActionSender().sendMessage(
									"This feature is not avaliable in f2p");
							return;
						}
						if (player.getInventory().remove(combinePotions[i][0],
								1, false)
								&& player.getInventory().remove(
										combinePotions[i][0], 1, false)) {
							player.getInventory().add(combinePotions[i][2], 1,
									false);
							player.getInventory().add(combinePotions[i][1], 1,
									false);
							player.getActionSender().sendInventory();
							player.getActionSender().sendMessage(
									"You combine the Potions");
							return;
						}
					}
				}
			}

			player.getActionSender().sendMessage("Nothing interesting happens");
		}
	}

	private boolean makeLeather(Player player, final InvItem needle,
			final InvItem leather) {
		if (leather.id != 148) {
			return false;
		}
		if (player.getInventory().countId(43) < 1) {
			player.getActionSender().sendMessage(
					"You need some thread to make anything out of leather");
			return true;
		}
		if (DataConversions.random(0, 5) == 0) {
			player.getInventory().remove(43, 1, false);
		}
		Instance.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				String[] options = new String[] { "Armour", "Gloves", "Boots",
						"Cancel" };
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						InvItem result;
						int reqLvl, exp;
						switch (option) {
						case 0:
							result = new InvItem(15, 1);
							reqLvl = 14;
							exp = 25;
							break;
						case 1:
							result = new InvItem(16, 1);
							reqLvl = 1;
							exp = 14;
							break;
						case 2:
							result = new InvItem(17, 1);
							reqLvl = 7;
							exp = 17;
							break;
						default:
							return;
						}
						if (owner.getCurStat(12) < reqLvl) {
							owner.getActionSender().sendMessage(
									"You need a crafting level of " + reqLvl
											+ " to make "
											+ result.getDef().getName() + ".");
							return;
						}
						if (owner.getInventory().remove(leather.id,
								leather.amount, false)) {
							owner.getActionSender().sendMessage(
									"You make some "
											+ result.getDef().getName());
							owner.getInventory().add(result.id, result.amount,
									false);
							owner.incExp(12, exp, true);
							owner.getActionSender().sendStat(12);
							owner.getActionSender().sendInventory();
						}
					}
				});
				owner.getActionSender().sendMenu(options);
			}
		});
		return true;
	}

	private boolean useWater(Player player, final InvItem water,
			final InvItem item) {
		int jugID = Formulae.getEmptyJug(water.id);
		if (jugID == -1) { // This shouldn't happen
			return false;
		}
		switch (item.id) {
		case 149: // Clay
			if (player.getInventory().remove(water.id, water.amount, false)
					&& player.getInventory()
							.remove(item.id, item.amount, false)) {
				player.getActionSender().sendMessage("You soften the clay.");
				player.getInventory().add(jugID, 1, false);
				player.getInventory().add(243, 1, false);
				player.getActionSender().sendInventory();
			}
			break;
		default:
			return false;
		}
		return true;
	}

	private boolean useWool(Player player, final InvItem woolBall,
			final InvItem item) {
		int newID;
		switch (item.id) {
		case 44: // Holy Symbol of saradomin
			newID = 45;
			break;
		case 1027: // Unholy Symbol of Zamorak
			newID = 1028;
			break;
		case 296: // Gold Amulet
			newID = 301;
			break;
		case 297: // Sapphire Amulet
			newID = 302;
			break;
		case 298: // Emerald Amulet
			newID = 303;
			break;
		case 299: // Ruby Amulet
			newID = 304;
			break;
		case 300: // Diamond Amulet
			newID = 305;
			break;
		case 524: // Dragonstone Amulet
			if (!Server.isMembers()) {
				player.getActionSender().sendMessage(
						"This feature is not avaliable in f2p");
				return true;
			}
			newID = 610;
			break;
		default:
			return false;
		}
		final int newId = newID;
		Instance.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (owner.getInventory().remove(woolBall.id, woolBall.amount,
						false)
						&& owner.getInventory().remove(item.id, item.amount,
								false)) {
					owner.getActionSender()
							.sendMessage("You string the amulet");
					owner.getInventory().add(newId, 1, false);
					owner.getActionSender().sendInventory();
				}
			}
		});
		return true;
	}
}
