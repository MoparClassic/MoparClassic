package org.moparscape.msc.gs.plugins.quests;

import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Item;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.quest.Quest;
import org.moparscape.msc.gs.quest.QuestAction;

/**
 * Quest: Goblin Diplomacy (v1.0) 8/5/2009 (uid: 10) Status: COMPLETE (untested)
 * Start: Bartender (id 150) 255, 628 Goblins (323, 448) Items: Goblin armour
 * (273) Orange goblin armour (274) Blue goblin armour (275) Reward: 5 quest
 * points, 1000 crafting exp and 1 gold bar
 * 
 * TODO: restrict armours from being seen by players not on the quest
 * 
 * @author punKrockeR
 */
public class GoblinDiplomacy extends Quest {
	private static final int QUEST_POINTS = 5;
	private static final int REWARD_EXP = 1000;
	private static final int BENTNOZE_ID = 152;
	private static final int WARTFACE_ID = 151;
	private static final int BARTENDER_ID = 150;
	private static final int WYSON_ID = 116;
	private static final int AGGIE_ID = 125;
	private static final int WOAD_LEAF_ID = 281;
	private static final int BERRIES_ID = 236;
	private static final int ONION_ID = 241;
	private static final int ORANGE_ARMOUR_ID = 274;
	private static final int GOBLIN_ARMOUR_ID = 273;
	private static final int RED_DYE_ID = 238;
	private static final int YELLOW_DYE_ID = 239;
	private static final int BLUE_DYE_ID = 272;
	private static final int ORANGE_DYE_ID = 282;
	private static final int BLUE_ARMOUR_ID = 275;

	public void init() {
		associateNpc(BENTNOZE_ID);
		associateNpc(WARTFACE_ID);
		associateNpc(BARTENDER_ID);
		// associateNpc(WYSON_ID);
		associateNpc(AGGIE_ID);
		associateItem(GOBLIN_ARMOUR_ID);
		associateItem(RED_DYE_ID);
		associateItem(YELLOW_DYE_ID);
		associateItem(BLUE_DYE_ID);
		associateItem(ORANGE_DYE_ID);
		// associateItem(ORANGE_ARMOUR_ID);
	}

	/**
	 * @return if the given Item is visible to the player
	 */
	public boolean isItemVisible(Item item, Player player) {
		int stage = player.getQuestStage(this);

		switch (item.getID()) {
		case ORANGE_ARMOUR_ID:
		case BLUE_ARMOUR_ID:
			return stage >= 1;
		default:
			return true;
		}
	}

	public String getName() {
		return "Goblin Diplomacy";
	}

	public int getUniqueID() {
		return 10;
	}

	public void handleAction(QuestAction action, Object[] args,
			final Player player) {

		if (action == QuestAction.TALKED_NPC) {
			if (!(args[0] instanceof Npc))
				return;

			final Npc npc = (Npc) args[0];

			if (npc.getID() == BENTNOZE_ID)
				handleBentnozeChat(npc, player);
			else if (npc.getID() == WARTFACE_ID)
				handleWartfaceChat(npc, player);
			else if (npc.getID() == BARTENDER_ID)
				handleBartenderChat(npc, player);
			else if (npc.getID() == WYSON_ID)
				handleWysonChat(npc, player);
			else if (npc.getID() == AGGIE_ID)
				handleAggieChat(npc, player);
		} else if (action == QuestAction.ITEM_USED_ON_ITEM) {
			if (!(args[0] instanceof InvItem) && !(args[1] instanceof InvItem))
				return;

			final InvItem item1 = (InvItem) args[0];
			final InvItem item2 = (InvItem) args[1];

			if ((item1.getID() == ORANGE_DYE_ID && item2.getID() == GOBLIN_ARMOUR_ID)
					|| (item1.getID() == GOBLIN_ARMOUR_ID && item2.getID() == ORANGE_DYE_ID)) {
				player.setBusy(true);
				sendMessage(player, "You dye the goblin armour orange");
				sleep(1200);
				takeItem(player, item1, item2);
				giveItem(player, ORANGE_ARMOUR_ID);
				player.setBusy(false);
				return;
			} else if ((item1.getID() == BLUE_DYE_ID && item2.getID() == GOBLIN_ARMOUR_ID)
					|| (item1.getID() == GOBLIN_ARMOUR_ID && item2.getID() == BLUE_DYE_ID)) {
				player.setBusy(true);
				sendMessage(player, "You dye the goblin armour dark blue");
				sleep(1200);
				takeItem(player, item1, item2);
				giveItem(player, BLUE_ARMOUR_ID);
				player.setBusy(false);
				return;
			} else if ((item1.getID() == RED_DYE_ID && item2.getID() == YELLOW_DYE_ID)
					|| (item1.getID() == YELLOW_DYE_ID && item2.getID() == RED_DYE_ID)) {
				player.setBusy(true);
				sendMessage(player,
						"You mix the two dyes and make an orange dye");
				sleep(1200);
				takeItem(player, item1, item2);
				giveItem(player, ORANGE_DYE_ID);
				player.setBusy(false);
				return;
			}
		}
	}

	private void handleAggieChat(final Npc npc, final Player player) {
		sendChat("What can I help you with?", npc, player);

		player.setBusy(false);
		int option = getMenuOption(player, "What could you make for me",
				"Cool, do you turn people into frogs?",
				"You mad old witch, you can't help me",
				"Can you make dyes for me please");
		if (option == -1)
			return;
		player.setBusy(true);
		sleep();

		if (option == 0) {
			sendChat("I mostly just make what i find pretty", npc, player);
			sendChat(
					"I sometimes make dye for the womens clothes, brighten the place up",
					npc, player);
			sendChat(
					"I can make red, yellow and blue dyes. would you like some?",
					npc, player);

			player.setBusy(false);
			option = getMenuOption(player,
					"What do you need to make some red dye please",
					"What do you need to make some yellow dye please",
					"What do you need to make some blue dye please",
					"No thanks, I am happy the colour I am");
			if (option == -1)
				return;
			player.setBusy(true);
			sleep();

			if (option == 0) {
				redDye(npc, player);
			} else if (option == 1) {
				yellowDye(npc, player);
			} else if (option == 2) {
				blueDye(npc, player);
			} else {
				sendChat("You are easily pleased with yourself then", npc,
						player);
				sendChat("When you need dyes, come to me", npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		} else if (option == 1) {
			sendChat("Oh, not for years, but if you meet a talking chicken,",
					npc, player);
			sendChat(
					"You have probably met the professor in the manor north of here",
					npc, player);
			sendChat(
					"A few years ago it was flying fish, that machine is a menace",
					npc, player, 1200);
			player.setBusy(false);
			npc.unblock();
			return;
		} else if (option == 2) {
			sendChat("Oh, you like to call a witch names, do you?", npc, player);

			if (countItem(player, 10) >= 20) {
				sendMessage(player,
						"Aggie waves her hands about, and you seem to be 20 coins poorer");
				takeItem(player, new InvItem(10, 20));
				sleep();
				sendChat(
						"Thats a fine for insulting a witch, you should learn some respect",
						npc, player, 1200);
			} else {
				sendChat("You should be careful about insulting a witch", npc,
						player);
				sendChat("You never know what shape you could wake up in", npc,
						player, 1200);
			}

			player.setBusy(false);
			npc.unblock();
			return;
		} else {
			sendChat("What sort of dye would you like? red, yellow or blue?",
					npc, player);

			player.setBusy(false);
			option = getMenuOption(player,
					"What do you need to make some red dye please",
					"What do you need to make some yellow dye please",
					"What do you need to make some blue dye please",
					"None thanks");
			if (option == -1)
				return;
			player.setBusy(true);
			sleep();

			if (option == 0) {
				redDye(npc, player);
			} else if (option == 1) {
				yellowDye(npc, player);
			} else if (option == 2) {
				blueDye(npc, player);
			} else {
				sendChat("Suit yourself", npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		}
	}

	private void redDye(final Npc npc, final Player player) {
		sendChat("3 Lots of red berries, and 5 coins, to you", npc, player);
		handleDye("red", BERRIES_ID, 3, "berries", 238, npc, player);
	}

	private void yellowDye(final Npc npc, final Player player) {
		sendChat("Yellow is a strange colour to get, comes from onion skins",
				npc, player);
		sendChat("I need 2 onions, and 5 coins to make yellow", npc, player);
		handleDye("yellow", ONION_ID, 2, "onions", YELLOW_DYE_ID, npc, player);
	}

	private void blueDye(final Npc npc, final Player player) {
		sendChat("2 Woad leaves, and 5 coins, to you", npc, player);
		handleDye("blue", WOAD_LEAF_ID, 2, "leaves", BLUE_DYE_ID, npc, player);
	}

	private void handleDye(String colour, int itemId, int amount,
			String itemName, int dyeId, final Npc npc, final Player player) {
		player.setBusy(false);
		int option = getMenuOption(player, "Okay, make me some " + colour
				+ " dye please",
				"I don't think I have all the ingredients yet",
				"I can do without dye at that price");
		if (option == -1)
			return;
		player.setBusy(true);
		sleep();

		if (option == 0) {
			if (countItem(player, itemId) < amount) {
				sendChat(
						"Ah wait, sorry. I don't have all the ingredients yet",
						player, npc);
				sendChat(
						"You know what you need to get now, come back when you have them",
						npc, player);
				sendChat("Goodbye for now", npc, player, 2000);
				player.setBusy(false);
				npc.unblock();
				sendMessage(player, "You don't have enough " + itemName
						+ " for the dye!");
				return;
			}

			if (countItem(player, 10) < 5) {
				sendChat("Ah wait, sorry, I don't have enough coins on me",
						player, npc);
				sendChat(
						"Well come back when you do and I'll make you some dye",
						npc, player, 2000);
				sendMessage(player, "You don't have enough coins for the dye!");
				player.setBusy(false);
				npc.unblock();
				return;
			}

			sendMessage(player, "You hand the " + itemName
					+ " and payment to Aggie");
			player.getActionSender().sendSound("coin");
			takeItem(player, new InvItem(10, 5), new InvItem(itemId, 1));
			sleep();
			sendMessage(player,
					"She takes a red bottle from nowhere and hands it to you");
			giveItem(player, dyeId);
			sleep();
			sendChat("Thank you", player, npc, 1200);
			player.setBusy(false);
			npc.unblock();
			return;
		} else if (option == 1) {
			sendChat(
					"You know what you need to get now, come back when you have them",
					npc, player);
			sendChat("Goodbye for now", npc, player, 1200);
			player.setBusy(false);
			npc.unblock();
			return;
		} else if (option == 2) {
			sendChat(
					"That's your choice, but I would think you have killed for less",
					npc, player);
			sendChat("I can see it in your eyes", npc, player, 1200);
			player.setBusy(false);
			npc.unblock();
		}
	}

	private void handleWysonChat(final Npc npc, final Player player) {
		sendChat("I am the gardener round here", npc, player);
		sendChat("Do you have any gardening that needs doing?", npc, player);

		if (getQuestStage(player) <= 1 || getQuestStage(player) == COMPLETE) {
			sendChat("Not right now thanks", player, npc, 1200);
			player.setBusy(false);
			npc.unblock();
			return;
		}

		player.setBusy(false);
		int option = getMenuOption(player, "I'm looking for woad leaves",
				"Not right now thanks");
		if (option == -1)
			return;
		player.setBusy(true);
		sleep();

		if (option == 0) {
			sendChat(
					"Well luckily for you I may have some around here somewhere",
					npc, player);
			sendChat("Can I buy one please?", player, npc);
			sendChat("How much are you willing to pay?", npc, player);

			player.setBusy(false);
			option = getMenuOption(player, "How about 5 coins?",
					"How about 10 coins?", "How about 15 coins?",
					"How about 20 coins?");
			if (option == -1)
				return;
			player.setBusy(true);
			sleep();

			if (option == 0 || option == 1) // 5 or 10
			{
				sendChat(
						"No no thats far too little. Woad leaves are hard to get you know",
						npc, player);
				sendChat(
						"I used to have plenty but someone kept stealing them off me",
						npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
			if (option == 2) // 15
			{
				sendChat("Mmmm ok that sounds fair.", npc, player);

				if (countItem(player, 10) < 15) {
					sendChat(
							"I dont have enough coins to buy the leaves. I'll come back later",
							player, npc, 1200);
					player.setBusy(false);
					npc.unblock();
					return;
				}

				takeItem(player, new InvItem(10, 15));
				sendMessage(player, "You give Wyson 15 coins");
				sleep();
				giveItem(player, WOAD_LEAF_ID);
				sendMessage(player,
						"Wyson the gardener gives you some woad leaves");
				sleep(1200);
				player.setBusy(false);
				npc.unblock();
			} else // 20
			{
				sendChat("Ok that's more than fair.", npc, player);
				takeItem(player, new InvItem(10, 20));
				sendMessage(player, "You give Wyson 20 coins");
				sleep();
				takeItem(player, WOAD_LEAF_ID);
				sendMessage(player,
						"Wyson the gardener gives you some woad leaves");
				sleep();
				sendChat("Here have some more you're a generous person", npc,
						player);
				takeItem(player, WOAD_LEAF_ID);
				sendMessage(player,
						"Wyson the gardener gives you some more leaves");
				sleep(1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		} else {
			player.setBusy(false);
			npc.unblock();
		}
	}

	private void handleBartenderChat(final Npc npc, final Player player) {
		int stage = getQuestStage(player);

		if (stage == -1) {
			sendChat("Hi there how may i help you", npc, player);

			player.setBusy(false);
			int option = getMenuOption(player, "Could I buy a beer please?",
					"Not very busy in here today is it?");
			if (option == -1)
				return;
			player.setBusy(true);
			sleep();

			if (option == 0) {
				sendChat("Sure that will be 2 gold coins please", npc, player);

				if (countItem(player, 10) >= 2) {
					sendChat("Ok here you go thanks", player, npc, 2000);
					player.getActionSender().sendMessage("You buy a beer");
					takeItem(player, new InvItem(10, 2));
					giveItem(player, 193);
					sleep(1200);
					player.setBusy(false);
					npc.unblock();
					return;
				} else {
					player.getActionSender().sendMessage(
							"You don't have enough coins to buy the beer!");
					sleep(1200);
					player.setBusy(false);
					npc.unblock();
					return;
				}
			} else {
				queueChat(
						npc,
						player,
						"No it was earlier",
						"There was a guy in here saying the goblins up by the mountain",
						"Are arguing again",
						"Of all things about the colour of their armour.",
						"Knowing the goblins, it could easily turn into a full blown war",
						"Which wouldn't be good",
						"Goblin wars make such a mess of the countryside");
				sendChat(
						"Well if I have time I'll see if I can go and knock some sense into them",
						player, npc, 1200);
				setStage(player, 1); // Start quest
				player.setBusy(false);
				npc.unblock();
			}
		} else {
			player.setBusy(false);
			int option = getMenuOption(player, "Could I buy a beer please?",
					stage == COMPLETE ? "I fixed the goblin problem"
							: "I'm negotiating with the goblins");
			if (option == -1)
				return;
			player.setBusy(true);
			sleep();

			if (option == 0) {
				sendChat("Sure that will be 2 gold coins please", npc, player);

				if (countItem(player, 10) >= 2) {
					sendChat("Ok here you go thanks", player, npc, 2000);
					player.getActionSender().sendMessage("You buy a beer");
					takeItem(player, new InvItem(10, 2));
					giveItem(player, 193);
					sleep(1200);
					player.setBusy(false);
					npc.unblock();
					return;
				} else {
					player.getActionSender().sendMessage(
							"You don't have enough coins to buy the beer!");
					sleep(1200);
					player.setBusy(false);
					npc.unblock();
					return;
				}
			} else {
				if (stage == COMPLETE) {
					sendChat("Thanks lad", npc, player);
					sendChat("Hard work like that deserves a beer", npc,
							player, 2000);
					player.getActionSender().sendMessage(
							"The bartender hands you a free beer");
					giveItem(player, 193);
					sleep(1200);
					sendChat("Cheers mate", player, npc, 1200);
					player.setBusy(false);
					npc.unblock();
				} else {
					sendChat("Well goodluck with that", npc, player);
					sendChat(
							"If you sort it out, come back and I'll shout you a beer on the house",
							npc, player, 1200);
					player.setBusy(false);
					npc.unblock();
					return;
				}
			}
		}
	}

	private void handleBentnozeChat(final Npc npc, final Player player) {
		final Npc wartface = World.getWorld().getNpc(WARTFACE_ID, 314, 330,
				441, 457);

		if (wartface == null)
			return;

		handleWartfaceChat(wartface, player);
	}

	private void handleWartfaceChat(final Npc npc, final Player player) {
		int stage = player.getQuestStage(this);
		final Npc bentnoze = World.getWorld().getNpc(BENTNOZE_ID, 314, 330,
				441, 457);

		if (stage == COMPLETE) {
			sendChat(
					"Now you've solved our argument we gotta think of something else to do",
					npc, player);
			sendChat("Yep, we bored now", bentnoze, player, 1200);
			npc.unblock();
			player.setBusy(false);
			return;
		}

		argue(player);

		if (npc == null)
			return;

		if (stage == -1 || stage == 1) {
			if (stage == 1)
				startQuest(npc, player);
		} else if (stage == 2) {
			sendChat("Oh it you", npc, player, 2000);
			sendChat("Have you got some orange goblin armour yet?", npc, player);

			if (hasItem(player, ORANGE_ARMOUR_ID)) {
				sendChat("Yeah I have it right here", player, npc);
				sendMessage(player, "You hand Wartface the armour");
				takeItem(player, ORANGE_ARMOUR_ID);
				sleep();
				sendChat("No I don't like that much", npc, player);
				sendChat("It clashes with my skin colour", bentnoze, player);
				sendChat("Try bringing us dark blue armour", npc, player, 1200);
				player.setQuestStage(this, 3);
				player.setBusy(false);
				npc.unblock();
				bentnoze.unblock();
				return;
			} else {
				sendChat("Err no", player, npc, 2000);
				sendChat("Come back when you have some", npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		} else if (stage == 3) {
			sendChat("Oh it you", npc, player, 2000);
			sendChat("Have you got us some dark blue goblin armour?", npc,
					player);

			if (hasItem(player, BLUE_ARMOUR_ID)) {
				sendChat("Yes, here you go", player, npc);
				sendMessage(player, "You hand Wartface the armour");
				takeItem(player, BLUE_ARMOUR_ID);
				sleep();
				sendChat("Doesn't seem quite right", npc, player);
				sendChat("Maybe if it was a bit lighter", bentnoze, player);
				sendChat("Yeah try light blue", npc, player);
				sendChat("I thought that was the amour you were changing from",
						player, npc);
				sendChat(
						"But never mind, anything is worth a try to avoid a war",
						player, npc, 1200);

				player.setQuestStage(this, 4);
				player.setBusy(false);
				npc.unblock();
				bentnoze.unblock();
				return;
			} else {
				sendChat("Not yet", player, npc, 2000);
				sendChat("Come back when you have some", npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		} else if (stage == 4) {
			if (player.getQuestStage(this) == COMPLETE) {
				player.setBusy(false);
				npc.unblock();
				bentnoze.unblock();
				return;
			}
			sendChat("Oh it you", npc, player, 2000);
			sendChat("Have you got some light blue goblin armour yet?", npc,
					player);

			if (hasItem(player, GOBLIN_ARMOUR_ID)) {
				sendChat("Sigh...", player, npc, 2000);
				sendChat("Yes, here it is", player, npc);
				sendMessage(player, "You hand Wartface the armour");
				takeItem(player, GOBLIN_ARMOUR_ID);
				sleep();
				sendChat("That is rather nice", npc, player);
				sendChat("Yes i could see myself wearing somethin' like that",
						bentnoze, player);
				sendChat("It a deal then", npc, player);
				sendChat("Light blue it is", npc, player);
				sendChat("Thank you for sorting our argument", npc, player,
						1200);
				player.setQuestStage(this, COMPLETE);
				player.incQuestPoints(QUEST_POINTS);
				player.incExp(12, REWARD_EXP, false);
				sleep();
				sendMessage(player,
						"General Wartface gives you a gold bar as thanks");
				giveItem(player, 172);
				player.setBusy(false);
				npc.unblock();
				bentnoze.unblock();
				return;
			} else {
				sendChat("Not yet", player, npc, 2000);
				sendChat("Come back when you have some", npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		}
	}

	private void startQuest(final Npc npc, final Player player) {
		player.setBusy(false);
		int option = getMenuOption(player,
				"Why are you arguing about the colour of your armour?",
				"Wouldn't you prefer peace?",
				"Do you want me to pick an armour colour for you?");
		if (option == -1)
			return;
		player.setBusy(true);
		sleep();

		final Npc bentnoze = World.getWorld().getNpc(BENTNOZE_ID, 314, 330,
				441, 457);

		if (bentnoze == null) {
			player.getActionSender().sendMessage(
					"Quest error: Bentnoze missing");
			player.setBusy(false);
			return;
		}

		if (option == 0) {
			queueChat(npc, player, "We decide to celebrate goblin new century",
					"By changing the colour of our armour",
					"Light blue get boring after a bit", "And we want change",
					"Problem is they want different change to us");
			player.setBusy(false);
			npc.unblock();
			return;
		} else if (option == 1) {
			sendChat(
					"Yeah peace is good as long as it is peace wearing green armour",
					npc, player);
			sendChat("But green to much like skin!", bentnoze, player);
			sendChat("Nearly make you look naked!", bentnoze, player, 1200);
			player.setBusy(false);
			npc.unblock();
			bentnoze.unblock();
			return;
		} else {
			sendChat("Different to either green or red", player, npc);
			sendChat("Hmm me dunno what that'd look like", npc, player);
			sendChat("You'd have to bring me some, so us could decide", npc,
					player);
			sendChat("Yep bring us orange armour", bentnoze, player);
			sendChat("Yep orange might be good", npc, player, 1200);

			player.setQuestStage(this, 2);
			player.setBusy(false);
			npc.unblock();
			bentnoze.unblock();
			return;
		}
	}

	private Npc argue(final Player player) {
		final Npc wartface = World.getWorld().getNpc(WARTFACE_ID, 321, 445,
				326, 449);
		final Npc bentnoze = World.getWorld().getNpc(BENTNOZE_ID, 321, 445,
				326, 449);

		if (wartface == null || bentnoze == null) {
			player.setBusy(false);
			return null;
		}

		sendChat("Green armour best", wartface, player, 2000);
		sendChat("No, no red every time", bentnoze, player, 2000);
		sendChat("Go away human, we busy", wartface, player, 2000);
		player.setBusy(false);
		wartface.unblock();
		bentnoze.unblock();
		return wartface;
	}

	public GoblinDiplomacy() {
	}
}