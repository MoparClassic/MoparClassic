package org.moparscape.msc.gs.plugins.quests;

import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.quest.Quest;
import org.moparscape.msc.gs.quest.QuestAction;

/**
 * Quest: Sheep Shearer (v1.0) Status: COMPLETE Start: Fred the farmer (id 77),
 * 159, 619 Items: 207x20 Reward: 1 quest point, 60 gold, Crafting 350 xp
 * 
 * @author Konijn
 */
public class SheepShearer extends Quest {
    private final int FRED_ID = 77;
    private final int ITEM_WOOL = 207;
    private final int REWARD_XP = 350;
    private final int REWARD_GP = 330;
    private final String[] FIRST_MENU = new String[] { "Sure, what do I need to do?", "No thanks, I'm good." };
    private final String[] SECOND_MENU = new String[] { "Sorry, I don't like the sound of that.", "I'd be happy to help." };

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

    public void handleAction(QuestAction action, Object[] args, final Player player) {
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

		sendChat("Hi there, traveller. Care to make some money?", npc, player);

		player.setBusy(false);
		int option = getMenuOption(player, FIRST_MENU);
		sleep();
		player.setBusy(true);
		switch (option) {
		case 1:
		    player.setBusy(false);
		    npc.unblock();

		    break;
		case 0:

		    sendChat("If you collect 20 balls of wool for me, I'll pay you 500 coins.", npc, player);

		    sendChat("Maybe I'll teach you a thing or two about crafting, too.", npc, player);

		    sendChat("I'm afraid you'll have to find your own shears, but the sheep are outside.", npc, player);

		    player.setBusy(false);
		    option = getMenuOption(player, SECOND_MENU);
		    sleep();
		    player.setBusy(true);
		    switch (option) {
		    case 1:
			sendChat("Great! Come back and see me when you're done.", npc, player);

			player.setQuestStage(getUniqueID(), 1);
			player.setBusy(false);
			npc.unblock();

			break;
		    case 0:

			sendChat("Suit yourself. Come and see me if you change your mind.", npc, player);
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

		if (npc.getID() != FRED_ID)
		    return;

		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Ahh, you've returned! Do you have my wool?", npc, player);

		player.setBusy(false);
		int option = getMenuOption(player, "I'm afraid not.", "Yes, I do.");

		if (option == -1) {
		    npc.unblock();
		    player.setBusy(false);
		    return;
		}
		sleep();
		player.setBusy(true);
		// SingleEvent
		if (option == 0) {

		    sendChat("Well, come and see me when you do. The offer still stands", npc, player);
		    player.setBusy(false);
		    npc.unblock();

		} else if (option == 1) {

		    // check items
		    if (player.getInventory().hasItemId(ITEM_WOOL) && player.getInventory().countId(ITEM_WOOL) >= 20) {
			finishQuest(player, npc);
		    } else {
			sendChat("Um, no you don't. Get back to me when you do. The reward still stands!", npc, player);
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
		sendChat("Hello " + player.getUsername() + "!", npc, player);
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
	sendChat("Thank you very much! As promised, here's your reward.", npc, player);

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
