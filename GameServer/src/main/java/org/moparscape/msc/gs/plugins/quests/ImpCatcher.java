package org.moparscape.msc.gs.plugins.quests;

import org.moparscape.msc.gs.external.EntityHandler;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.quest.Quest;
import org.moparscape.msc.gs.quest.QuestAction;

/**
 * Quest: Imp Catcher (v1.0) 5/1/2009 Status: COMPLETE Start: Talborn the wizard
 * (id 17), 218, 1635 Items: 231, 232, 233, 234 Reward: 1 quest point, 350 magic
 * xp
 * 
 * @author punKrockeR
 */
public class ImpCatcher extends Quest {
	private static final int TALBORN_ID = 17;
	private static final int RED_BEAD = 231;
	private static final int YELLOW_BEAD = 232;
	private static final int BLACK_BEAD = 233;
	private static final int WHITE_BEAD = 234;
	private static final int ITEM_REWARD = 235;
	private static final int REWARD_XP = 350;
	private static final String[] FIRST_MENU = new String[] {
			"No way you crazy man", "Sure, why not" };
	private static final String[] SECOND_MENU = new String[] { "Yeah I did",
			"Not yet" };
	// private static final int DEFAULT_EVENT_DELAY = 3200;
	private static final int QUEST_POINTS = 1;

	/**
	 * @return the quest's name
	 */
	public String getName() {
		return "Imp Catcher";
	}

	/**
	 * @return this quest's unique id
	 */
	public int getUniqueID() {
		return 3;
	}

	/**
	 * Initialises the quest
	 */
	public void init() {
		associateNpc(TALBORN_ID);
	}

	/**
	 * Handles the given quest action
	 */
	public void handleAction(QuestAction action, Object[] args,
			final Player player) {
		int stage = player.getQuestStage(this);

		if (action == QuestAction.TALKED_NPC) {
			if (!(args[0] instanceof Npc))
				return;

			final Npc npc = (Npc) args[0];

			if (npc.getID() != TALBORN_ID)
				return;

			player.setBusy(true);
			npc.blockedBy(player);

			if (stage == -1)
				startQuest(player, npc);
			else
				handleTalk(player, npc);
		}
	}

	/**
	 * Handles npc chat if the quest hasn't been started yet
	 */
	private void startQuest(final Player player, final Npc npc) {
		sendChat("...Avada kedavra!", npc, player);

		spellPlayer(player, npc);
		sleep(1000);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("wow!!", player, npc);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("What? Huh? Get out of the way you fool!", npc, player);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("You shot me you stupid sod", player, npc);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Hmm, indeed. But it worked!", npc, player);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Yeah it did. You burnt my sleeve.", player, npc);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Collateral damage. A necessary casualty.", npc, player);
		sendChat("Your sleeve is expendable in this war", npc, player);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("What war?", player, npc);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Well the war against those nasty little imps of course", npc,
				player);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Imps? What threat could they possibly pose?", player, npc);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Those little red demons stole my beads!", npc, player);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat(
				"Riight.. your beads. And what's so special about these beads?",
				player, npc);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Why, absolutely nothing. It's the principle of the matter.",
				npc, player);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Surely it can't be that hard to get them to give them back",
				player, npc);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Oh really? Why don't you try and get them back then", npc,
				player);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat(
				"Yeah right, chase imps around for free. You're crazier than I thought",
				player, npc);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Free? Nonsense. I'd be happy to reward you.", npc, player);
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Hmm. What kind of reward?", player, npc);
		npc.blockedBy(player);
		player.setBusy(true);
		sendChat(
				"I've got a few valuable things lying around, I'll find something.",
				npc, player);
		npc.blockedBy(player);
		player.setBusy(true);
		sendChat("How about it? Would you help an old wizard out?", npc, player);

		player.setBusy(false);
		int option = getMenuOption(player, FIRST_MENU);
		if (option == -1) {
			npc.unblock();
			player.setBusy(false);
			return;
		}
		sleep(2000);

		player.setBusy(true);
		npc.blockedBy(player);
		if (option == 1) {
			player.setBusy(true);
			npc.blockedBy(player);
			sendChat(
					"Excellent! Begin at once if you wish to collect them within a decade",
					npc, player);
			player.setBusy(true);
			npc.blockedBy(player);
			sendChat("There are four beads! Red, white, black, and yellow.",
					npc, player);

			player.setBusy(true);
			npc.blockedBy(player);
			sendChat("Ahh you said... nevermind", player, npc);

			player.setBusy(true);
			npc.blockedBy(player);
			sendChat("I'll come back when I get the beads...", player, npc);

			player.setBusy(true);
			npc.blockedBy(player);
			sendChat(
					"If! If you get the beads. Don't underestimate the little beasts!",
					npc, player);

			player.setBusy(true);
			npc.blockedBy(player);
			sendChat("Sure, whatever old man.", player, npc);

			player.setBusy(true);
			npc.blockedBy(player);
			player.setQuestStage(getUniqueID(), 1);
			sendChat("Tarantallegra!", npc, player);
			spellPlayer(player, npc);

			sendChat("Ouch! Would you bloody wait til I'm gone!", player, npc);
			player.setBusy(false);
			npc.unblock();

			sendChat("Move it!", npc, player);

		} else {
			player.setBusy(true);
			npc.blockedBy(player);
			sendChat(
					"Fine. But don't come to me when you need some pomegranate!",
					npc, player);

			sendChat("Okaaay...", player, npc);
			player.setBusy(false);
			npc.unblock();
		}

	}

	// black
	/**
	 * Handles npc chat if the quest is started but not finished
	 */
	private void handleTalk(final Player player, final Npc npc) {
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("I know you. You're that fellow that stole my beads!", npc,
				player);

		npc.blockedBy(player);
		sendChat("What? No, I'm the one that offered to find them for you.",
				player, npc);

		npc.blockedBy(player);
		sendChat("It makes no difference, I still don't like you.", npc, player);
		sleep(1000);
		if (player.getQuestStage(getUniqueID()) != COMPLETE) {

			npc.blockedBy(player);
			sendChat("But my beads, did you find them?", npc, player);

			player.setBusy(false);
			npc.blockedBy(player);
			int option = getMenuOption(player, SECOND_MENU);
			if (option == -1) {
				npc.unblock();
				player.setBusy(false);
				return;
			}
			if (option == 0) // Yes
			{
				sleep(1500);
				if (player.getInventory().hasItemId(RED_BEAD)
						&& player.getInventory().hasItemId(YELLOW_BEAD)
						&& player.getInventory().hasItemId(BLACK_BEAD)
						&& player.getInventory().hasItemId(WHITE_BEAD)) {
					sendChat("Excellent! Hand them over immediately", npc,
							player);

					sendChat(
							"Alright, calm down. I thought they were useless anyway",
							player, npc);

					sendChat("Useless! Oh my dear "
							+ (player.isMale() ? "boy" : "girl")
							+ ", they are quite important", npc, player);

					finishQuest(player, npc);
				} else {
					sendChat(
							"Bollocks! Don't return til you have them you fool",
							npc, player);
					spellPlayer(player, npc);
					sendChat("Hey! Watch yourself old man", player, npc);
					player.setBusy(false);
					npc.unblock();
				}

			} else // No
			{

				sendChat("Well what are you doing here then? Move it!", npc,
						player);

				spellPlayer(player, npc);

				sendChat("Would you bugger off with the spells you mad fool!",
						player, npc);
				player.setBusy(false);
				npc.unblock();
			}

		} else {
			player.setBusy(false);
			npc.unblock();
		}
	}

	/**
	 * Finishes the quest
	 */
	private void finishQuest(final Player player, final Npc npc) {
		if (player.getQuestStage(this) == COMPLETE) {
			player.setBusy(false);
			npc.unblock();
			return;
		}
		player.setBusy(true);
		npc.blockedBy(player);
		player.getActionSender().sendMessage("You hand over the beads");
		player.getInventory().remove(YELLOW_BEAD, 1);
		player.getInventory().remove(RED_BEAD, 1);
		player.getInventory().remove(WHITE_BEAD, 1);
		player.getInventory().remove(BLACK_BEAD, 1);
		player.getActionSender().sendInventory();
		player.getActionSender().sendSound("click");

		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Alright, that's my half of the bargain.", player, npc);

		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Now what of my reward?", player, npc);

		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Ah, your reward. I found this! You can take it.", npc, player);

		player.setBusy(true);
		npc.blockedBy(player);
		player.getActionSender().sendMessage(
				EntityHandler.getNpcDef(TALBORN_ID).getName()
						+ " gives you one "
						+ EntityHandler.getItemDef(ITEM_REWARD).getName());
		player.getInventory().add(new InvItem(ITEM_REWARD, 1));
		player.getActionSender().sendInventory();

		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("What am I supposed to do with this?", player, npc);

		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("I don't know, whatever you like. I certainly don't want it",
				npc, player);

		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("And have a read of this book", npc, player);

		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("It'll teach you a thing or two about magic!", npc, player);

		player.setBusy(true);
		npc.blockedBy(player);
		player.getActionSender().sendMessage("You read the wizard's book");

		player.setBusy(true);
		npc.blockedBy(player);
		player.incExp(6, REWARD_XP, false);
		player.getActionSender().sendStat(6);

		player.setBusy(true);
		npc.blockedBy(player);

		sendChat("Now I have work to do! Leave my tower!", npc, player);

		spellPlayer(player, npc);

		sendChat("Damnit! Gladly...", player, npc);

		player.setQuestStage(getUniqueID(), Quest.COMPLETE);
		player.incQuestPoints(QUEST_POINTS);
		player.setBusy(false);
		npc.unblock();

	}

	/**
	 * Casts an imaginary spell at the player
	 */
	private void spellPlayer(Player player, Npc npc) {
		player.setLastDamage(0);
		player.informOfModifiedHits(player);
		player.getActionSender().sendTeleBubble(npc.getLocation().getX(),
				npc.getLocation().getY(), false);
		player.getActionSender().sendSound("combat1a");
	}

	/**
	 * Construct the quest (empty)
	 */
	public ImpCatcher() {
	}
}