package org.moparscape.msc.gs.plugins.quests;

import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.Item;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.quest.Quest;
import org.moparscape.msc.gs.quest.QuestAction;

/**
 * Quest: Witch's Potion (v1.0) 7/5/2009 (uid: 9) Status: COMPLETE (untested)
 * Start: Hetty (id 148) 316, 665 Items: Rat tail (271) Onion (241) Newt's eye
 * (270) Burnt meat (134) Reward: 1 quest point, 1000 magic exp
 * 
 * TODO: restrict rats tails from being seen by players not on the quest fix
 * isNpcAssociated() from some how picking up NPCs from other quests.
 * 
 * @author punKrockeR
 */
public class WitchsPotion extends Quest {
	private static final int WITCH_ID = 148;
	private static final int RAT_ID = 29;
	private static final int TAIL_ID = 271;
	private static final int CAULDRON_ID = 147;
	private static final int CAULDRON_X = 316;
	private static final int CAULDRON_Y = 666;
	private static final int ONION_ID = 241;
	private static final int NEWT_EYE_ID = 270;
	private static final int BURNT_MEAT_ID = 134;
	private static final int QUEST_POINTS = 1;
	private static final int REWARD_XP = 1000;

	public void init() {
		associateNpc(WITCH_ID);
		associateNpc(RAT_ID);
		associateObject(CAULDRON_ID, CAULDRON_X, CAULDRON_Y);
	}

	public String getName() {
		return "Witch's Potion";
	}

	public int getUniqueID() {
		return 9;
	}

	/**
	 * @return if the given npc id is associated with this quest
	 */
	@Override
	public boolean isNpcAssociated(int id, Player player) {
		if (id == RAT_ID && player.getQuestStage(this) == 1)
			return true;

		if (id == WITCH_ID)
			return true;

		return false;
	}

	public void handleAction(QuestAction action, Object[] args,
			final Player player) {
		int stage = player.getQuestStage(this);

		if (action == QuestAction.TALKED_NPC) {
			if (!(args[0] instanceof Npc))
				return;

			final Npc npc = (Npc) args[0];

			if (npc.getID() == WITCH_ID)
				handleWitchChat(npc, player);
			else if (npc.getID() == RAT_ID) {
				player.getActionSender().sendMessage("Rats can't talk!");
				player.setBusy(false);
				npc.unblock();
				return;
			}
		} else if (action == QuestAction.KILLED_NPC) {
			if (!(args[0] instanceof Npc))
				return;

			final Npc npc = (Npc) args[0];

			if (npc.getID() == RAT_ID) {
				if (stage == 1)
					world.registerItem(new Item(TAIL_ID, npc.getX(),
							npc.getY(), 1, player));
			}
		} else if (action == QuestAction.USED_OBJECT) {
			if (!(args[0] instanceof GameObject))
				return;

			final GameObject obj = (GameObject) args[0];

			if (obj.getID() == CAULDRON_ID && obj.getX() == CAULDRON_X
					&& obj.getY() == CAULDRON_Y) {
				player.setBusy(true);

				if (stage != 2) {
					sendChat("I'd rather not", player, null);
					sendChat("It doesn't look very tasty", player, null);
					player.setBusy(false);
				} else {
					if (player.getQuestStage(this) == COMPLETE) {
						player.setBusy(false);
						return;
					}
					sendMessage(player, "You drink from the cauldron");
					sleep();
					sendMessage(player, "You feel yourself imbued with power");
					sleep(1200);
					player.setQuestStage(this, COMPLETE);
					player.incQuestPoints(QUEST_POINTS);
					player.incExp(6, REWARD_XP, false);
					player.setBusy(false);
				}
			}
		}
	}

	private void handleWitchChat(final Npc npc, final Player player) {
		int stage = player.getQuestStage(this);

		if (stage == -1) {
			sendChat("Greetings traveller", npc, player);

			sendChat("What could you want with an old woman like me?", npc,
					player);

			player.setBusy(false);
			int option = getMenuOption(player, "I am in search of a quest",
					"I've heard that you are a witch");
			if (option == -1)
				return;
			player.setBusy(true);
			sleep();

			if (option == 0) {
				sendChat("Hmm maybe i can think of something for you", npc,
						player);

				sendChat(
						"Would you like to become more proficient in the dark arts?",
						npc, player);

				player.setBusy(false);
				option = getMenuOption(player,
						"Yes help me become one with my darker side",
						"No I have my principles and honour",
						"What, you mean improve my magic?");
				if (option == -1)
					return;
				player.setBusy(true);
				sleep();

				if (option == 0) {
					startQuest(npc, player, true);
					return;
				} else if (option == 1) {
					sendChat("Suit yourself, but you're missing out", npc,
							player);
					sleep(1200);
					player.setBusy(false);
					npc.unblock();
					return;
				} else {
					sendChat("Yes improve your magic", npc, player);

					sendChat("Do you have no sense of drama?", npc, player);

					player.setBusy(false);
					option = getMenuOption(player,
							"Yes I'd like to improve my magic",
							"No I'm not interested",
							"Show me the mysteries of the dark arts");
					if (option == -1)
						return;
					player.setBusy(true);
					sleep();

					if (option == 0) {
						startQuest(npc, player, false);
						return;
					} else if (option == 1) {
						sendChat("Many aren't to start off with", npc, player);
						sendChat(
								"But i think you'll be drawn back to this place",
								npc, player, 1200);
						player.setBusy(false);
						npc.unblock();
						return;
					} else {
						startQuest(npc, player, true);
						return;
					}
				}
			} else {
				sendChat(
						"Yes it does seem to be getting fairly common knowledge",
						npc, player);
				sendChat(
						"I fear i may get a visit from the witch hunters of falador before long",
						npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		} else if (stage == 1) {
			sendChat("So have you found the things for the potion?", npc,
					player);

			if (hasItem(player, TAIL_ID, NEWT_EYE_ID, BURNT_MEAT_ID, ONION_ID)) {
				sendChat("Yes i have everthing", player, npc);
				sendChat("Excellent, can i have them then?", npc, player);
				sendMessage(player, "You pass the ingredients to Hetty");
				takeItem(player, NEWT_EYE_ID, TAIL_ID, BURNT_MEAT_ID, ONION_ID);
				sleep();
				sendMessage(player,
						"Hetty puts all the ingredients in her cauldron");
				sleep();
				sendMessage(player, "She closes her eyes and begins to chant");
				sleep();
				sendChat("Ok drink from the cauldron", npc, player, 1200);
				player.setQuestStage(this, 2);
				player.setBusy(false);
				npc.unblock();
				return;
			} else {
				sendChat("No not yet", player, npc);
				sendChat("Well remember what you need to get", npc, player);
				sendChat(
						"An eye of newt, a rat's tail, some burnt meat and an onion",
						npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		} else if (stage == 2) {
			sendChat("Well are you going to drink the potion or not?", npc,
					player, 1200);
			player.setBusy(false);
			npc.unblock();
			return;
		} else if (stage == COMPLETE) {
			sendChat("Greetings traveller", npc, player);
			sendChat("How's your magic coming along? ", npc, player);
			sendChat("I'm practicing and slowly getting better", player, npc);
			sendChat("Good, good", npc, player, 1200);
			player.setBusy(false);
			npc.unblock();
			return;
		} else // error
		{
			player.setBusy(false);
			npc.unblock();
			return;
		}
	}

	private void startQuest(final Npc npc, final Player player, boolean dark) {
		sendChat(
				"Ok, I'm going to make a potion to help bring out your darker self",
				npc, player);
		sendChat(
				"So that you can perform acts of dark magic with greater ease",
				npc, player);

		if (!dark) {
			sendChat("Dark magic?", player, npc);
			sendChat("It's not as ominous as it sounds, trust me", npc, player);

			player.setBusy(false);
			int option = getMenuOption(player,
					"No, I don't like the sound of it", "Well, alright...");
			if (option == -1)
				return;
			player.setBusy(true);
			sleep();

			if (option == 0) {
				sendChat("Fine, suit yourself", npc, player);
				sendChat("But I sense a great deal of dark power within you",
						npc, player);
				sendChat("You'll change your mind one day", npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		}

		sendChat("You will need certain ingredients", npc, player);
		sendChat("What do i need?", player, npc, 2000);
		sendChat(
				"You need an eye of newt, a rat's tail, an onion and a piece of burnt meat",
				npc, player);
		player.setQuestStage(this, 1);
		player.setBusy(false);
		npc.unblock();
		return;
	}

	public WitchsPotion() {
	}
}