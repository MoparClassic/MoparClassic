package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.config.Config;
import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.Server;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.event.MiniEvent;
import org.moparscape.msc.gs.external.EntityHandler;
import org.moparscape.msc.gs.external.ItemArrowHeadDef;
import org.moparscape.msc.gs.external.ItemBowStringDef;
import org.moparscape.msc.gs.external.ItemDartTipDef;
import org.moparscape.msc.gs.external.ItemGemDef;
import org.moparscape.msc.gs.external.ItemHerbDef;
import org.moparscape.msc.gs.external.ItemHerbSecond;
import org.moparscape.msc.gs.external.ItemLogCutDef;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.MenuHandler;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
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
		if (feathers.getAmount() < amount) {
			amount = feathers.getAmount();
		}
		if (item.getAmount() < amount) {
			amount = item.getAmount();
		}
		InvItem newItem;
		int exp;
		ItemDartTipDef tipDef = null;
		if (item.getID() == 280) {
			newItem = new InvItem(637, amount);
			exp = amount;
		} else if ((tipDef = EntityHandler.getItemDartTipDef(item.getID())) != null) {
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
				if (owner.getInventory().remove(feathers.getID(), amt) > -1
						&& owner.getInventory().remove(item.getID(), amt) > -1) {
					owner.getActionSender().sendMessage(
							"You attach the feathers to the "
									+ item.getDef().getName());
					owner.getInventory().add(newItm);
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
		if (secondHalf.getID() != 527) {
			return false;
		}
		if (player.getInventory().remove(firstHalf) > -1
				&& player.getInventory().remove(secondHalf) > -1) {
			player.getActionSender().sendMessage(
					"You combine the key halves to make a crystal key.");
			player.getInventory().add(new InvItem(525, 1));
			player.getActionSender().sendInventory();
		}
		return true;
	}

	private boolean doArrowHeads(Player player, final InvItem headlessArrows,
			final InvItem arrowHeads) {
		final ItemArrowHeadDef headDef = EntityHandler
				.getItemArrowHeadDef(arrowHeads.getID());
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
		if (headlessArrows.getAmount() < amount) {
			amount = headlessArrows.getAmount();
		}
		if (arrowHeads.getAmount() < amount) {
			amount = arrowHeads.getAmount();
		}
		final int amt = amount;
		Instance.getDelayedEventHandler().add(new MiniEvent(player) {
			public void action() {
				if (owner.getInventory().remove(headlessArrows.getID(), amt) > -1
						&& owner.getInventory().remove(arrowHeads.getID(), amt) > -1) {
					owner.getActionSender().sendMessage(
							"You attach the heads to the arrows");
					owner.getInventory().add(
							new InvItem(headDef.getArrowID(), amt));
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
				.getItemBowStringDef(bow.getID());
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
				if (owner.getInventory().remove(bowString) > -1
						&& owner.getInventory().remove(bow) > -1) {
					owner.getActionSender().sendMessage(
							"You add the bow string to the bow");
					owner.getInventory().add(
							new InvItem(stringDef.getBowID(), 1));
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
		final ItemGemDef gemDef = EntityHandler.getItemGemDef(gem.getID());
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
				if (owner.getInventory().remove(gem) > -1) {
					InvItem cutGem = new InvItem(gemDef.getGemID(), 1);
					owner.getActionSender().sendMessage(
							"You cut the " + cutGem.getDef().getName());
					owner.getActionSender().sendSound("chisel");
					owner.getInventory().add(cutGem);
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
		if (glass.getID() != 623) {
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
						if (owner.getInventory().remove(glass) > -1) {
							owner.getActionSender().sendMessage(
									"You make a " + result.getDef().getName());
							owner.getInventory().add(result);
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
		switch (item.getID()) {
		case 466: // Unicorn Horn
			newID = 473;
			break;
		case 467: // Blue dragon scale
			newID = 472;
			break;
		default:
			return false;
		}
		if (player.getInventory().remove(item) > -1) {
			player.getActionSender().sendMessage(
					"You grind up the " + item.getDef().getName());
			player.getInventory().add(new InvItem(newID, 1));
			player.getActionSender().sendInventory();
		}
		return true;
	}

	private boolean doHerblaw(Player player, final InvItem vial,
			final InvItem herb) {
		final ItemHerbDef herbDef = EntityHandler.getItemHerbDef(herb.getID());
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
				if (owner.getInventory().remove(vial) > -1
						&& owner.getInventory().remove(herb) > -1) {
					owner.getActionSender().sendMessage(
							"You add the " + herb.getDef().getName()
									+ " to the water");
					owner.getInventory().add(
							new InvItem(herbDef.getPotionId(), 1));
					// owner.incExp(15, herbDef.getExp(), true);
					// owner.getActionSender().sendStat(15);
					owner.getActionSender().sendInventory();
				}
			}
		});
		return true;
	}

	private boolean doHerbSecond(Player player, final InvItem second,
			final InvItem unfinished, final ItemHerbSecond def) {
		if (!Config.members) {
			player.getActionSender().sendMessage(
					"This feature is not avaliable in f2p");
			return true;
		}
		if (unfinished.getID() != def.getUnfinishedID()) {
			return false;
		}
		if (player.getCurStat(15) < def.getReqLevel()) {
			player.getActionSender().sendMessage(
					"You need a herblaw level of " + def.getReqLevel()
							+ " to mix those");
			return true;
		}
		if (player.getInventory().remove(second) > -1
				&& player.getInventory().remove(unfinished) > -1) {
			player.getActionSender().sendMessage(
					"You mix the " + second.getDef().getName() + " with the "
							+ unfinished.getDef().getName());
			player.getInventory().add(new InvItem(def.getPotionID(), 1));
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
		final ItemLogCutDef cutDef = EntityHandler
				.getItemLogCutDef(log.getID());
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
						if (owner.getInventory().remove(log) > -1) {
							owner.getActionSender().sendMessage(
									"You make a " + result.getDef().getName());
							owner.getInventory().add(result);
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
		InvItem item1 = player.getInventory().get(p.readShort());
		InvItem item2 = player.getInventory().get(p.readShort());
		if (item1 == null || item2 == null) {
			player.setSuspiciousPlayer(true);
			return;

		}
		world.addEntryToSnapshots(new Activity(player.getUsername(), player
				.getUsername()
				+ " used item "
				+ item1.getDef().getName()
				+ "("
				+ item1.getID()
				+ ")"
				+ " [CMD: "
				+ item1.getDef().getCommand()
				+ "] ON A ANOTHER INV ITEM "
				+ item2.getDef().getName()
				+ "("
				+ item2.getID()
				+ ")"
				+ " [CMD: "
				+ item2.getDef().getCommand()
				+ "] at: " + player.getX() + "/" + player.getY()));

		World.getQuestManager().handleUseItemOnItem(item1, item2, player);

		ItemHerbSecond secondDef = null;
		if ((secondDef = EntityHandler.getItemHerbSecond(item1.getID(),
				item2.getID())) != null
				&& doHerbSecond(player, item1, item2, secondDef)) {
			return;
		} else if ((secondDef = EntityHandler.getItemHerbSecond(item2.getID(),
				item1.getID())) != null
				&& doHerbSecond(player, item2, item1, secondDef)) {
			return;
		}
		// water - empty water
		/*
		 * int[][] jugs = { {50, 21}, {141, 140}, {342, 341} }; if(item1.getID()
		 * == 136 || item2.getID() == 136) { for(int i=0; i < jugs.length; i++)
		 * { if(item2.getID() == jugs[i][0]) {
		 * if(player.getInventory().remove(new InvItem(136)) > -1 &&
		 * player.getInventory().remove(new InvItem(jugs[i][0])) > -1) {
		 * player.getInventory().add(new InvItem(jugs[i][1]));
		 * player.getInventory().add(new InvItem(135));
		 * player.getInventory().add(new InvItem(250));
		 * player.getActionSender().sendInventory();
		 * player.getActionSender().sendMessage("You create pastry dough");
		 * return; } } } }
		 */
		// dish ingred id - uncooked dish id
		int[][] stuff = { { 252, 254 }, { 132, 255 }, { 236, 256 } };
		if (item1.getID() == 253 || item2.getID() == 253) {
			for (int i = 0; i < stuff.length; i++) {
				if (stuff[i][0] == item1.getID()
						|| item2.getID() == stuff[i][0]) {
					if (player.getInventory().remove(new InvItem(253)) > -1
							&& player.getInventory().remove(
									new InvItem(stuff[i][0])) > -1) {
						player.getInventory().add(new InvItem(stuff[i][1]));
						player.getActionSender().sendMessage(
								"You create an uncooked pie!");
						player.getActionSender().sendInventory();
						return;
					}
				}
			}
		}
		if (item1.getID() == 238 && item2.getID() == 239
				|| item1.getID() == 239 && item2.getID() == 238) {
			if (player.getInventory().remove(new InvItem(239)) > -1
					&& player.getInventory().remove(new InvItem(238)) > -1) {
				player.getInventory().add(new InvItem(282));
				player.getActionSender().sendMessage("You mix the Dyes");
				player.getActionSender().sendInventory();
				return;
			}
		}
		// 1 dose on 2 dose str = 3 dose
		if (item1.getID() == 224 && item2.getID() == 223
				|| item1.getID() == 223 && item2.getID() == 224) {
			if (player.getInventory().remove(new InvItem(224)) > -1
					&& player.getInventory().remove(new InvItem(223)) > -1) {
				player.getInventory().add(new InvItem(222));
				player.getActionSender().sendMessage(
						"You mix the strength potions");
				player.getActionSender().sendInventory();
				return;
			}
		}
		// 1 dose on 3 dose = 4 dose
		if (item1.getID() == 224 && item2.getID() == 222
				|| item1.getID() == 222 && item2.getID() == 224) {
			if (player.getInventory().remove(new InvItem(224)) > -1
					&& player.getInventory().remove(new InvItem(222)) > -1) {
				player.getInventory().add(new InvItem(221));
				player.getActionSender().sendMessage(
						"You mix the strength potions");
				player.getActionSender().sendInventory();
				return;
			}
		}

		// 2 dose on 2 dose = 4 dose
		if (item1.getID() == 223 && item2.getID() == 223
				|| item1.getID() == 223 && item2.getID() == 223) {
			if (player.getInventory().remove(new InvItem(223)) > -1
					&& player.getInventory().remove(new InvItem(223)) > -1) {
				player.getInventory().add(new InvItem(221));
				player.getActionSender().sendMessage(
						"You mix the strength potions");
				player.getActionSender().sendInventory();
				return;
			}
		}
		if (item1.getID() == 224 && item2.getID() == 224
				|| item1.getID() == 224 && item2.getID() == 224) {
			if (player.getInventory().remove(new InvItem(224)) > -1
					&& player.getInventory().remove(new InvItem(224)) > -1) {
				player.getInventory().add(new InvItem(223));
				player.getActionSender().sendMessage(
						"You mix the strength potions");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.getID() == 132 && item2.getID() == 342
				|| item1.getID() == 342 && item2.getID() == 132) {
			if (player.getInventory().remove(new InvItem(342)) > -1
					&& player.getInventory().remove(new InvItem(132)) > -1) {
				player.getInventory().add(new InvItem(344));
				player.getActionSender().sendMessage(
						"You start to create a stew");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.getID() == 348 && item2.getID() == 342
				|| item1.getID() == 342 && item2.getID() == 348) {
			if (player.getInventory().remove(new InvItem(342)) > -1
					&& player.getInventory().remove(new InvItem(348)) > -1) {
				player.getInventory().add(new InvItem(343));
				player.getActionSender().sendMessage(
						"You start to create a stew");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.getID() == 132 && item2.getID() == 343
				|| item1.getID() == 343 && item2.getID() == 132) {
			if (player.getInventory().remove(new InvItem(343)) > -1
					&& player.getInventory().remove(new InvItem(132)) > -1) {
				player.getInventory().add(new InvItem(345));
				player.getActionSender().sendMessage(
						"Your stew is now ready, but uncooked");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.getID() == 348 && item2.getID() == 344
				|| item1.getID() == 344 && item2.getID() == 348) {
			if (player.getInventory().remove(new InvItem(344)) > -1
					&& player.getInventory().remove(new InvItem(348)) > -1) {
				player.getInventory().add(new InvItem(345));
				player.getActionSender().sendMessage(
						"our stew is now ready, but uncooked");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.getID() == 337 && item2.getID() == 330
				|| item1.getID() == 330 && item2.getID() == 337) {
			if (player.getInventory().remove(new InvItem(337)) > -1
					&& player.getInventory().remove(new InvItem(330)) > -1) {
				player.getInventory().add(new InvItem(332));
				player.getActionSender().sendMessage(
						"You add chocolate to the cake");
				player.getActionSender().sendInventory();
				return;
			}
		}

		int egg = 19;
		int milk = 22;
		int flour = 136;

		if (item1.getID() == 338 || item2.getID() == 338) {
			if (player.getInventory().countId(egg) > -1
					&& player.getInventory().countId(milk) > -1
					&& player.getInventory().countId(flour) > -1) {
				if (player.getInventory().remove(new InvItem(egg)) > -1
						&& player.getInventory().remove(new InvItem(milk)) > -1
						&& player.getInventory().remove(new InvItem(flour)) > -1
						&& player.getInventory().remove(new InvItem(338)) > -1) {
					player.getInventory().add(new InvItem(135));
					player.getInventory().add(new InvItem(339));
					player.getActionSender().sendInventory();
					player.getActionSender().sendMessage(
							"You create an uncooked cake");
					return;
				}
			}
		}
		if (item1.getID() == 238 && item2.getID() == 239
				|| item1.getID() == 239 && item2.getID() == 238) {
			if (player.getInventory().remove(new InvItem(239)) > -1
					&& player.getInventory().remove(new InvItem(238)) > -1) {
				player.getInventory().add(new InvItem(282));
				player.getActionSender().sendMessage("You mix the Dyes");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.getID() == 250 && item2.getID() == 251
				|| item1.getID() == 251 && item2.getID() == 250) {
			if (player.getInventory().remove(new InvItem(251)) > -1
					&& player.getInventory().remove(new InvItem(250)) > -1) {
				player.getInventory().add(new InvItem(253));
				player.getActionSender().sendMessage(
						"You add the pastry dough in the dish");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.getID() == 238 && item2.getID() == 272
				|| item1.getID() == 272 && item2.getID() == 238) {
			if (player.getInventory().remove(new InvItem(272)) > -1
					&& player.getInventory().remove(new InvItem(238)) > -1) {
				player.getInventory().add(new InvItem(516));
				player.getActionSender().sendMessage("You mix the Dyes");
				player.getActionSender().sendInventory();
				return;
			}
		}
		if (item1.getID() == 239 && item2.getID() == 272
				|| item1.getID() == 272 && item2.getID() == 239) {
			if (player.getInventory().remove(new InvItem(272)) > -1
					&& player.getInventory().remove(new InvItem(239)) > -1) {
				player.getInventory().add(new InvItem(515));
				player.getActionSender().sendMessage("You mix the Dyes");
				player.getActionSender().sendInventory();
				return;
			}
		}

		else if (item1.getID() == 1276 && item2.getID() == 1277) {
			if (player.getInventory().remove(new InvItem(1276)) > -1
					&& player.getInventory().remove(new InvItem(1277)) > -1) {
				player.getActionSender().sendMessage(
						"You combine the two parts.");
				player.getInventory().add(new InvItem(1278));
				player.getActionSender().sendInventory();
				return;
			}
		}// here
		else if (item1.getID() == 143 && item2.getID() == 141
				|| item1.getID() == 141 && item2.getID() == 143) {
			if (player.getCurStat(7) < 35) {
				player.getActionSender().sendMessage(
						"You need level 35 cooking to do this");
				return;
			}
			if (player.getInventory().remove(new InvItem(141)) > -1
					&& player.getInventory().remove(new InvItem(143)) > -1) {
				int rand = Formulae.Rand(0, 4);
				if (rand == 2) {
					player.incExp(7, 55, true);
					player.getInventory().add(new InvItem(180));
					player.getActionSender()
							.sendMessage(
									"You mix the grapes, and accidentally create Bad wine!");
				} else {
					player.incExp(7, 110, true);
					player.getInventory().add(new InvItem(142));
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
			if (il == item1.getID()) {
				for (int i = 0; i < dye.length; i++) {
					if (dye[i] == item2.getID()) {
						if (player.getInventory().remove(
								new InvItem(item1.getID())) > -1
								&& player.getInventory().remove(
										new InvItem(item2.getID())) > -1) {
							player.getActionSender().sendMessage(
									"You dye the Cape");
							player.getInventory().add(new InvItem(newCapes[i]));
							player.getActionSender().sendInventory();
							return;
						}
					}
				}
			} else if (il == item2.getID()) {
				for (int i = 0; i < dye.length; i++) {
					if (dye[i] == item1.getID()) {
						if (player.getInventory().remove(
								new InvItem(item1.getID())) > -1
								&& player.getInventory().remove(
										new InvItem(item2.getID())) > -1) {
							player.getActionSender().sendMessage(
									"You dye the Cape");
							player.getInventory().add(new InvItem(newCapes[i]));
							player.getActionSender().sendInventory();
							return;
						}
					}
				}
			}
		}
		if (item1.getID() == 141 && item2.getID() == 136
				|| item1.getID() == 136 && item2.getID() == 141) {
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
					if (owner.getInventory().remove(new InvItem(141)) > -1
							&& owner.getInventory().remove(new InvItem(136)) > -1) {
						owner.getActionSender().sendMessage(
								"You create a " + reply);
						owner.getInventory().add(new InvItem(140));
						owner.getInventory().add(new InvItem(135));
						owner.getInventory().add(new InvItem(newid));
						owner.getActionSender().sendInventory();
					}

				}
			});
			player.getActionSender().sendMenu(optionsz);
			return;
		}

		if (item1.getID() == 50 && item2.getID() == 136 || item1.getID() == 136
				&& item2.getID() == 50) {
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
					if (owner.getInventory().remove(new InvItem(50)) > -1
							&& owner.getInventory().remove(new InvItem(136)) > -1) {
						owner.getActionSender().sendMessage(
								"You create a " + reply);
						owner.getInventory().add(new InvItem(21));
						owner.getInventory().add(new InvItem(135));
						owner.getInventory().add(new InvItem(newid));
						owner.getActionSender().sendInventory();
					}

				}
			});
			player.getActionSender().sendMenu(optionsz);
			return;
		}

		if (item1.getID() == 273 && item2.getID() == 282
				|| item1.getID() == 282 && item2.getID() == 273) {
			if (player.getInventory().remove(new InvItem(282)) > -1
					&& player.getInventory().remove(new InvItem(273)) > -1) {
				player.getInventory().add(new InvItem(274));
				player.getActionSender()
						.sendMessage("You dye the goblin armor");
				player.getActionSender().sendInventory();
				return;
			}
		}
		if (item1.getID() == 273 && item2.getID() == 272
				|| item1.getID() == 272 && item2.getID() == 273) {
			if (player.getInventory().remove(new InvItem(272)) > -1
					&& player.getInventory().remove(new InvItem(273)) > -1) {
				player.getInventory().add(new InvItem(275));
				player.getActionSender()
						.sendMessage("You dye the goblin armor");
				player.getActionSender().sendInventory();
				return;
			}
		}

		if (item1.getID() == 320 && item2.getID() == 321
				|| item1.getID() == 321 && item2.getID() == 320) {
			if (player.getInventory().remove(new InvItem(321)) > -1
					&& player.getInventory().remove(new InvItem(320)) > -1) {
				player.getInventory().add(new InvItem(323));
				player.getActionSender().sendMessage(
						"You add the Tomato to the Pizza base");
				player.getActionSender().sendInventory();
				return;
			}
		}
		if (item1.getID() == 319 && item2.getID() == 323
				|| item1.getID() == 323 && item2.getID() == 319) {
			if (player.getInventory().remove(new InvItem(323)) > -1
					&& player.getInventory().remove(new InvItem(319)) > -1) {
				player.getInventory().add(new InvItem(324));
				player.getActionSender().sendMessage(
						"You add Cheese on the Unfinished Pizza");
				player.getActionSender().sendInventory();
				return;
			}
		}
		if (item1.getID() == 325 && item2.getID() == 352
				|| item1.getID() == 352 && item2.getID() == 325) {
			if (player.getCurStat(7) > 54) {
				if (player.getInventory().remove(new InvItem(352)) > -1
						&& player.getInventory().remove(new InvItem(325)) > -1) {
					player.getInventory().add(new InvItem(327));
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
		if (item1.getID() == 325 && item2.getID() == 132
				|| item1.getID() == 132 && item2.getID() == 325) {
			if (player.getCurStat(7) > 44) {
				if (player.getInventory().remove(new InvItem(132)) > -1
						&& player.getInventory().remove(new InvItem(325)) > -1) {
					player.getInventory().add(new InvItem(326));
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
		if (item2.getID() == 1276 && item1.getID() == 1277) {
			if (player.getInventory().remove(new InvItem(1276)) > -1
					&& player.getInventory().remove(new InvItem(1277)) > -1) {
				player.getActionSender().sendMessage(
						"You combine the two parts.");
				player.getInventory().add(new InvItem(1278));
				player.getActionSender().sendInventory();
				return;
			}
		} else if (item1.getID() == 381 && attachFeathers(player, item1, item2)) {
			return;
		} else if (item2.getID() == 381 && attachFeathers(player, item2, item1)) {
			return;
		} else if (item1.getID() == 167 && doCutGem(player, item1, item2)) {
			return;
		} else if (item2.getID() == 167 && doCutGem(player, item2, item1)) {
			return;
		} else if (item1.getID() == 13 && doLogCut(player, item1, item2)) {
			return;
		} else if (item2.getID() == 13 && doLogCut(player, item2, item1)) {
			return;
		} else if (item1.getID() == 464 && doHerblaw(player, item1, item2)) {
			return;
		} else if (item2.getID() == 464 && doHerblaw(player, item2, item1)) {
			return;
		} else if (item1.getID() == 676 && doBowString(player, item1, item2)) {
			return;
		} else if (item2.getID() == 676 && doBowString(player, item2, item1)) {
			return;
		} else if (item1.getID() == 637 && doArrowHeads(player, item1, item2)) {
			return;
		} else if (item2.getID() == 637 && doArrowHeads(player, item2, item1)) {
			return;
		} else if (item1.getID() == 468 && doGrind(player, item1, item2)) {
			return;
		} else if (item2.getID() == 468 && doGrind(player, item2, item1)) {
			return;
		} else if (item1.getID() == 207 && useWool(player, item1, item2)) {
			return;
		} else if (item2.getID() == 207 && useWool(player, item2, item1)) {
			return;
		} else if (item1.getID() == 39 && makeLeather(player, item1, item2)) {
			return;
		} else if (item2.getID() == 39 && makeLeather(player, item2, item1)) {
			return;
		} else if (item1.getID() == 621 && doGlassBlowing(player, item1, item2)) {
			return;
		} else if (item2.getID() == 621 && doGlassBlowing(player, item2, item1)) {
			return;
		} else if ((item1.getID() == 50 || item1.getID() == 141 || item1
				.getID() == 342) && useWater(player, item1, item2)) {
			return;
		} else if ((item2.getID() == 50 || item2.getID() == 141 || item2
				.getID() == 342) && useWater(player, item2, item1)) {
			return;
		}

		else if (item1.getID() == 526 && combineKeys(player, item1, item2)) {
			return;
		} else if (item2.getID() == 526 && combineKeys(player, item2, item1)) {
			return;
		} else if ((item1.getID() == 23 && item2.getID() == 135)
				|| (item2.getID() == 23 && item1.getID() == 135)) {
			if (player.getInventory().remove(new InvItem(23)) > -1
					&& player.getInventory().remove(new InvItem(135)) > -1) {
				player.getInventory().add(new InvItem(136));
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
				if ((item1.getID() == combinePotions[i][0] && item2.getID() == combinePotions[i][1])
						|| (item2.getID() == combinePotions[i][0] && item1
								.getID() == combinePotions[i][1])) {
					if (!Config.members) {
						player.getActionSender().sendMessage(
								"This feature is not avaliable in f2p");
						return;
					}
					if (player.getInventory().remove(
							new InvItem(combinePotions[i][0])) > -1
							&& player.getInventory().remove(
									new InvItem(combinePotions[i][1])) > -1) {
						player.getInventory().add(
								new InvItem(combinePotions[i][2]));
						player.getActionSender().sendInventory();
						player.getActionSender().sendMessage(
								"You combine the Potions");
						return;
					}
				} else if (item1.getID() == combinePotions[i][1]
						&& item2.getID() == combinePotions[i][1]) {
					if (!Config.members) {
						player.getActionSender().sendMessage(
								"This feature is not avaliable in f2p");
						return;
					}
					if (player.getInventory().remove(
							new InvItem(combinePotions[i][1])) > -1
							&& player.getInventory().remove(
									new InvItem(combinePotions[i][1])) > -1) {
						if (!Server.isMembers()) {
							player.getActionSender().sendMessage(
									"This feature is not avaliable in f2p");
							return;
						}
						player.getInventory().add(
								new InvItem(combinePotions[i][0]));
						player.getActionSender().sendInventory();
						player.getActionSender().sendMessage(
								"You combine the Potions");
						return;
					} else if (item1.getID() == combinePotions[i][0]
							&& item2.getID() == combinePotions[i][0]) {
						if (!Config.members) {
							player.getActionSender().sendMessage(
									"This feature is not avaliable in f2p");
							return;
						}
						if (player.getInventory().remove(
								new InvItem(combinePotions[i][0])) > -1
								&& player.getInventory().remove(
										new InvItem(combinePotions[i][0])) > -1) {
							player.getInventory().add(
									new InvItem(combinePotions[i][2]));
							player.getInventory().add(
									new InvItem(combinePotions[i][1]));
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
		if (leather.getID() != 148) {
			return false;
		}
		if (player.getInventory().countId(43) < 1) {
			player.getActionSender().sendMessage(
					"You need some thread to make anything out of leather");
			return true;
		}
		if (DataConversions.random(0, 5) == 0) {
			player.getInventory().remove(43, 1);
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
						if (owner.getInventory().remove(leather) > -1) {
							owner.getActionSender().sendMessage(
									"You make some "
											+ result.getDef().getName());
							owner.getInventory().add(result);
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
		int jugID = Formulae.getEmptyJug(water.getID());
		if (jugID == -1) { // This shouldn't happen
			return false;
		}
		switch (item.getID()) {
		case 149: // Clay
			if (player.getInventory().remove(water) > -1
					&& player.getInventory().remove(item) > -1) {
				player.getActionSender().sendMessage("You soften the clay.");
				player.getInventory().add(new InvItem(jugID, 1));
				player.getInventory().add(new InvItem(243, 1));
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
		switch (item.getID()) {
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
				if (owner.getInventory().remove(woolBall) > -1
						&& owner.getInventory().remove(item) > -1) {
					owner.getActionSender()
							.sendMessage("You string the amulet");
					owner.getInventory().add(new InvItem(newId, 1));
					owner.getActionSender().sendInventory();
				}
			}
		});
		return true;
	}
}
