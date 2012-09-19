package org.moparscape.msc.gs.plugins.quests;

import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.quest.Quest;
import org.moparscape.msc.gs.quest.QuestAction;

/**
 * Quest: Sheep Shearer (v1.0) Status: COMPLETE Start: Fred the farmer (id 77),
 * 159, 619 Items: 207x20 Reward: 1 quest point, 330 gold, Crafting 350 xp
 * 
 * @author Konijn
 * edited by Latifundio
 */
public class SheepShearer extends Quest {
	private final int FRED_ID = 77;
	private final int ITEM_WOOL = 207;
	private final int REWARD_XP = 350;
	private final int REWARD_GP = 330;
	private final String[] FIRST_MENU = new String[] {
			"I'm lost.", "I'm looking for a quest.", "I'm looking for something to kill." };
	private final String[] SECOND_MENU = new String[] {
			"That doesn't sound like an exciting quest.", "The thing? What do you mean?" };
	private final String[] THIRD_MENU = new String[] {
			"I'm not sure about this..", "Yes okay, I can do that." };

	public void init() {
		associateNpc(FRED_ID);
	}

	public SheepShearer() {
	}

	public String getName() {
		return "Sheep Shearer";
	}

	public int getUniqueID() {
		return 1;
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

				if (npc.getID() != FRED_ID)
					return;

				player.setBusy(true);
				npc.blockedBy(player);

				sendChat("What are you doing on my land?", npc,
						player);						
				sendChat("Aren't you the one who keeps all my gates open", npc,
						player);						
				sendChat("and letting out all my sheep?", npc,
						player);

				player.setBusy(false);
				int option = getMenuOption(player, FIRST_MENU);
				sleep();
				player.setBusy(true);
				switch (option) {
				case 2:
					sendChat("What on my land?",
							npc, player);							
					sendChat("Leave my livestock alone you scoundrel.",
							npc, player);

					player.setBusy(false);
					npc.unblock();
					break;
				case 0:
					sendChat("How can you be lost?",
							npc, player);
							
					sendChat("Just follow the road east and south.",
							npc, player);
							
					sendChat("You'll end up in Lundbridge fairly quickly.",
							npc, player);
							
					player.setBusy(false);
					npc.unblock();

					break;
				case 1:
					sendChat("You're after a quest, you say?",
							npc, player);

					sendChat("Actually I could do with a bit of help.",
							npc, player);

					sendChat("My sheep are getting mighty woolly.",
							npc, player);
					
					sendChat("Maybe you could sheer them",
							npc, player);
							
					sendChat("and while you're at it, spin the wool for me too.",
							npc, player);
							
					sendChat("Yes that's it, bring me 20 balls of wool.",
							npc, player);
							
					sendChat("And I'm sure I could sort out some kind of payment.",
							npc, player);
							
					sendChat("Of course, there's the small matter of the thing.",
							npc, player);
							
					player.setBusy(false);
					option = getMenuOption(player, SECOND_MENU);
					sleep();
					player.setBusy(true);
					switch (option) {

					case 0:
						sendChat("Well, what do you expect if you ask a farmer for a quest?",
								npc, player);

						player.setBusy(false);
						npc.unblock();
						break;
					case 1:
						sendChat("I wouldn't worry about it",
								npc, player);
						sendChat("Something eat all the previous shearers..",
								npc, player);
						sendChat("They probably just got unlucky",
								npc, player);
						sendChat("I'm sure it is nothing to worry about.",
								npc, player);
						sendChat("Now are you going to help me or not?",
								npc, player);

						player.setBusy(false);
						option = getMenuOption(player, THIRD_MENU);
						sleep();
						player.setBusy(true);
						switch (option) {

						case 1:
							sendChat("Okay, I will see you when you have some wool.",
									npc, player);

							player.setQuestStage(getUniqueID(), 1);
							player.setBusy(false);
							npc.unblock();
						break;
						case 0:
							sendChat("Why are you so worried?",
									npc, player);
							sendChat("Probably they're just hiding or somethig",
									npc, player);

							player.setBusy(false);
							npc.unblock();
							break;
						default:
							player.setBusy(false);
							npc.unblock();
							break;
						}

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

				if (npc.getID() != FRED_ID)
					return;

				player.setBusy(true);
				npc.blockedBy(player);
				sendChat("How are you doing getting those balls of wool?", npc,
						player);

				player.setBusy(false);
				int option = getMenuOption(player, "I'm not sure how to do it",
						"I got some here.");

				if (option == -1) {
					npc.unblock();
					player.setBusy(false);
					return;
				}
				sleep();
				player.setBusy(true);
				// SingleEvent
				if (option == 0) {

					sendChat("Well, find a spinning wheel to spin the wool.",
							npc, player);
					player.setBusy(false);
					npc.unblock();

				} else if (option == 1) {

					// check items
					if (player.getInventory().hasItemId(ITEM_WOOL)
							&& player.getInventory().countId(ITEM_WOOL) >= 20) {
						finishQuest(player, npc);
					} else {
						sendChat("Um, you still don't have all of them.",
								npc, player);
						player.setBusy(false);
						npc.unblock();
					}
				}
			}
		} else if (stage == 0) {
			if (action == QuestAction.TALKED_NPC) {
				if (!(args[0] instanceof Npc))
					return;

				final Npc npc = (Npc) args[0];

				if (npc.getID() != FRED_ID)
					return;

				player.setBusy(true);
				npc.blockedBy(player);
				sendChat("What are you still doing in my land?", 
						npc, player);
				sendChat("Err.. nothing.", player, npc);

				player.setBusy(false);
				npc.unblock();
			}
		}
	}

	private void finishQuest(final Player player, final Npc npc) {
		if (player.getQuestStage(this) == COMPLETE) {
			player.setBusy(false);
			npc.unblock();
			return;
		}
		sendChat("I guess I did better pay you then.", npc,
				player);

		player.incExp(12, REWARD_XP, false);
		player.getActionSender().sendStat(12);
		player.setQuestStage(getUniqueID(), Quest.COMPLETE);
		for (int i = 0; i < 20; i++)
			player.getInventory().remove(ITEM_WOOL, 1);
		player.getInventory().add(new InvItem(10, REWARD_GP));
		player.getActionSender().sendInventory();
		player.incQuestPoints(1);
		player.setBusy(false);
		npc.unblock();

	}
}
