package org.moparscape.msc.gs.plugins.quests;

import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.quest.Quest;
import org.moparscape.msc.gs.quest.QuestAction;

/**
 * Quest: Doric's Quest (v1.1) 4/5/2009 (uid: 7) Status: COMPLETE (untested)
 * Start: Doric (326, 490) Items: 6 x clay (149) 4 x copper (150) 2 x iron (151)
 * Reward: 1 quest point, 1500gp, 1000 smithing exp and use of Doric's anvils
 * 
 * @author punKrockeR
 */
public class Dorics extends Quest {
	public static final int DORIC_ID = 144;
	public static final int ANVIL1_ID = 177;
	public static final int ANVIL1_X = 327;
	public static final int ANVIL1_Y = 490;
	public static final int ANVIL2_ID = 177;
	public static final int ANVIL2_X = 327;
	public static final int ANVIL2_Y = 487;
	private static final int QUEST_POINTS = 1;
	private static final int REWARD_GP = 1500;
	private static final int REWARD_EXP = 1000;

	public void init() {
		associateNpc(DORIC_ID);
		// associateObject(ANVIL1_ID, ANVIL1_X, ANVIL1_Y);
		// associateObject(ANVIL2_ID, ANVIL2_X, ANVIL2_Y);
	}

	/*
	 * public boolean isObjectAssociated(GameObject object, Player player) {
	 * if(object.getID() == ANVIL1_ID && (object.getX() == ANVIL1_X &&
	 * object.getY() == ANVIL1_Y) || (object.getX() == ANVIL2_X && object.getY()
	 * == ANVIL2_Y)) return player.getQuestStage(getUniqueID()) != COMPLETE;
	 * 
	 * return false; }
	 */

	public String getName() {
		return "Doric's Quest";
	}

	public int getUniqueID() {
		return 7;
	}

	public void handleAction(QuestAction action, Object[] args,
			final Player player) {

		if (action == QuestAction.TALKED_NPC) {
			if (!(args[0] instanceof Npc))
				return;

			final Npc npc = (Npc) args[0];

			player.setBusy(true);
			npc.blockedBy(player);

			if (npc.getID() == DORIC_ID)
				handleDoricChat(npc, player);
		} else if (action == QuestAction.ITEM_USED_ON_OBJECT) {
			if (args.length < 2 || !(args[0] instanceof InvItem)
					|| !(args[1] instanceof GameObject))
				return;

		}
	}

	private void handleDoricChat(final Npc npc, final Player player) {
		int stage = player.getQuestStage(this);
		if (stage == Quest.COMPLETE) {
			sendChat("Be sure to use my anvils at anytime friend!", npc, player);
			sendChat("I will do that, thanks.", player, npc);
			player.setBusy(false);
			npc.unblock();
			return;
		}

		if (stage == -1) {
			sendChat("Why are you so grumpy little man?", player, npc);
			sendChat(
					"I'm sick of people sneaking into my house to use my anvils!",
					npc, player);
			sendChat("But they're the only anvils in this part of town",
					player, npc);
			sendChat("It's very selfish of you to keep them all to yourself",
					player, npc);
			sendChat("They're my anvils and it's my house", npc, player);
			sendChat(
					"So unless they earn my respect as a smither, I'll be as selfish as I want!",
					npc, player);

			player.setBusy(false);
			int option = getMenuOption(player, "How can I earn your respect?",
					"No wonder no one likes you", "But... I'm bigger than you");
			if (option == -1)
				return;
			player.setBusy(true);
			sleep();

			if (option == 0) {
				sendChat("Well, I can always use a helping hand", npc, player);
				sendChat("What do you need help with?", player, npc);
				sendChat("I'm crafting some amulets at the moment", npc, player);
				sendChat(
						"But I've just run out of materials to make them with",
						npc, player);
				sendChat("If you're serious about helping me", npc, player);
				sendChat("Then collecting them for me would be a great start",
						npc, player);

				player.setBusy(false);
				option = getMenuOption(player, "I don't have time",
						"I don't work for little people",
						"Alright, what do you need?");
				if (option == -1)
					return;
				player.setBusy(true);
				sleep();

				if (option == 0) {
					sendChat(
							"Fine, leave me to it then. But don't expect me to let you use my anvils!",
							npc, player, 1200);
					player.setBusy(false);
					npc.unblock();
					return;
				} else if (option == 1) {
					sendChat("I'm vertically challenged!", npc, player);
					sendChat(
							"Get out of here before I smelt an iron bar to your ass",
							npc, player, 1200);
					player.setBusy(false);
					npc.unblock();
					return;
				} else if (option == 2) {
					sendChat(
							"I need six pieces of clay, four copper ores and two iron ores",
							npc, player);
					sendChat(
							"Save me some time and bring those back here for me",
							npc, player);
					sendChat(
							"And I'll let you use my anvils any time you need to",
							npc, player);

					player.setBusy(false);
					option = getMenuOption(player, "I'll get to it then",
							"Maybe later");
					if (option == -1)
						return;
					player.setBusy(true);
					sleep();

					if (option == 0) {
						player.setQuestStage(getUniqueID(), 1); // Start quest
						sendChat("Thank you " + player.getUsername(), npc,
								player);
						sendChat("That would be a great help", npc, player);
						sendChat(
								"I'll just wait here for you to come back then",
								npc, player, 1200);
						player.setBusy(false);
						npc.unblock();
						return;
					} else {
						sendChat(
								"Fine, but don't expect me to let you use my anvils in the meantime!",
								npc, player, 1200);
						player.setBusy(false);
						npc.unblock();
						return;
					}
				}
			} else if (option == 1) {
				sendChat("I don't care much for friends", npc, player);
				sendChat("So get outta my house!", npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			} else {
				sendChat("I'm big where it counts, "
						+ (player.isMale() ? "buddy" : "missy") + "!", npc,
						player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		} else if (stage == 1) {
			sendChat("Have you got my materials yet?", npc, player);

			player.setBusy(false);
			int option = getMenuOption(player, "Yes, I have", "No, not yet",
					"I forgot what to get!");
			if (option == -1)
				return;
			player.setBusy(true);
			sleep();

			if (option == 0) {

				if (countItem(player, 149) >= 6 && countItem(player, 150) >= 4
						&& countItem(player, 151) >= 2 && stage != COMPLETE) {
					sendChat("Excellent! Hand 'em over, then", npc, player);
					player.getActionSender().sendMessage(
							"You hand Doric the materials");
					for (int i = 0; i < 6; i++)
						takeItem(player, new InvItem(149));
					for (int i = 0; i < 4; i++)
						takeItem(player, new InvItem(150));
					for (int i = 0; i < 2; i++)
						takeItem(player, new InvItem(151));
					sleep();
					sendChat("Thanks for your help " + player.getUsername(),
							npc, player);
					sendChat(
							"Anyone who lends a hand is welcome in my workshop",
							npc, player);
					sendChat("Come back and use my anvils whenever you like!",
							npc, player, 1200);
					player.incQuestPoints(QUEST_POINTS);
					player.incExp(13, REWARD_EXP, false);
					giveItem(player, new InvItem(10, REWARD_GP));
					player.setQuestStage(getUniqueID(), Quest.COMPLETE);
					player.setBusy(false);
					npc.unblock();
					return;
				} else {
					sendChat(
							"Liar! You're not doing a very good job of earning my trust!",
							npc, player, 1200);
					player.setBusy(false);
					npc.unblock();
					return;
				}
			} else if (option == 1) {
				sendChat("Well come and talk to me when you do", npc, player);
				sendChat("And I'll consider letting you use my anvils", npc,
						player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			} else if (option == 2) {
				sendChat(
						"I need six pieces of clay, four copper ores and two iron ores",
						npc, player);
				sendChat("Oh right, thanks. I'll get back to it then", player,
						npc, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		}
		npc.unblock();
		player.setBusy(true);
	}

	public Dorics() {
	}
}