package org.moparscape.msc.gs.plugins.quests;

import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Item;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.Script;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.quest.Quest;
import org.moparscape.msc.gs.quest.QuestAction;

/**
 * Quest: The Knight's Sword (v1.0) 7/5/2009 Status: COMPLETE (untested) Start:
 * (NPC id 132) 312, 567 Thurgo (id 134) 288, 717 Reldo (id 20) 129, 457 Items:
 * 2 x Iron bar (170) Bluerite ore (266) Sword (265) Picture (264) Redberry pie
 * (258) Rewards: 1 quest points
 * 
 * @author punKrockeR
 */
public class KnightsSword extends Quest {

	private static final int QUEST_POINTS = 1;
	private static final int NPC_SQUIRE = 132;
	private static final int NPC_RELDO = 20;
	private static final int NPC_DWARF = 134;
	private static final int NPC_VYVIN = 138;
	private static final int CUPBOARD_ID = 175;
	private static final int CUPBOARD_X = 318;
	private static final int CUPBOARD_Y = 2454;
	private static final int BLUERITE_ID = 176;
	// private static final int BLUERITE_X = 312;
	// private static final int BLUERITE_Y = 3517;
	// private static final int BLUERITE2_X = 314;
	// private static final int BLUERITE2_Y = 3527;
	private static final int BLUERITE_ORE_ID = 266;
	private static final int SWORD_ID = 265;
	private static final int PICTURE_ID = 264;

	public void init() {
		associateNpc(NPC_SQUIRE);
		associateNpc(NPC_RELDO);
		associateNpc(NPC_DWARF);
		associateNpc(NPC_VYVIN);
		associateObject(CUPBOARD_ID, CUPBOARD_X, CUPBOARD_Y);
		// associateObject(BLUERITE_ID, BLUERITE_X, BLUERITE_Y);
		// associateObject(BLUERITE_ID, BLUERITE2_X, BLUERITE2_Y);
		associateItem(BLUERITE_ORE_ID);
		associateItem(SWORD_ID);
		associateItem(PICTURE_ID);
	}

	/**
	 * @return if the given Item is visible to the player
	 */
	public boolean isItemVisible(Item item, Player player) {
		int stage = player.getQuestStage(this);

		if (stage == COMPLETE) {
			return true;
		}

		switch (item.getID()) {
		case BLUERITE_ORE_ID:
		case SWORD_ID:
			return stage >= 6;
		case PICTURE_ID:
			return stage >= 4;
		default:
			return true;
		}
	}

	public String getName() {
		return "The Knight's Sword";
	}

	public int getUniqueID() {
		return 8;
	}

	// iron bar
	public void handleAction(QuestAction action, Object[] args,
			final Player player) {

		if (action == QuestAction.TALKED_NPC) {
			if (!(args[0] instanceof Npc)) {
				return;
			}

			final Npc npc = (Npc) args[0];

			if (npc.getID() != NPC_SQUIRE && npc.getID() != NPC_RELDO
					&& npc.getID() != NPC_DWARF && npc.getID() != NPC_VYVIN) {
				return;
			}

			player.setBusy(true);
			npc.blockedBy(player);

			if (npc.getID() == NPC_SQUIRE) {
				handleSquireChat(npc, player);
			} else if (npc.getID() == NPC_RELDO) {
				handleReldoChat(npc, player);
			} else if (npc.getID() == NPC_DWARF) {
				handleDwarfChat(npc, player);
			} else if (npc.getID() == NPC_VYVIN) {
				handleVyvinChat(npc, player);
			}
		} else if (action == QuestAction.USED_OBJECT) {
			if (!(args[0] instanceof GameObject)) {
				return;
			}

			final GameObject obj = (GameObject) args[0];
			if (obj.getID() != CUPBOARD_ID && obj.getID() != BLUERITE_ID) {
				return;
			}

			if (obj.getID() == CUPBOARD_ID && obj.getX() == CUPBOARD_X
					&& obj.getY() == CUPBOARD_Y) {
				player.setBusy(true);

				final Npc npc = World.getWorld().getNpc(138, 316, 320, 2454,
						2459);

				if (npc != null) {
					if (!npc.isBusy()) {
						player.setSprite(4);
						npc.setSprite(0);
						sendChat("Hey what are you doing?", npc, player);
						sendChat("That's my cupboard", npc, player);
						player.getActionSender()
								.sendMessage(
										"Maybe you need someone to distract Sir Vivyn for you");
						sleep(1200);
						player.setBusy(false);
						npc.unblock();
						return;
					}

					player.getActionSender().sendMessage(
							"You search through the cupboard");
					sleep(2000);

					if (hasItem(player, PICTURE_ID)
							|| player.getQuestStage(this) < 4) {
						player.getActionSender().sendMessage(
								"The cupboard is just full of junk");
						sleep(1200);
						player.setBusy(false);
						return;
					}

					player.getActionSender()
							.sendMessage(
									"You find a small portrait in there which you take");
					giveItem(player, PICTURE_ID);
					player.setBusy(false);
					return;
				}
			} /*
			 * else if(obj.getID() == BLUERITE_ID) { player.setBusy(false);
			 * handleMining(obj, player, click); }
			 */
		}
	}

	/*
	 * private void handleMining(final GameObject object, final Player player,
	 * final boolean click) { if(player.isBusy()) return;
	 * 
	 * if(!player.withinRange(object, 1)) return;
	 * 
	 * final GameObject newobject = world.getTile(object.getX(),
	 * object.getY()).getGameObject(); final ObjectMiningDef def =
	 * org.moparscape.msc.gs.external.EntityHandler.getObjectMiningDef(102);
	 * 
	 * final InvItem ore = new InvItem(BLUERITE_ORE_ID); if(!click) {
	 * player.getActionSender().sendMessage("This rock contains Bluerite ore");
	 * return; }
	 * 
	 * if(player.getCurStat(14) < 15) {player.getActionSender().sendMessage(
	 * "You need a mining level of 15 to mine this rock."); return; }
	 * 
	 * int axeId = -1; for(int id :
	 * org.moparscape.msc.config.Formulae.miningAxeIDs) { if(countItem(player,
	 * id) > 0) { axeId = id; break; } }
	 * 
	 * if(axeId < 0) {
	 * player.getActionSender().sendMessage("You need a pickaxe to mine this rock."
	 * ); return; }
	 * 
	 * final int axeID = axeId; int retrytimes = -1; final int swings =
	 * player.getSkillLoops(); final int mineLvl = player.getCurStat(14); int
	 * reqlvl = 1;
	 * 
	 * switch(axeID) { case 1258: retrytimes = 2; break; case 1259: retrytimes =
	 * 4; reqlvl = 6; break; case 1260: retrytimes = 6; reqlvl = 21; break; case
	 * 1261: retrytimes = 8; reqlvl = 31; break; case 1262: retrytimes = 12;
	 * reqlvl = 41; break; }
	 * 
	 * if(reqlvl > mineLvl) {
	 * player.getActionSender().sendMessage("You need to be level " + reqlvl +
	 * " to use this pick."); return; }
	 * 
	 * player.setBusy(true); player.getActionSender().sendSound("mine"); Bubble
	 * bubble = new Bubble(player, axeId); for(Player p :
	 * player.getViewArea().getPlayersInView()) p.informOfBubble(bubble);
	 * 
	 * final int retrytime = retrytimes;
	 * player.getActionSender().sendMessage("You swing your pick at the rock..."
	 * ); world.getDelayedEventHandler().add(new ShortEvent(player) { public
	 * void action() { final GameObject newobject2 =
	 * world.getTile(object.getX(), object.getY()).getGameObject();
	 * if(newobject2 != newobject) { player.setBusy(false);
	 * player.setSkillLoops(0); return; }
	 * 
	 * if(org.moparscape.msc.config.Formulae.getOre(def, player.getCurStat(14),
	 * axeID)) { giveItem(player, ore);
	 * player.getActionSender().sendMessage("You manage to obtain some " +
	 * ore.getDef().getName() + "."); player.setSkillLoops(0); player.incExp(14,
	 * def.getExp(), true); player.getActionSender().sendStat(14);
	 * world.registerGameObject(new GameObject(object.getLocation(), 98,
	 * object.getDirection(), object.getType()));
	 * world.delayedSpawnObject(newobject.getLoc(), 60000); } else { boolean
	 * retry = false; if(retrytime >= swings) retry = true;
	 * 
	 * player.getActionSender().sendMessage(
	 * "You only succeed in scratching the rock."); if(retry) {
	 * world.getDelayedEventHandler().add(new SingleEvent(player, 500) { public
	 * void action() { player.setSkillLoops(swings + 1); handleMining(object,
	 * player, click); } }); }
	 * 
	 * if(!retry) { player.isMining(false); player.setSkillLoops(0); } }
	 * 
	 * player.setBusy(false); } }); }
	 */
	private void handleVyvinChat(final Npc npc, final Player player) {
		sendChat("Hello", player, npc, 2000);
		sendChat("Greetings traveller", npc, player, 2000);

		player.setBusy(false);
		int option = getMenuOption(player, "Do you have anything to trade?",
				"Why are there so many knights in this city?");
		if (option == -1) {
			return;
		}
		player.setBusy(true);
		sleep();

		if (option == 0) {
			sendChat("No I'm sorry", npc, player, 1200);
			player.setBusy(false);
			npc.unblock();
			return;
		} else {
			queueChat(npc, player, "We are the white knights of falador",
					"We are the most powerfull order of knights in the land",
					"We are helping the king vallance rule the kingdom",
					"As he is getting old and tired");
			player.setBusy(false);
			npc.unblock();
			return;
		}
	}

	private void handleDwarfChat(final Npc npc, final Player player) {
		int stage = player.getQuestStage(this);

		if (stage == 2) {
			player.setBusy(false);
			int option = getMenuOption(player,
					"Hello are you an incando Dwarf?",
					"Would you like some redberry pie?");
			if (option == -1) {
				return;
			}
			player.setBusy(true);
			sleep();

			if (option == 0) {
				sendChat("Yeah what about it?", npc, player);
				player.setBusy(false);
				option = getMenuOption(player,
						"Would you like some redberry pie?",
						"Can you make me a special sword?");
				if (option == -1) {
					return;
				}
				player.setBusy(true);
				sleep();

				if (option == 0) {
					givePie(npc, player);
					return;
				} else {
					sendChat("No i don't do that anymore", npc, player);
					sendChat("I'm getting old", npc, player, 1200);
					player.setBusy(false);
					npc.unblock();
					return;
				}
			} else {
				givePie(npc, player);
				return;
			}
		} else if (stage == 3) {
			sendChat("Can you make me a special sword?", player, npc);
			sendChat("Well after you've brought me such a great pie", npc,
					player);
			sendChat("I guess i should give it a go", npc, player);
			sendChat("What sort of sword is it?", npc, player);
			sendChat("I need you to make a sword for one of falador's knights",
					player, npc);
			sendChat(
					"He had one which was passed down through five generations",
					player, npc);
			sendChat("But his squire has lost it", player, npc);
			queueChat(
					npc,
					player,
					"A knight's sword eh?",
					"Well i'd need to know exactly how it looked",
					"Before i could make a new one",
					"All the faladian knights used to have swords with different designs",
					"Could you bring me a picture or something?");
			sendChat("I'll see if i can find one", player, npc);
			sendChat("I'll go and ask his squire", player, npc, 1200);
			player.setQuestStage(this, 4);
			player.setBusy(false);
			npc.unblock();
			return;
		} else if (stage == 4 || stage == 5 && stage != COMPLETE) {
			if (hasItem(player, PICTURE_ID)) {
				sendChat(
						"I have found a picture of the sword i would like you to make",
						player, npc);
				player.getActionSender().sendMessage(
						"You hand Thurgo the portrait");
				takeItem(player, PICTURE_ID);
				sleep(2000);
				player.getActionSender().sendMessage(
						"Thurgo examines the picture for a moment");
				sleep();
				queueChat(
						npc,
						player,
						"Ok you'll need to get me some stuff for me to make this",
						"I'll need two iron bars to make the sword to start with",
						"I'll also need an ore called blurite",
						"It's useless for making actual weapons for fighting with",
						"But i'll need some as decoration for the hilt",
						"It is a fairly rare sort of ore",
						"The only place i know where to get it",
						"Is under this cliff here",
						"But it is guarded by a very powerful ice giant",
						"Most the rocks in that cliff are pretty useless",
						"Don't contain much of anything",
						"But there's definitly some blurite in there",
						"You'll need a little bit of mining experience",
						"To be able to find it");
				sendChat("Ok i'll go and find them", player, npc, 1200);
				player.setQuestStage(this, 6);
				player.setBusy(false);
				npc.unblock();
			} else {
				sendChat("Have you got a picture of the sword for me yet?",
						npc, player);
				sendChat("No sorry, not yet", player, npc, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		} else if (stage == 6) {
			if (hasItem(player, 265)) {
				sendChat("Thanks for your help getting the sword for me",
						player, npc);
				sendChat("No worries mate", npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}

			if (countItem(player, 170) >= 2 && hasItem(player, BLUERITE_ORE_ID)) {
				sendChat("How are you doing finding sword materials?", npc,
						player);
				sendChat("I have them all", player, npc);
				player.getActionSender().sendMessage(
						"You hand Thurgo the items");
				takeItem(player, new InvItem(170),
						new InvItem(BLUERITE_ORE_ID), new InvItem(170));
				player.getActionSender().sendMessage(
						"Thurgo hammers the materials into a metal sword");
				sleep();
				giveItem(player, SWORD_ID);
				sendChat("Here you go", npc, player);
				sendChat("Thank you very much", player, npc);
				sendChat("Just remember to call in with more pie some time",
						npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			} else {
				sendChat("How are you doing finding sword materials?", npc,
						player);
				sendChat("I haven't found everything yet", player, npc);
				sendChat("Well come back when you do", npc, player);
				sendChat("Remember i need blurite ore and two iron bars", npc,
						player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		} else if (stage == COMPLETE) {
			sendChat("Thanks for your help getting the sword for me", player,
					npc);
			sendChat("No worries mate", npc, player, 1200);
			player.setBusy(false);
			npc.unblock();
			return;
		} else {
			sendChat("Go away", npc, player, 1200);
			player.setBusy(false);
			npc.unblock();
			return;
		}
	}

	private void givePie(final Npc npc, final Player player) {
		player.getActionSender().sendMessage("Thurgo's eyes light up");
		sleep();
		sendChat("I'd never say no to a redberry pie", npc, player);

		if (!hasItem(player, 258)) {
			sendChat("Well that's too bad, because I don't have any", player,
					npc);
			player.getActionSender().sendMessage(
					"Thurgo does not look impressed");
			sleep(1200);
			player.setBusy(false);
			npc.unblock();
			return;
		} else {
			player.getActionSender().sendMessage("You hand over the pie");
			takeItem(player, 258);
			player.setQuestStage(this, 3);
			sleep();
			sendChat("It's great stuff", npc, player);
			player.getActionSender().sendMessage("Thurgo eats the pie");
			sleep(2000);
			player.getActionSender().sendMessage("He pats his stomach");
			sleep(2000);
			sendChat("By guthix that was good pie", npc, player);
			sendChat("Anyone who makes pie like that has gotta be alright",
					npc, player, 1200);
			player.setBusy(false);
			npc.unblock();
			return;
		}
	}

	private void handleReldoChat(final Npc npc, final Player player) {
		int stage = player.getQuestStage(this);

		if (stage == 1) {
			sendChat("Hello", player, npc, 2000);
			sendChat("Hello stranger", npc, player, 2000);

			player.setBusy(false);
			int option = getMenuOption(player, "I'm in search of a quest",
					"Do you have anything to trade?", "What do you do?",
					"What do you know about the incando dwarves?");
			if (option == -1) {
				return;
			}
			player.setBusy(true);
			sleep();

			if (option == 0) {
				// shield of arrav chat
				sendChat("I don't think there's any here", npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			} else if (option == 1) {
				sendChat("No, sorry. I'm not the trading type", npc, player);
				sendChat("Ah well", player, npc, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			} else if (option == 2) {
				sendChat("I'm the palace librarian", npc, player);
				sendChat("Ah that's why you're in the library then", npc,
						player);
				sendChat("Yes", npc, player, 2000);
				sendChat(
						"Though i might be in here even if i didn't work here",
						npc, player);
				sendChat("I like reading", npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			} else {
				queueChat(
						npc,
						player,
						"The incando dwarves, you say?",
						"They were the world's most skilled smiths about a hundred years ago",
						"They used secret knowledge",
						"Which they passed down from generation to generation",
						"Unfortunatly about a century ago the once thriving race",
						"Was wiped out during the barbarian invasions of that time");
				sendChat("So are there any incando left at all?", player, npc);
				queueChat(
						npc,
						player,
						"A few of them survived",
						"But with the bulk of their population destroyed",
						"Their numbers have dwindled even further",
						"Last i knew there were a couple living in asgarnia",
						"Near the cliffs on the asgarnian southern peninsula",
						"They tend to keep to themselves",
						"They don't tend to tell people that they're the descendants of the incando",
						"Which is why people think that the tribe has died out totally",
						"You may have more luck talking to them if you bring them some red berry pie",
						"They really like red berry pie");
				sendChat("Thank you", player, npc);
				sendChat("You're welcome", npc, player, 1200);
				player.setQuestStage(this, 2);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		} else {
			sendChat("I'm busy, leave me alone", npc, player, 1200);
			player.setBusy(false);
			npc.unblock();
		}
	}

	private void handleSquireChat(final Npc npc, final Player player) {
		int stage = player.getQuestStage(this);

		if (stage == -1) {
			sendChat("Hello, i am the squire to sir vyvin", npc, player);

			player.setBusy(false);
			int option = getMenuOption(player, "And how is life as a squire?",
					"Wouldn't you prefer to be a squire for me?");
			if (option == -1) {
				return;
			}
			player.setBusy(true);
			sleep();

			if (option == 0) {
				sendChat("Well sir vyvin is a good guy to work for", npc,
						player);
				sendChat("However i'm in a spot of trouble today", npc, player);
				sendChat("I've gone and lost sir vyvin's sword", npc, player);

				player.setBusy(false);
				option = getMenuOption(player,
						"Do you know where you lost it?",
						"I can make a new sword if you like", "Is he angry?");
				if (option == -1) {
					return;
				}
				player.setBusy(true);
				sleep();

				if (option == 0) {
					sendChat("Well now if i knew that", npc, player);
					sendChat("It wouldn't be lost, now would it?", npc, player);

					mainOptionsChat(npc, player);
					return;
				} else if (option == 1) {
					makeNewSwordChat(npc, player);
					return;
				} else {
					queueChat(npc, player, "He doesn't know yet",
							"I was hoping i could think of something to do",
							"Before he does find out",
							"But i find myself at a loss");

					mainOptionsChat(npc, player);
				}
			} else {
				sendChat("No, sorry i'm loyal to vyvin", npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		} else if (stage == 1 || stage == 2 || stage == 3) {
			sendChat("So how are you doing getting a sword?", npc, player);
			sendChat("I'm still looking for incando dwarves", player, npc, 1200);
			player.setBusy(false);
			npc.unblock();
			return;
		} else if (stage == 4) {
			sendChat("So how are you doing getting a sword?", npc, player);
			sendChat("I've found an incando dwarf", player, npc);
			sendChat(
					"But he needs a picture of the sword before he can make it",
					player, npc);
			sendChat("A picture eh?", npc, player);
			sendChat(
					"The only one i can think of is in a small portrait of sir vyvin's father",
					npc, player);
			sendChat("He's holding the sword in it", npc, player);
			sendChat("Sir vyvin keeps it in a cupboard in his room i think",
					npc, player, 1200);
			player.setQuestStage(this, 5);
			player.setBusy(false);
			npc.unblock();
			return;
		} else if (stage == 5 || stage == 6) {
			if (hasItem(player, SWORD_ID)) {
				if (player.getQuestStage(this) == COMPLETE) {
					player.setBusy(false);
					npc.unblock();
					return;
				}
				sendChat("I have retrieved your sword for you", player, npc);
				sendChat("Thankyou, thankyou", npc, player);
				sendChat(
						"I was seriously worried i'd have to own up to sir vyvin",
						npc, player);
				player.getActionSender().sendMessage(
						"You give the sword to the squire");
				takeItem(player, SWORD_ID);
				sleep();
				player.setQuestStage(this, COMPLETE);
				player.incQuestPoints(QUEST_POINTS);
				player.incExp(Script.SMITHING, 4000, false);
				player.setBusy(false);
				npc.unblock();
				return;
			} else if (hasItem(player, PICTURE_ID)) {
				sendChat("So how are you doing getting a sword?", npc, player);
				sendChat("I haven't got it from Thurgo yet", player, npc);
				sendChat("Please let me know when you do", npc, player, 1200);
				player.setBusy(false);
				npc.unblock();
				return;
			} else {
				sendChat("So how are you doing getting a sword?", npc, player);
				sendChat("I've found an incando dwarf", player, npc);
				sendChat(
						"But he needs a picture of the sword before he can make it",
						player, npc);
				sendChat("A picture eh?", npc, player);
				sendChat(
						"The only one i can think of is in a small portrait of sir vyvin's father",
						npc, player);
				sendChat(
						"Sir vyvin keeps it in a cupboard in his room i think",
						npc, player, 1200);
				player.setQuestStage(this, 5);
				player.setBusy(false);
				npc.unblock();
				return;
			}
		} else if (stage == COMPLETE) {
			queueChat(npc, player, "Hello friend",
					"Thanks for your help before",
					"Vyvin never even realised it was a different sword");
			player.setBusy(false);
			npc.unblock();
			return;
		}
	}

	private void mainOptionsChat(final Npc npc, final Player player) {
		player.setBusy(false);
		int option = getMenuOption(player,
				"Well do you know the vague area you lost it?",
				"I can make a new sword if you like",
				"Well the kingdom is fairly abundant with swords",
				"Well I hope you find it soon");
		if (option == -1) {
			return;
		}
		player.setBusy(true);
		sleep();

		if (option == 0) {
			lostItChat(npc, player);
			return;
		} else if (option == 1) {
			makeNewSwordChat(npc, player);
			return;
		} else if (option == 2) {
			abundantSwordChat(npc, player);
			return;
		} else {
			findItChat(npc, player);
			return;
		}
	}

	private void abundantSwordChat(final Npc npc, final Player player) {
		queueChat(npc, player, "Yes you can get bronze swords anywhere",
				"But this isn't any old sword");

		dwarfChat(npc, player);
	}

	private void dwarfChat(final Npc npc, final Player player) {
		queueChat(
				npc,
				player,
				"The thing is, this sword is a family heirloom",
				"It has been passed down through vyvin's family for five generations",
				"It was originally made by the incando dwarves",
				"Who were a particularly skilled tribe of dwarven smiths",
				"I doubt anyone could make it in the style they do");

		player.setBusy(false);
		int option = getMenuOption(player,
				"So would these dwarves make another one?",
				"Well I hope you find it soon");
		if (option == -1) {
			return;
		}
		player.setBusy(true);
		sleep();

		if (option == 0) {
			startQuest(npc, player);
			return;
		} else if (option == 1) {
			findItChat(npc, player);
			return;
		}
	}

	private void lostItChat(final Npc npc, final Player player) {
		queueChat(
				npc,
				player,
				"No i was carrying it for him all the way from where he had it stored in Varrock",
				"It must have slipped from my pack during the trip",
				"And you know what people are like these days",
				"Someone will have just picked it up and kept it for themselves");

		player.setBusy(false);
		int option = getMenuOption(player,
				"I can make a new sword if you like",
				"Well the kingdom is fairly abundant with swords",
				"Well I hope you find it soon");
		if (option == -1) {
			return;
		}
		player.setBusy(true);
		sleep();

		if (option == 0) {
			makeNewSwordChat(npc, player);
			return;
		} else if (option == 1) {
			abundantSwordChat(npc, player);
			return;
		} else if (option == 2) {
			findItChat(npc, player);
			return;
		}
	}

	private void makeNewSwordChat(final Npc npc, final Player player) {
		sendChat("Thanks for the offer", npc, player);
		sendChat("I'd be surprised if you could though", npc, player);
		dwarfChat(npc, player);
	}

	private void findItChat(final Npc npc, final Player player) {
		sendChat("Yes me too", npc, player);
		sendChat("I'm not looking forward to telling vyvin i've lost it", npc,
				player);
		sendChat("He's going to want it for the parade next week as well", npc,
				player, 1200);
		player.setBusy(false);
		npc.unblock();
		return;
	}

	private void startQuest(final Npc npc, final Player player) {
		queueChat(
				npc,
				player,
				"I'm not a hundred percent sure the incando tribe exists anymore",
				"I should think reldo the palace librarian in varrock will know",
				"He has done a lot of research on the races of these lands",
				"I don't suppose you could try and track down the incando dwarves for me?",
				"I've got so much work to do");

		player.setBusy(false);
		int option = getMenuOption(player, "Ok I'll give it a go",
				"No I've got lots of mining work to do");
		if (option == -1) {
			return;
		}
		player.setBusy(true);
		sleep();

		if (option == 0) {
			sendChat("Thankyou very much", npc, player);
			sendChat("As i say the best place to start should be with reldo",
					npc, player, 1200);
			player.setQuestStage(this, 1); // Start quest
			player.setBusy(false);
			npc.unblock();
			return;
		} else {
			player.setBusy(false);
			npc.unblock();
			return;
		}
	}

	public KnightsSword() {
	}
}
