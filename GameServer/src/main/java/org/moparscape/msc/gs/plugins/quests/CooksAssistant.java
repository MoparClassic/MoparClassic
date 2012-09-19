package org.moparscape.msc.gs.plugins.quests;

import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.quest.Quest;
import org.moparscape.msc.gs.quest.QuestAction;

/**
 * Quest: Cook's Assistant (v1.0) Status: COMPLETE Start: Lumbridge cook (id 7)
 * Items: 19, 136, 22 Reward: 1 quest point, 350 cooking xp
 * 
 * @author youKnowWho
 */
public class CooksAssistant extends Quest {
	private static final int COOK_ID = 7;
	private static final int ITEM_EGG = 19;
	private static final int ITEM_FLOUR = 136;
	private static final int ITEM_MILK = 22;
	private static final int REWARD_XP = 350;
	private static final String[] FIRST_MENU = new String[] { "Oh, ok, sorry.",
			"Well maybe I can help?" };
	private static final String[] SECOND_MENU = new String[] {
			"Sure, what do you need?", "No, sorry" };
	private static final int QUEST_POINTS = 1;

	public void init() {
		associateNpc(COOK_ID);
	}

	public CooksAssistant() {
	}

	public String getName() {
		return "Cook's Assistant";
	}

	public int getUniqueID() {
		return 0;
	}

	public void handleAction(QuestAction action, Object[] args,
			final Player player) {
		int stage = player.getQuestStage(this);

		if (stage == -1) // Quest hasn't been started
		{
			if (action == QuestAction.TALKED_NPC) {
				if (!(args[0] instanceof Npc))
					return;

				final Npc npc = (Npc) args[0];

				if (npc.getID() != COOK_ID)
					return;

				player.setBusy(true);
				npc.blockedBy(player);

				sendChat("Sorry, I can't talk right now, I'm very busy!", npc,
						player);

				player.setBusy(false);
				int option = getMenuOption(player, FIRST_MENU);
				player.setBusy(true);
				sleep();
				switch (option) {

				case 0:
					player.setBusy(false);
					npc.unblock();
					break;
				case 1:

					sendChat(
							"Perhaps you can... You see, it's the duke's birthday tomorrow",
							npc, player);

					sendChat(
							"And I haven't got the ingredients for his cake yet!",
							npc, player);

					sendChat("Do you think you could collect them for me?",
							npc, player);

					player.setBusy(false);
					option = getMenuOption(player, SECOND_MENU);

					player.setBusy(true);
					switch (option) {
					case 0:

						sendChat(
								"Oh thank you so much! I'm going to need an egg, some milk and a pot of flour.",
								npc, player);

						sendChat("Please, hurry!", npc, player);
						player.setQuestStage(getUniqueID(), 1);
						player.setBusy(false);
						npc.unblock();

						break;
					case 1:

						sendChat("Fine. I didn't want your help anyway.", npc,
								player);
						player.setBusy(false);
						npc.unblock();

						break;
					default:
						player.setBusy(false);
						npc.unblock();
						break;
					}

					break;
				default:
					player.setBusy(false);
					npc.unblock();
					break;
				}

			} else
				return;
		} else if (stage == 1) {
			if (action == QuestAction.TALKED_NPC) {
				if (!(args[0] instanceof Npc))
					return;

				final Npc npc = (Npc) args[0];

				if (npc.getID() != COOK_ID)
					return;

				player.setBusy(true);
				npc.blockedBy(player);
				sendChat(
						"Sorry, I can't - oh, it's you! Do you have the ingredients?",
						npc, player);

				player.setBusy(false);
				int option = getMenuOption(player, "I forgot what to get!",
						"Yes, i have them", "No, not yet");
				player.setBusy(true);
				sleep();
				if (option == 0) {

					sendChat(
							"I need an egg, a pot flour and some milk! And quickly!",
							npc, player);
					player.setBusy(false);
					npc.unblock();

				} else if (option == 1) {

					// check items
					if (player.getInventory().hasItemId(ITEM_EGG)
							&& player.getInventory().hasItemId(ITEM_FLOUR)
							&& player.getInventory().hasItemId(ITEM_MILK)) {
						finishQuest(player, npc);
					} else {
						sendChat(
								"No you don't! Oh please, don't get my hopes up like that!",
								npc, player);
						player.setBusy(false);
						npc.unblock();
					}

				} else {

					sendChat("Oh, please hurry!", npc, player);
					player.setBusy(false);
					npc.unblock();

				}

			}
		} else if (stage == 0) {
			if (action == QuestAction.TALKED_NPC) {
				if (!(args[0] instanceof Npc))
					return;

				final Npc npc = (Npc) args[0];

				if (npc.getID() != COOK_ID)
					return;

				player.setBusy(true);
				npc.blockedBy(player);
				sendChat(
						"Oh hi "
								+ player.getUsername()
								+ ". Thanks for your help! I can't talk at the moment though.",
						npc, player);
				player.setBusy(false);
				npc.unblock();
			}
		}

	}

	private void finishQuest(final Player player, final Npc npc) {
		sleep(1000);
		if (player.getQuestStage(this) == COMPLETE) {
			player.setBusy(false);
			npc.unblock();
			return;
		}
		sendChat("Oh, thank you so much " + player.getUsername() + "!", npc,
				player);

		sendChat("I'm afraid don't have any money to reward you with", npc,
				player, 3000);

		sendChat("But I can give you some cooking tips!", npc, player);

		player.incExp(7, REWARD_XP, false);
		player.getActionSender().sendStat(7);
		player.setQuestStage(getUniqueID(), Quest.COMPLETE);
		player.getInventory().remove(ITEM_EGG, 1);
		player.getInventory().remove(ITEM_MILK, 1);
		player.getInventory().remove(ITEM_FLOUR, 1);
		player.getActionSender().sendInventory();
		player.incQuestPoints(QUEST_POINTS);
		player.setBusy(false);
		npc.unblock();
	}
}
