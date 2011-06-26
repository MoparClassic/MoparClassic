package org.moparscape.msc.gs.plugins.extras;

import java.util.ArrayList;
import java.util.Random;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.event.FightEvent;
import org.moparscape.msc.gs.event.MiniEvent;
import org.moparscape.msc.gs.event.ShortEvent;
import org.moparscape.msc.gs.event.WalkToMobEvent;
import org.moparscape.msc.gs.external.GameObjectDef;
import org.moparscape.msc.gs.model.ActiveTile;
import org.moparscape.msc.gs.model.Bubble;
import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Mob;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.states.Action;
import org.moparscape.msc.gs.util.Logger;

/**
 * 
 * @author xEnt Thieving Class, All types of thieving stored here.
 */

public class Thieving {

	public static String[] StealChats = {
			"You think i'm going to buy my own items off you? Get out of here.",
			"You've just stolen from me, I'm not selling you anything.",
			"How dare you try and buy from me after you have stolen.",
			"Thief, go away now before i call security",
			"You have guts, trying to sell my own goods back to me",
			"Real thiefs don't sell back to their victims",
			"i know better than to buy my own items from thiefs" };

	private static final World world = Instance.getWorld();

	public static int Rands(int max) {
		Random r = new Random();
		return r.nextInt(max);
	}

	public static int Rands(int min, int max) {
		Random r = new Random();
		return r.nextInt(max) + min;
	}

	public static boolean stallSuccess(int lvl, int reqLevel) {
		Random r = new Random();
		double dif = lvl - reqLevel;
		double rand = ((r.nextDouble() * 100) + 1) / 100;
		double success = ((3.27 * Math.pow(10, -6)) * Math.pow(dif, 4)
				+ (-5.516 * Math.pow(10, -4)) * Math.pow(dif, 3) + 0.014307
				* Math.pow(dif, 2) + 1.65560813 * dif + 18.2095966) / 100.0;
		if (success < 0.35)
			success = 0.35;
		if (reqLevel < 15)
			if (lvl - reqLevel < 10)
				if (Rands(1, 10) == 5)
					success = 1.0;
		success = success * 2;
		return rand < success;
	}

	public static boolean thievingSuccess(int lvl, int reqLevel) {
		Random r = new Random();
		double dif = lvl - reqLevel;
		double rand = ((r.nextDouble() * 100) + 1) / 100;
		double success = ((3.27 * Math.pow(10, -6)) * Math.pow(dif, 4)
				+ (-5.516 * Math.pow(10, -4)) * Math.pow(dif, 3) + 0.014307
				* Math.pow(dif, 2) + 1.65560813 * dif + 18.2095966) / 100.0;
		if (success < 0.35)
			success = 0.35;
		if (reqLevel < 15) {
			if (lvl - reqLevel < 10) {
				if (Rands(1, 10) == 5)
					success = 1.0;
			}
		}
		if (rand < success)
			return false;
		return true;
	}

	private Mob affectedMob;
	private Npc affectedNpc;
	private int Amount[] = { -1, -1 };
	private String[] caughtChats = { "Guards! Guards! Im being Robbed!",
			"Help Guards i am being Robbed please help!",
			"Someone help! My items are getting stolen!",
			"You'll wish you never did that, Thief!",
			"You are going to pay for that",
			"-name- how could you steal from me? Guards!",
			"-name- get your hands out of my stall!",
			"Hey -name- thats not yours!",
			"Dont steal from me -name- Im going to go tell a mod",
			"Oi! -name- you deserve a spanking!" };
	private String[] Chats = { "Oi! Get your hands out of there -name-!",
			"Hey thief! get here!", "Trying to steal from me hmm?",
			"No one steals from me!", "Take those hands off me Thief",
			"Are you trying to steal from me -name-?",
			"Dont you dare touch me", "Thief get back here now!",
			"Stealing won't get you anywhere", "Bad person! you shall die",
			"Die evil thief!", "You are going to pay for that",
			"Ill make you wish you were never born",
			"-name- i am going to hurt you", "-name- dont steal from me again",
			"Remove your filthy hands off me",
			"A real man doesn't need to steal" };
	// ID, LvlReq, Exp, RespawnTime, Loot then(amount)
	public int[][] Chests = { { 340, 59, 250, 45000, 619, 2 }, // Blood rune
			// chest.
			{ 334, 18, 8, 10000, 10, 50 }, // Next 2 nature chest.
			{ 336, 72, 500, 180000, 546, 1, 154, 1, 160, 1, 10, 1000 }, // Good
			// chest,
			// ardy..
			{ 339 } // Dont add anything to this, its a dummy chest.
	};
	private int curDoor = -1;
	// given.
	private int curStall = -1;
	private GameObjectDef def;
	// Cur X, Cur Y, New X, New Y, Lvl, Exp
	private int[][] Doors = {
			{ 586, 581, 585, 581, 10, 13 }, // Nat rune West
			// house (In)
			{ 585, 581, 586, 581, 10, 13 }, // Nat rune West house (Out)
			{ 539, 598, 539, 599, 10, 13 }, // Nat Rune East House (Out)
			{ 539, 599, 539, 598, 10, 13 }, // Nat Rune East House (Out)
			{ 609, 1547, 609, 1548, 61, 43 }, // Paladin Door (In)
			{ 609, 1548, 609, 1547, 61, 43 }, // Paladin Door (Out)
			{ 537, 3425, 536, 3425, 31, 25 }, // Ardy Door
			{ 536, 3425, 537, 3425, 31, 25 }, // Ardy Door
			{ 617, 556, 617, 555, 46, 37 }, // Blood rune door
			{ 617, 555, 617, 556, 46, 37 }, // Blood rune door
			{ 593, 3590, 593, 3589, 61, 41 }, // yanile door
			{ 593, 3589, 593, 3590, 61, 41 }, // yanile door
			{ 266, 100, 266, 99, 39, 35 }, // pirate doors wildy
			{ 266, 99, 266, 100, 39, 35 }, // pirate doors wildy
			{ 160, 103, 160, 102, 30, 28 }, // Axe hut wildy
			{ 160, 102, 160, 103, 30, 28 }, // Axe hut wildy
			{ 581, 580, 580, 580, 10, 30 }, { 580, 580, 581, 580, 10, 30 },
			{ 538, 592, 538, 591, 8, 10 }, // Some
			// ardy
			// door
			{ 538, 591, 538, 592, 8, 10 } // some ardy door
	};
	private int exp = 0;

	public int ExpMultiplier = 1; // Modify this number to multiply the XP

	private int Loot[] = { -1, -1 };

	private int lvl = 1;

	private int npcID;

	private GameObject object;

	private int ourChest = -1;

	private Player player;

	private int[][] StallNpcs = {
			{ 325, 543, 546, 597, 602 }, // Baker
			{ -1, -1, -1, -1, -1 }, { -1, -1, -1, -1, -1 },
			{ 328, 553, 558, 592, 595 }, // Silver
			// merchant
			{ 329, 542, 547, 588, 593 }, // Spice merchant
			{ 330, 549, 553, 597, 602 } // Gem merchant
	};

	/*
	 * private int[][] StallProtectorLvls = { { 28 }, // Guards { 28 }, //
	 * Guards { 28, 56 }, // Guards + Knights { 56 }, // Knights { 56, 71 }, //
	 * Knights + Paladins { 71, 83 } // Paladins + Heroes };
	 */

	// LvReq, Obj ID, Exp, RespawnTime, LootID(s)
	private int[][] Stalls = { { 5, 322, 16, 5, 330 }, // Bakers
			{ 20, 323, 24, 11, 200 }, // Silk
			{ 35, 324, 36, 18, 541 }, // Fur
			{ 50, 325, 54, 31, 383 }, // Silver
			{ 65, 326, 81, 45, 707 }, // Spice
			{ 75, 327, 16, 80, 160, 159, 158, 157 } // Gem
	};

	public Thieving(Player p, GameObject obj) {
		this.player = p;
		this.object = obj;
		this.setDef(object.getGameObjectDef());
	}

	public Thieving(Player p, Npc np, Mob mb) {
		this.player = p;
		this.affectedNpc = np;
		this.affectedMob = mb;
		npcID = affectedNpc.getID();
	}

	public void beginPickpocket() {
		try {
			boolean ret = false;

			switch (npcID) {

			case 318:
			case 11:
				exp = 8;
				lvl = 0;
				Loot[0] = 10;
				Amount[0] = 3;
				break;

			case 63:
				exp = 12;
				lvl = 10;
				Loot[0] = 10;
				Amount[0] = 9;
				break;

			case 159:
			case 320:
				exp = 26;
				lvl = 25;
				Loot[0] = 10;
				Amount[0] = 18;
				break;

			case 342:
				exp = 36;
				lvl = 32;
				setLootArrays(0);
				break;

			case 65:
			case 100:
			case 321:
				exp = 46;
				lvl = 40;
				Loot[0] = 10;
				Amount[0] = 30;
				break;

			case 322:
				exp = 85;
				lvl = 55;
				Loot[0] = 10;
				Amount[0] = 50;
				break;

			case 574:
			case 685:
				exp = 138;
				lvl = 65;
				Loot[0] = 10;
				Loot[1] = 138;
				Amount[0] = 60;
				Amount[1] = 1;
				break;

			case 323:
				exp = 152;
				lvl = 70;
				Loot[0] = 10;
				Loot[1] = 41;
				Amount[0] = 80;
				Amount[1] = 1;
				break;

			case 581:
			case 582:
			case 583:
			case 580:
				exp = 198;
				lvl = 75;
				setLootArrays(1);
				break;

			case 324:
				exp = 274;
				lvl = 80;
				setLootArrays(2);
				break;

			default:
				player.getActionSender()
						.sendMessage(
								"Sorry, this NPC has not been added to the Pickpocketing list yet.");
				Logger.println("Player " + player.getUsername()
						+ " found a NPC (pickpocket) not added, ID: " + npcID);
				ret = true;
			}

			if (ret) {
				player.setBusy(false);
				player.setSpam(false);
				return;
			}

			player.setFollowing(affectedMob);
			Instance.getDelayedEventHandler().add(
					new WalkToMobEvent(player, affectedMob, 1) {
						public void arrived() {
							if (owner.getSpam()) {
								return;
							} else {

								if (affectedMob.inCombat() || owner.isBusy()) {
									owner.setSpam(false);
									owner.setBusy(false);
									return;
								}

								if (affectedNpc == null
										|| affectedNpc.inCombat()) {
									owner.resetPath();
									owner.setBusy(false);
									owner.setSpam(false);
									return;
								} else if (owner == null) {
									affectedNpc.unblock();
									return;
								}

								if (!owner.nextTo(affectedMob)) {
									owner.setSpam(false);
									affectedMob.setBusy(false);
									owner.setBusy(false);
									return;
								}

								owner.setSpam(true);
								// affectedNpc.blockedBy(player);
								owner.setBusy(true);
								if (owner.getCurStat(17) < lvl) {
									owner.getActionSender().sendMessage(
											"You must be at least " + lvl
													+ " thieving to pick the "
													+ affectedNpc.getDef().name
													+ "'s pocket.");
									owner.setBusy(false);
									// affectedNpc.unblock();
									owner.setBusy(false);
									owner.setSpam(false);
									return;
								}

								affectedNpc.resetPath();
								Bubble bubble = new Bubble(player, 16);
								for (Player p : owner.getViewArea()
										.getPlayersInView()) {
									p.informOfBubble(bubble);
								}
								owner.getActionSender().sendMessage(
										"You attempt to pick the "
												+ affectedNpc.getDef().name
												+ "'s pocket...");

								owner.setBusy(true);

								Instance.getDelayedEventHandler().add(
										new ShortEvent(owner) {
											public void action() {
												owner.setBusy(false);
												affectedNpc.setBusy(false);
												if (chanceFormulae(lvl)) {
													owner.setSpam(false);
													owner.getActionSender()
															.sendMessage(
																	"You sucessfully stole from the "
																			+ affectedNpc
																					.getDef().name);
													for (int i = 0; i < Loot.length; i++) {
														owner.getInventory()
																.add(new InvItem(
																		Loot[i],
																		Amount[i]));
													}
													owner.getActionSender()
															.sendInventory();
													owner.incExp(17, exp
															* ExpMultiplier,
															true);
													owner.getActionSender()
															.sendStat(17);
													owner.setBusy(false);
													affectedNpc.unblock();
												} else {

													owner.setSpam(false);
													owner.getActionSender()
															.sendMessage(
																	"You fail to pick the "
																			+ affectedNpc
																					.getDef().name
																			+ "'s pocket.");
													int temp = Rand(10);
													if (temp >= 3) {
														owner.setBusy(false);
														affectedNpc.unblock();
														return;
													}

													affectedNpc.resetPath();
													owner.setBusy(true);
													if (affectedNpc == null
															|| affectedNpc
																	.inCombat()) {
														owner.resetPath();
														owner.setBusy(false);
														owner.setSpam(false);
														return;
													} else if (owner == null) {
														affectedNpc.unblock();
														return;
													}
													String msg = Chats[Rand(Chats.length)];
													msg = msg
															.replace(
																	"-name-",
																	owner.getUsername());
													owner.informOfNpcMessage(new ChatMessage(
															affectedNpc, msg,
															owner));
													world.getDelayedEventHandler()
															.add(new MiniEvent(
																	owner, 1000) {
																public void action() {
																	if (affectedNpc == null
																			|| affectedNpc
																					.inCombat()) {
																		owner.resetPath();
																		owner.setBusy(false);
																		owner.setSpam(false);
																		return;
																	} else if (owner == null) {
																		affectedNpc
																				.unblock();
																		return;
																	}
																	owner.setBusy(false);
																	// affectedNpc.unblock();
																	affectedNpc
																			.resetPath();

																	owner.resetPath();
																	owner.resetAll();
																	owner.getActionSender()
																			.sendSound(
																					"underattack");
																	owner.setStatus(Action.FIGHTING_MOB);
																	owner.getActionSender()
																			.sendMessage(
																					"You are under attack!");

																	affectedNpc
																			.resetPath();
																	affectedNpc
																			.setLocation(
																					owner.getLocation(),
																					true);
																	affectedNpc
																			.resetPath();
																	affectedNpc
																			.setBusy(true);

																	for (Player p : affectedNpc
																			.getViewArea()
																			.getPlayersInView()) {
																		p.removeWatchedNpc(affectedNpc);
																	}
																	if (affectedNpc
																			.inCombat()) {
																		owner.setBusy(false);
																		owner.resetPath();
																		affectedNpc
																				.setBusy(false); // untick
																		// this
																		return;
																	}

																	owner.setBusy(true);
																	owner.setSprite(9);
																	owner.setOpponent(affectedNpc);
																	owner.setCombatTimer();

																	affectedNpc
																			.setBusy(true);
																	affectedNpc
																			.setSprite(8);
																	affectedNpc
																			.setOpponent(owner);
																	affectedNpc
																			.setCombatTimer();

																	FightEvent fighting = new FightEvent(
																			owner,
																			affectedNpc,
																			true);
																	fighting.setLastRun(0);
																	world.getDelayedEventHandler()
																			.add(fighting);
																}
															});
												}
											}
										});
							}
						}
					});
		} catch (Exception e) {
			player.setBusy(false);
			affectedNpc.setBusy(false);
			affectedNpc.unblock();
			Logger.error(e.getMessage() + "\nStack: " + e.getStackTrace());
			player.setSpam(false);
		}

	}

	/**
	 * 
	 * I don't have the best Maths, but this is a tweak-able formula for every 5
	 * levels -xEnt
	 */

	public boolean chanceFormulae(int targetLv) {
		try {
			int chance[] = { 27, 33, 35, 37, 40, 43, 47, 51, 54, 58, 62, 66,
					71, 74, 78, 81, 84, 88, 93, 95 };
			int maxLvl[] = { 1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60,
					65, 70, 75, 80, 85, 90, 95, 100 };
			int diff = player.getMaxStat(17) - targetLv;
			int index = 0;
			for (int i = 0; i < maxLvl.length; i++)
				if (diff >= maxLvl[i] && diff < maxLvl[i] + 5)
					index = i;
			int Chance = (chance[index] < 27 ? 27 : chance[index]);
			return Rand(100) < Chance;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean checkToGetAttacked() {
		ActiveTile[][] tiles = player.getViewArea().getViewedArea(5, 5, 5, 5);
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				ActiveTile t = tiles[x][y];
				if (t != null) {
					return false;
				}
			}
		}
		return false;
	}

	private void doChest() {
		try {

			world.registerGameObject(new GameObject(object.getLocation(), 339,
					object.getDirection(), object.getType()));
			world.delayedSpawnObject(object.getLoc(), 900);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doDoor() {
		try {
			player.getActionSender().sendSound("opendoor");
			world.registerGameObject(new GameObject(object.getLocation(), 11,
					object.getDirection(), object.getType()));
			world.delayedSpawnObject(object.getLoc(), 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void lockpick() {
		try {

			if (player.isBusy() || player.inCombat() || player == null
					|| object == null) {
				player.setSpam(false);
				player.setBusy(false);
				return;
			}
			player.setBusy(true);

			boolean cont = true;
			for (int i = 0; i < Doors.length; i++) {
				if (player.getX() == Doors[i][0]
						&& player.getY() == Doors[i][1]) {
					curDoor = i;
					cont = false;
				}
			}
			if (cont) {
				player.getActionSender().sendMessage(
						"This door has not been added");
				Logger.println("Player " + player.getUsername()
						+ " found a door(lockpick) not added, ID: "
						+ object.getID() + ", Coords: " + object.getLocation());
				player.setSpam(false);
				player.setBusy(false);
				return;
			}
			if (player.getMaxStat(17) < Doors[curDoor][4]) {

				player.getActionSender()
						.sendMessage(
								"Sorry, you don't have a high enough thieving level to unlock this");
				player.setSpam(false);
				player.setBusy(false);
				return;
			}
			Bubble bubble = new Bubble(player, 714);
			for (Player p : player.getViewArea().getPlayersInView()) {
				p.informOfBubble(bubble);
			}
			player.getActionSender().sendMessage(
					"You attempt to pick the lock on the "
							+ object.getDoorDef().name);
			Instance.getDelayedEventHandler().add(new ShortEvent(player) {
				public void action() {
					if (!chanceFormulae(Doors[curDoor][4])) {
						owner.getActionSender().sendMessage(
								"You failed to unlock the door");
						owner.setSpam(false);
						owner.setBusy(false);
						return;
					} else {
						owner.getActionSender().sendMessage(
								"You sucessfully unlocked the "
										+ object.getDoorDef().name);
						doDoor();
						owner.incExp(17, Doors[curDoor][5] * ExpMultiplier,
								true);
						owner.getActionSender().sendStat(17);
						owner.setSpam(false);
						owner.setBusy(false);
						owner.teleport(Doors[curDoor][2], Doors[curDoor][3],
								false);

					}
				}
			});
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	}

	public void openThievedChest() {

		if (player.getCurStat(3) <= 2) {
			player.getActionSender().sendMessage("Go away Emo kid.");
			player.setSpam(false);
			player.setBusy(false);
			return;
		}
		int damage = player.getCurStat(3) / 9;
		player.getActionSender().sendMessage(
				"You have activated a trap on the chest");
		player.setLastDamage(damage);
		player.setCurStat(3, player.getCurStat(3) - damage);
		ArrayList<Player> playersToInform = new ArrayList<Player>();
		playersToInform.addAll(player.getViewArea().getPlayersInView());
		player.getActionSender().sendStat(3);

		for (Player p : playersToInform) {
			p.informOfModifiedHits(player);
		}

		Instance.getDelayedEventHandler().add(new MiniEvent(player, 1200) {
			public void action() {
				owner.setSpam(false);
				owner.setBusy(false);
			}
		});
	}

	public int Rand(int max) {
		Random r = new Random();
		return r.nextInt(max);
	}

	public int Rand(int min, int max) {
		Random r = new Random();
		return r.nextInt(max) + min;
	}

	private void replaceChest(int delay) {
		try {
			world.registerGameObject(new GameObject(object.getLocation(), 338,
					object.getDirection(), object.getType()));
			world.delayedSpawnObject(object.getLoc(), delay);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setLootArrays(int type) {
		try {
			int rand = Rand(100);
			if (type == 0) { // Rogue
				if (rand < 20) {
					Loot[0] = 10;
					Amount[0] = Rand(20, 40);
					return;
				}
				if (rand < 40) {
					Loot[0] = 33;
					Amount[0] = 8;
					return;
				}
				if (rand < 60) {
					Loot[0] = 714;
					Amount[0] = 1;
					return;
				}
				if (rand < 80) {
					Loot[0] = 559;
					Amount[0] = 1;
					return;
				}
				if (rand <= 100) {
					Loot[0] = 142;
					Amount[0] = 1;
					return;
				}

			} else if (type == 1) { // Gnome
				if (rand < 20) {
					Loot[0] = 10;
					Amount[0] = Rand(200, 300);
					return;
				}
				if (rand < 40) {
					Loot[0] = 34;
					Amount[0] = Rand(1, 5);
					return;
				}
				if (rand < 50) {
					Loot[0] = 612;
					Amount[0] = 1;
					return;
				}
				if (rand < 60) {
					Loot[0] = 152;
					Amount[0] = 1;
					return;
				}
				if (rand < 80) {
					Loot[0] = 895;
					Amount[0] = 1;
					return;
				}
				if (rand <= 100) {
					Loot[0] = 897;
					Amount[0] = 1;
					return;
				}
			} else { // Hero
				if (rand <= 5) {
					Loot[0] = 161;
					Amount[0] = 1;
					return;
				}
				if (rand < 15) {
					Loot[0] = 619;
					Amount[0] = 1;
					return;
				}
				if (rand < 30) {
					Loot[0] = 38;
					Amount[0] = 2;
					return;
				}
				if (rand < 40) {
					Loot[0] = 152;
					Amount[0] = 1;
					return;
				}
				if (rand < 50) {
					Loot[0] = 612;
					Amount[0] = 1;
					return;
				}
				if (rand < 60) {
					Loot[0] = 142;
					Amount[0] = 1;
					return;
				}
				if (rand <= 100) {
					Loot[0] = 10;
					Amount[0] = Rand(200, 300);
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void thieveChest() {

		if (object == null) {
			player.setSpam(false);
			player.setBusy(false);
			return;
		}

		if (object.getID() == 338) {
			player.getActionSender().sendMessage(
					"It looks like that chest has already been looted.");
			player.setSpam(false);
			player.setBusy(false);
			return;
		}

		for (int i = 0; i < Chests.length; i++)
			if (object.getID() == Chests[i][0])
				ourChest = i;

		if (ourChest == -1) {
			player.setSpam(false);
			player.setBusy(false);
			player.getActionSender()
					.sendMessage(
							"This chest has not yet been added to the chest Thieving list.");
			Logger.println("Player " + player.getUsername()
					+ " found a chest not added, ID: " + object.getID()
					+ ", Coords: " + object.getLocation());
			return;
		}

		if (player.getMaxStat(17) < Chests[ourChest][1]) {
			player.setSpam(false);
			player.setBusy(false);
			player.getActionSender().sendMessage(
					"You are not high enough level to Steal from this chest.");
			return;
		}

		player.getActionSender().sendMessage(
				"You search for traps on the chest");
		player.setBusy(true);

		Instance.getDelayedEventHandler().add(new MiniEvent(player, 300) {
			public void action() {

				owner.getActionSender().sendMessage(
						"You find a trap on the chest..");
				Bubble bubble = new Bubble(player, 549);
				for (Player p : owner.getViewArea().getPlayersInView()) {
					p.informOfBubble(bubble);
				}

				Instance.getDelayedEventHandler().add(
						new MiniEvent(player, 1000) {
							public void action() {

								owner.getActionSender().sendMessage(
										"You disarm the trap");

								Instance.getDelayedEventHandler().add(
										new MiniEvent(player, 1000) {
											public void action() {
												owner.getActionSender()
														.sendMessage(
																"You open the chest");
												doChest();

												Instance.getDelayedEventHandler()
														.add(new MiniEvent(
																player, 1200) {
															public void action() {
																replaceChest(Chests[ourChest][3]);
																owner.getActionSender()
																		.sendMessage(
																				"You find treasure inside!");
																for (int i = 4; i < Chests[ourChest].length - 1; i++) {
																	InvItem loot = new InvItem(
																			Chests[ourChest][i],
																			Chests[ourChest][i + 1]);
																	owner.getInventory()
																			.add(loot);
																	i = i + 1;
																}
																if (Chests[ourChest][0] == 340) {
																	owner.getActionSender()
																			.sendMessage(
																					"You triggered the trap!");
																	owner.teleport(
																			612,
																			568,
																			true);
																}
																owner.getActionSender()
																		.sendInventory();
																owner.incExp(
																		17,
																		Chests[ourChest][2],
																		true);
																owner.getActionSender()
																		.sendStat(
																				17);
																owner.setBusy(false);
																owner.setSpam(false);
															}
														});
											}
										});
							}
						});
			}
		});
	}

	public int thieveGem() {
		try {
			int temp = Rand(100);
			if (temp <= 40) {
				return 160;
			} else if (temp <= 70) {
				return 159;
			} else if (temp <= 90) {
				return 158;
			} else if (temp <= 100) {
				return 157;
			}
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void thieveStall() {

		try {

			if (object == null) {
				player.setSpam(false);
				// owner.packetSpam
				return;
			}
			boolean exist = false;

			for (int i = 0; i < Stalls.length; i++) {
				if (object.getID() == Stalls[i][1]) {
					curStall = i;
					exist = true;
					if (player.getMaxStat(17) < Stalls[i][0]) {
						player.getActionSender().sendMessage(
								"Sorry, you need a thieving level of "
										+ Stalls[i][0] + " to steal from that");
						player.setSpam(false);
						return;
					}
				}
			}
			if (!exist) {
				player.getActionSender().sendMessage(
						"Sorry, this stall does not exist.. contact an admin?");
				Logger.println("Player " + player.getUsername()
						+ " found a stall not added, ID: " + object.getID()
						+ ", Coords: " + object.getLocation());
				player.setSpam(false);
				return;
			}
			Bubble bubble = new Bubble(player, 609);
			for (Player p : player.getViewArea().getPlayersInView()) {
				p.informOfBubble(bubble);
			}
			player.getActionSender().sendMessage(
					"You attempt to steal from the "
							+ object.getGameObjectDef().name);
			player.setBusy(true);

			Instance.getDelayedEventHandler().add(new ShortEvent(player) {
				public void action() {

					owner.setSpam(false);
					if (object == null) {
						owner.getActionSender()
								.sendMessage(
										"There are nothing to steal as this current time");
						owner.setSpam(false);
						owner.setBusy(false);
						return;
					}
					if (!chanceFormulae(Stalls[curStall][0])) {

						owner.getActionSender().sendMessage(
								"You failed to steal from the "
										+ object.getGameObjectDef().name);
						owner.setSpam(false);
						owner.setBusy(false);
						if (Rand(20) <= 3) {
							if (StallNpcs[curStall][0] == -1) {
							} else {
								Npc person = world.getNpc(
										StallNpcs[curStall][0],
										StallNpcs[curStall][1],
										StallNpcs[curStall][2],
										StallNpcs[curStall][3],
										StallNpcs[curStall][4]);
								if (person != null) {
									owner.getNpcThief()[curStall] = true;
									String str = caughtChats[Rand(caughtChats.length)];
									str = str.replace("-name-",
											owner.getUsername());
									owner.informOfNpcMessage(new ChatMessage(
											person, str, owner));
									person.resetPath();
								}

							}
						}
						return;

					} else {
						world.registerGameObject(new GameObject(object
								.getLocation(), 341, object.getDirection(),
								object.getType()));
						world.delayedSpawnObject(object.getLoc(),
								Stalls[curStall][3] * 1000);
						owner.getActionSender().sendMessage(
								"You successfully thieved from the "
										+ object.getGameObjectDef().name);
						owner.setSpam(false);
						owner.setBusy(false);

						if (curStall == 5) {
							InvItem loot = new InvItem(thieveGem(), 1);
							owner.getInventory().add(loot);
						} else {
							InvItem loot = new InvItem(Stalls[curStall][4], 1);
							owner.getInventory().add(loot);
						}
						owner.getActionSender().sendInventory();
						owner.incExp(17, Stalls[curStall][2] * ExpMultiplier,
								true);
						owner.getActionSender().sendStat(17);
					}
				}
			});
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	}

	public void setDef(GameObjectDef def) {
		this.def = def;
	}

	public GameObjectDef getDef() {
		return def;
	}

}
