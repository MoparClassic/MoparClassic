package org.moparscape.msc.gs.phandler.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.Server;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.config.Constants;
import org.moparscape.msc.gs.config.Formulae;
import org.moparscape.msc.gs.config.Constants.GameServer;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.event.FightEvent;
import org.moparscape.msc.gs.event.ObjectRemover;
import org.moparscape.msc.gs.event.WalkMobToMobEvent;
import org.moparscape.msc.gs.event.WalkToMobEvent;
import org.moparscape.msc.gs.event.WalkToPointEvent;
import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Item;
import org.moparscape.msc.gs.model.Mob;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.Projectile;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.definition.EntityHandler;
import org.moparscape.msc.gs.model.definition.skill.ItemSmeltingDef;
import org.moparscape.msc.gs.model.definition.skill.ItemWieldableDef;
import org.moparscape.msc.gs.model.definition.skill.ReqOreDef;
import org.moparscape.msc.gs.model.definition.skill.SpellDef;
import org.moparscape.msc.gs.model.landscape.ActiveTile;
import org.moparscape.msc.gs.model.landscape.ProjectilePath;
import org.moparscape.msc.gs.model.mini.Damage;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.service.ItemAttributes;
import org.moparscape.msc.gs.states.Action;

public class SpellHandler implements PacketHandler {
	static int[] spellDamage = {};
	private static TreeMap<Integer, InvItem[]> staffs = new TreeMap<Integer, InvItem[]>();

	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	static {
		staffs.put(31, new InvItem[] { new InvItem(197), new InvItem(615),
				new InvItem(682) }); // Fire-Rune
		staffs.put(32, new InvItem[] { new InvItem(102), new InvItem(616),
				new InvItem(683) }); // Water-Rune
		staffs.put(33, new InvItem[] { new InvItem(101), new InvItem(617),
				new InvItem(684) }); // Air-Rune
		staffs.put(34, new InvItem[] { new InvItem(103), new InvItem(618),
				new InvItem(685) }); // Earth-Rune
	}

	private static boolean canCast(Player player) {
		if ((player.isPMod() || player.isMod()) && !player.isAdmin()) {
			return false;
		}
		if (!player.castTimer()) {
			player.getActionSender().sendMessage(
					"You must wait another " + player.getSpellWait()
							+ " seconds to cast another spell.");
			player.resetPath();
			return false;
		}
		return true;
	}

	private static boolean checkAndRemoveRunes(Player player, SpellDef spell) {
		for (Entry<Integer, Integer> e : spell.getRunesRequired()) {
			boolean skipRune = false;
			for (InvItem staff : getStaffs(e.getKey())) {
				if (player.getInventory().contains(staff.id)) {
					for (InvItem item : player.getInventory().getItems()) {
						if (item.equals(staff) && item.wielded) {
							skipRune = true;
							break;
						}
					}
				}
			}
			if (skipRune) {
				continue;
			}
			if (player.getInventory()
					.countId(((Integer) e.getKey()).intValue()) < ((Integer) e
					.getValue()).intValue()) {
				player.setSuspiciousPlayer(true);
				player.getActionSender()
						.sendMessage(
								"You don't have all the reagents you need for this spell");
				return false;
			}
		}
		for (Entry<Integer, Integer> e : spell.getRunesRequired()) {
			boolean skipRune = false;
			for (InvItem staff : getStaffs(e.getKey())) {
				if (player.getInventory().contains(staff.id)) {
					for (InvItem item : player.getInventory().getItems()) {
						if (item.equals(staff) && item.wielded) {
							skipRune = true;
							break;
						}
					}
				}
			}
			if (skipRune) {
				continue;
			}
			player.getInventory().remove(((Integer) e.getKey()).intValue(),
					((Integer) e.getValue()).intValue(), false);
		}

		return true;
	}

	private static InvItem[] getStaffs(int runeID) {
		InvItem[] items = staffs.get(runeID);
		if (items == null) {
			return new InvItem[0];
		}
		return items;

	}

	private Random r = new Random();

	private void finalizeSpell(Player player, SpellDef spell) {
		player.incExp(6, spell.getExp(), true);
		player.setLastCast(GameEngine.getTime());
		player.getActionSender().sendSound("spellok");
		player.getActionSender().sendMessage("Cast spell successfully");
		player.setCastTimer();
	}

	public void godSpellObject(Mob affectedMob, int spell) {

		switch (spell) {
		case 33:
			GameObject guthix = new GameObject(affectedMob.getLocation(), 1142,
					0, 0);
			world.registerGameObject(guthix);
			Instance.getDelayedEventHandler().add(
					new ObjectRemover(guthix, 500));
			break;
		case 34:
			GameObject sara = new GameObject(affectedMob.getLocation(), 1031,
					0, 0);
			world.registerGameObject(sara);
			Instance.getDelayedEventHandler().add(new ObjectRemover(sara, 500));
			break;
		case 35:
			GameObject zammy = new GameObject(affectedMob.getLocation(), 1036,
					0, 0);
			world.registerGameObject(zammy);
			Instance.getDelayedEventHandler()
					.add(new ObjectRemover(zammy, 500));
			break;
		case 47:
			GameObject charge = new GameObject(affectedMob.getLocation(), 1147,
					0, 0);
			world.registerGameObject(charge);
			Instance.getDelayedEventHandler().add(
					new ObjectRemover(charge, 500));
			break;
		}
	}

	private void handleGroundCast(Player player, SpellDef spell, int id) {
		if (player.isAdmin()) {
			player.getActionSender().sendMessage("Spellid: " + id);
		}
		switch (id) {
		case 7: // Bones to bananas
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			Iterator<InvItem> inventory = player.getInventory().getItems()
					.iterator();
			int boneCount = 0;
			while (inventory.hasNext()) {
				InvItem i = inventory.next();
				if (i.id == 20) {
					inventory.remove();
					boneCount++;
				}
			}
			for (int i = 0; i < boneCount; i++) {
				player.getInventory().add(249, 1, false);
			}
			finalizeSpell(player, spell);
			break;
		case 48: // Charge

			if (!Server.isMembers()) {
				player.getActionSender().sendMessage(
						GameServer.P2P_LIMIT_MESSAGE);
				return;
			}
			if (world.getTile(player.getLocation()).hasGameObject()) {
				player.getActionSender()
						.sendMessage(
								"You cannot charge here, please move to a different area.");
				return;
			}
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			player.getActionSender().sendMessage(
					"@gre@You feel charged with magical power...");
			player.setCharged();
			godSpellObject(player, 47);

			finalizeSpell(player, spell);
			return;

		}
	}

	private void handleInvItemCast(Player player, SpellDef spell, int id,
			InvItem affectedItem, int slot) {
		switch (id) {
		case 3: // Enchant lvl-1 Sapphire amulet
			if (affectedItem.id == 302) {
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				player.getInventory().remove(affectedItem.id,
						affectedItem.amount, false);
				player.getInventory().add(314, 1, false);
				finalizeSpell(player, spell);
			} else {
				player.getActionSender().sendMessage(
						"This spell cannot be used on this kind of item");
			}
			break;
		case 10: // Low level alchemy
			if (!Server.isMembers() && affectedItem.getDef().members) {
				player.getActionSender().sendMessage(
						GameServer.P2P_LIMIT_MESSAGE);
				return;
			}
			if (affectedItem.id == 10) {
				player.getActionSender().sendMessage("You cannot alchemy that");
				return;
			}
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			if (player.getInventory().remove(affectedItem.id,
					affectedItem.amount, false)) {
				int value = (int) (affectedItem.getDef().getBasePrice() * 0.4D * affectedItem.amount);
				player.getInventory().add(10, value, false); // 40%
			}
			finalizeSpell(player, spell);
			break;
		case 13: // Enchant lvl-2 emerald amulet
			if (affectedItem.id == 303) {
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				player.getInventory().remove(affectedItem.id,
						affectedItem.amount, false);
				player.getInventory().add(315, 1, false);
				finalizeSpell(player, spell);
			} else {
				player.getActionSender().sendMessage(
						"This spell cannot be used on this kind of item");
			}
			break;
		case 21: // Superheat item
			ItemSmeltingDef smeltingDef = ItemAttributes
					.getSmeltingDef(affectedItem.id);
			if (smeltingDef == null) {
				player.getActionSender().sendMessage(
						"This spell cannot be used on this kind of item");
				return;
			}
			for (ReqOreDef reqOre : smeltingDef.getReqOres()) {
				if (player.getInventory().countId(reqOre.getId()) < reqOre
						.getAmount()) {
					if (affectedItem.id == 151) {
						smeltingDef = EntityHandler.getItemSmeltingDef(9999);
						break;
					}
					player.getActionSender().sendMessage(
							"You need "
									+ reqOre.getAmount()
									+ " "
									+ EntityHandler.getItemDef(reqOre.getId())
											.getName() + " to smelt a "
									+ affectedItem.getDef().getName() + ".");
					return;
				}
			}
			if (player.getCurStat(13) < smeltingDef.getReqLevel()) {
				player.getActionSender()
						.sendMessage(
								"You need a smithing level of "
										+ smeltingDef.getReqLevel()
										+ " to smelt this.");
				return;
			}
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			InvItem bar = new InvItem(smeltingDef.getBarId());
			if (player.getInventory().remove(affectedItem.id,
					affectedItem.amount, false)) {
				for (ReqOreDef reqOre : smeltingDef.getReqOres()) {
					player.getInventory().remove(reqOre.getId(), reqOre.amount,
							false);
				}
				player.getActionSender().sendMessage(
						"You make a " + bar.getDef().getName() + ".");
				player.getInventory().add(bar.id, bar.amount, false);
				player.incExp(13, smeltingDef.getExp(), true);
				player.getActionSender().sendStat(13);
				player.getActionSender().sendInventory();
			}
			finalizeSpell(player, spell);
			break;
		case 24: // Enchant lvl-3 ruby amulet
			if (affectedItem.id == 304) {
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				player.getInventory().remove(affectedItem.id,
						affectedItem.amount, false);
				player.getInventory().add(316, 1, false);
				finalizeSpell(player, spell);
			} else {
				player.getActionSender().sendMessage(
						"This spell cannot be used on this kind of item");
			}
			break;
		case 28: // High level alchemy
			if (!Server.isMembers() && affectedItem.getDef().members) {
				player.getActionSender().sendMessage(
						GameServer.P2P_LIMIT_MESSAGE);
				return;
			}
			if (affectedItem.id == 10) {
				player.getActionSender().sendMessage("You cannot alchemy that");
				return;
			}
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			if (player.getInventory().remove(affectedItem.id,
					affectedItem.amount, false)) {
				int value = (int) (affectedItem.getDef().getBasePrice() * 0.6D * affectedItem.amount);
				player.getInventory().add(10, value, false); // 60%
			}
			finalizeSpell(player, spell);
			break;
		case 30: // Enchant lvl-4 diamond amulet
			if (affectedItem.id == 305) {
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				player.getInventory().remove(affectedItem.id,
						affectedItem.amount, false);
				player.getInventory().add(317, 1, false);
				finalizeSpell(player, spell);
			} else {
				player.getActionSender().sendMessage(
						"This spell cannot be used on this kind of item");
			}
			break;
		case 43: // Enchant lvl-5 dragonstone amulet
			if (!Server.isMembers() && affectedItem.getDef().members) {
				player.getActionSender().sendMessage(
						GameServer.P2P_LIMIT_MESSAGE);
				return;
			}
			if (affectedItem.id == 610) {
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				player.getInventory().remove(affectedItem.id,
						affectedItem.amount, false);
				player.getInventory().add(522, 1, false);
				finalizeSpell(player, spell);
			} else {
				player.getActionSender().sendMessage(
						"This spell cannot be used on this kind of item");
			}
			break;

		}
		if (affectedItem.wielded) {
			player.getActionSender().sendSound("click");
			player.getInventory().setWield(slot, false);
			ItemWieldableDef def = ItemAttributes.getWieldable(affectedItem.id);
			player.updateWornItems(def.getWieldPos(), player
					.getPlayerAppearance().getSprite(def.getWieldPos()));
			player.getActionSender().sendEquipmentStats();
		}
	}

	private void handleItemCast(Player player, final SpellDef spell,
			final int id, final Item affectedItem) {
		player.setStatus(Action.CASTING_GITEM);
		Instance.getDelayedEventHandler().add(
				new WalkToPointEvent(player, affectedItem.getLocation(), 5,
						true) {
					public void arrived() {
						owner.resetPath();
						ActiveTile tile = world.getTile(location);
						if (!canCast(owner) || !tile.hasItem(affectedItem)
								|| owner.getStatus() != Action.CASTING_GITEM
								|| affectedItem.isRemoved()) {
							return;
						}
						// check if the item is a rare
						/*
						 * int itemid = affectedItem.id; if(itemid == 828 ||
						 * itemid == 831 || itemid == 832 || itemid == 422 ||
						 * itemid == 1289) {
						 * owner.getActionSender().sendMessage(
						 * "Using Telekinetic grab on rare drops isn't fun ):");
						 * return; }
						 */
						owner.resetAllExceptDueling();
						switch (id) {
						case 16: // Telekinetic grab
							if (!checkAndRemoveRunes(owner, spell)) {
								return;
							}
							owner.getActionSender().sendTeleBubble(
									location.getX(), location.getY(), true);
							for (Object o : owner.getWatchedPlayers()
									.getAllEntities()) {
								Player p = ((Player) o);
								p.getActionSender().sendTeleBubble(
										location.getX(), location.getY(), true);
							}
							world.unregisterItem(affectedItem);
							finalizeSpell(owner, spell);
							owner.getInventory().add(affectedItem.getID(),
									affectedItem.getAmount(), false);
							break;
						}
						owner.getActionSender().sendInventory();
						owner.getActionSender().sendStat(6);
					}
				});
	}

	private void handleMobCast(final Player player, Mob affectedMob,
			final int spellID) {
		if (player.isDueling() && affectedMob instanceof Player) {
			Player aff = (Player) affectedMob;
			if (!player.getWishToDuel().getUsername().toLowerCase()
					.equals(aff.getUsername().toLowerCase()))
				return;
		}
		if (player.isAdmin()) {
			player.getActionSender().sendMessage("Spellid: " + spellID);
		}
		if (!new ProjectilePath(player.getX(), player.getY(),
				affectedMob.getX(), affectedMob.getY()).isValid()) {
			player.getActionSender().sendMessage(
					"I can't get a clear shot from here");
			player.resetPath();
			return;
		}
		if (affectedMob instanceof Player) {
			Player other = (Player) affectedMob;
			if (player.getLocation().inWilderness()
					&& GameEngine.getTime() - other.getLastRun() < 1000) {
				player.resetPath();
				return;
			}
		}
		if (player.getLocation().inWilderness()
				&& GameEngine.getTime() - player.getLastRun() < 3000) {
			player.resetPath();
			return;
		}
		player.setFollowing(affectedMob);
		player.setStatus(Action.CASTING_MOB);
		Instance.getDelayedEventHandler().add(
				new WalkToMobEvent(player, affectedMob, 5) {
					public void arrived() {
						if (!new ProjectilePath(owner.getX(), owner.getY(),
								affectedMob.getX(), affectedMob.getY())
								.isValid()) {
							owner.getActionSender().sendMessage(
									"I can't get a clear shot from here");
							owner.resetPath();
							return;
						}
						player.resetFollowing();
						owner.resetPath();

						SpellDef spell = EntityHandler.getSpellDef(spellID);
						if (!canCast(owner) || affectedMob.getHits() <= 0
								|| !owner.checkAttack(affectedMob, true)
								|| owner.getStatus() != Action.CASTING_MOB) {
							player.resetPath();
							return;
						}
						owner.resetAllExceptDueling();
						switch (spellID) {
						case 1: // Confuse

						case 5: // Weaken
						case 9: // Curse

							if (affectedMob instanceof Npc) {
								Npc np = (Npc) affectedMob;
								if (spellID == 1) {
									if (np.confused) {
										owner.getActionSender()
												.sendMessage(
														"Your oponent is already confused");
										return;
									}
								}
								if (spellID == 5) {
									if (np.weakend) {
										owner.getActionSender()
												.sendMessage(
														"Your oponent is already weakend");
										return;
									}
								}
								if (spellID == 9) {
									if (np.cursed) {
										owner.getActionSender()
												.sendMessage(
														"Your oponent is already cursed");
										return;
									}
								}
							} /*
							 * else { player.getActionSender().sendMessage(
							 * "Currently Unavaliable"); return; }
							 */

							if (affectedMob instanceof Player
									&& !owner.isDueling()) {
								Player affectedPlayer = (Player) affectedMob;
								owner.setSkulledOn(affectedPlayer);
							}
							int stat = -1;
							if (spellID == 1)
								stat = 0;
							else if (spellID == 5)
								stat = 2;
							else if (spellID == 9)
								stat = 1;

							int statLv = -1;
							if (affectedMob instanceof Player) {
								Player affectedPlayer = (Player) affectedMob;
								statLv = affectedPlayer.getCurStat(stat);

							} else if (affectedMob instanceof Npc) {
								Npc n = (Npc) affectedMob;
								if (stat == 0)
									statLv = n.getAttack();
								else if (stat == 1)
									statLv = n.getDefense();
								else if (stat == 2)
									statLv = n.getStrength();
							}
							if (statLv == -1 || stat == -1)
								return;

							if (affectedMob instanceof Player) {
								Player affectedPlayer = (Player) affectedMob;
								if (affectedPlayer.getCurStat(stat) < affectedPlayer
										.getMaxStat(stat)
										- (affectedPlayer.getMaxStat(stat) / 20)) {
									owner.getActionSender().sendMessage(
											"Your opponent is already stunned");
									return;
								} else {
									affectedPlayer.setCurStat(
											stat,
											affectedPlayer.getCurStat(stat)
													- (affectedPlayer
															.getMaxStat(stat) / 20));
									affectedPlayer.getActionSender()
											.sendStats();
									affectedPlayer
											.getActionSender()
											.sendMessage(
													owner.getUsername()
															+ " has stunned you");
								}

							} else if (affectedMob instanceof Npc) {
								Npc n = (Npc) affectedMob;
								if (spellID == 1)
									n.confused = true;
								else if (spellID == 5)
									n.weakend = true;
								else if (spellID == 9)
									n.cursed = true;

							}
							if (!checkAndRemoveRunes(owner, spell)) {
								return;
							}

							Projectile projectilez = new Projectile(owner,
									affectedMob, 1);
							ArrayList<Player> playersToInformm = new ArrayList<Player>();
							playersToInformm.addAll(owner.getViewArea()
									.getPlayersInView());
							playersToInformm.addAll(affectedMob.getViewArea()
									.getPlayersInView());
							for (Player p : playersToInformm) {
								p.informOfProjectile(projectilez);
								p.informOfModifiedHits(affectedMob);
							}

							finalizeSpell(owner, spell);

							owner.getActionSender().sendInventory();
							owner.getActionSender().sendStat(6);
							return;
						case 19: // Crumble undead

							if (affectedMob instanceof Player) {
								owner.getActionSender()
										.sendMessage(
												"You can not use this spell on a Player");
								return;
							}
							if (!checkAndRemoveRunes(owner, spell)) {
								return;
							}
							Npc n = (Npc) affectedMob;
							int damaga = Formulae.Rand(1, 5);
							boolean foundd = false;
							if (n.undead) {
								foundd = true;
								damaga = Formulae
										.Rand(3,
												Constants.GameServer.CRUMBLE_UNDEAD_MAX);
							}

							if (!foundd) {
								owner.getActionSender()
										.sendMessage(
												"You must use this spell on undead monsters only");
								return;
							}
							if (Formulae.Rand(0, 8) == 2)
								damaga = 0;

							Projectile projectilee = new Projectile(owner,
									affectedMob, 1);
							affectedMob.setLastDamage(damaga);
							int newp = affectedMob.getHits() - damaga;
							affectedMob.setHits(newp);
							ArrayList<Player> playersToInforms = new ArrayList<Player>();
							playersToInforms.addAll(owner.getViewArea()
									.getPlayersInView());
							playersToInforms.addAll(affectedMob.getViewArea()
									.getPlayersInView());
							for (Player p : playersToInforms) {
								p.informOfProjectile(projectilee);
								p.informOfModifiedHits(affectedMob);
							}
							if (newp <= 0) {
								affectedMob.killedBy(owner, owner.isDueling());
								if (owner instanceof Player
										&& affectedMob instanceof Npc) {
									((Npc) affectedMob).getSyndicate()
											.distributeExp((Npc) affectedMob);
								}
							}
							finalizeSpell(owner, spell);

							owner.getActionSender().sendInventory();
							owner.getActionSender().sendStat(6);
							return;
						case 42: // vulnerability
						case 45: // Enfeeble
						case 47: // Stun
							owner.getActionSender().sendMessage(
									"@or1@This spell is not yet implemented.");
							break;
						case 25:
							if (owner.getLocation().inWilderness()) {
								owner.getActionSender().sendMessage(
										"Can not use this spell in wilderness");
								return;
							}
							if (affectedMob instanceof Npc) {
								if (((Npc) affectedMob).getID() == 477) {
									player.getActionSender()
											.sendMessage(
													"The dragon seems immune to this spell");
									return;
								}
							}
							boolean flagispro = false;
							ListIterator<InvItem> iterator22 = owner
									.getInventory().getItems().listIterator();
							while (iterator22.hasNext()) {
								InvItem cape = (InvItem) iterator22.next();
								if (cape.id == 1000 && cape.wielded) {
									flagispro = flagispro || true;
								}
								// else {flag = false;}
							}
							if (flagispro) {
								if (!owner.isCharged()) {
									owner.getActionSender().sendMessage(
											"@red@You are not charged!");
								}

								if (!checkAndRemoveRunes(owner, spell)) {
									return;
								}
								if (affectedMob instanceof Player
										&& !owner.isDueling()) {
									Player affectedPlayer = (Player) affectedMob;
									owner.setSkulledOn(affectedPlayer);
								}
								int damag = Formulae.calcSpellHit(20,
										owner.getMagicPoints());
								if (affectedMob instanceof Player) {
									Player affectedPlayer = (Player) affectedMob;
									affectedPlayer
											.getActionSender()
											.sendMessage(
													owner.getUsername()
															+ " is shooting at you!");
								}
								Projectile projectil = new Projectile(owner,
										affectedMob, 1);
								// godSpellObject(affectedMob, 33);
								affectedMob.setLastDamage(damag);
								int newhp = affectedMob.getHits() - damag;
								affectedMob.setHits(newhp);
								ArrayList<Player> playersToInfor = new ArrayList<Player>();
								playersToInfor.addAll(owner.getViewArea()
										.getPlayersInView());
								playersToInfor.addAll(affectedMob.getViewArea()
										.getPlayersInView());
								for (Player p : playersToInfor) {
									p.informOfProjectile(projectil);
									p.informOfModifiedHits(affectedMob);
								}
								if (affectedMob instanceof Player) {
									Player affectedPlayer = (Player) affectedMob;
									affectedPlayer.getActionSender()
											.sendStat(3);
								}
								if (newhp <= 0) {
									affectedMob.killedBy(owner,
											owner.isDueling());
								}
								owner.getActionSender().sendInventory();
								owner.getActionSender().sendStat(6);
								finalizeSpell(owner, spell);
								break;
							} else {
								owner.getActionSender()
										.sendMessage(
												"You need to be wearing the Iban Staff to cast this spell!");
								return;
							}
						case 33:
							if (owner.getLocation().inWilderness()) {
								owner.getActionSender().sendMessage(
										"Can not use this spell in wilderness");
								return;
							}
							if (affectedMob instanceof Npc) {
								if (((Npc) affectedMob).getID() == 477) {
									player.getActionSender()
											.sendMessage(
													"The dragon seems immune to this spell");
									return;
								}
							}
							boolean flag = false;
							ListIterator<InvItem> iterator = owner
									.getInventory().getItems().listIterator();
							while (iterator.hasNext()) {
								InvItem cape = iterator.next();
								if (cape.id == 1217 && cape.wielded) {
									flag = true;
								}
								// else {flag = false;}
							}
							if (flag) {
								if (!owner.isCharged()) {
									owner.getActionSender().sendMessage(
											"@red@You are not charged!");
								}

								if (!checkAndRemoveRunes(owner, spell)) {
									return;
								}
								if (affectedMob instanceof Player
										&& !owner.isDueling()) {
									Player affectedPlayer = (Player) affectedMob;
									owner.setSkulledOn(affectedPlayer);
								}
								int damag = Formulae.calcGodSpells(owner,
										affectedMob);
								// int damag = Formulae.calcSpellHit(max,
								// owner.getMagicPoints());
								if (affectedMob instanceof Player) {
									Player affectedPlayer = (Player) affectedMob;
									affectedPlayer
											.getActionSender()
											.sendMessage(
													owner.getUsername()
															+ " is shooting at you!");
								}
								Projectile projectil = new Projectile(owner,
										affectedMob, 1);
								godSpellObject(affectedMob, 33);
								affectedMob.setLastDamage(damag);
								int newhp = affectedMob.getHits() - damag;
								affectedMob.setHits(newhp);
								ArrayList<Player> playersToInfor = new ArrayList<Player>();
								playersToInfor.addAll(owner.getViewArea()
										.getPlayersInView());
								playersToInfor.addAll(affectedMob.getViewArea()
										.getPlayersInView());
								for (Player p : playersToInfor) {
									p.informOfProjectile(projectil);
									p.informOfModifiedHits(affectedMob);
								}
								if (affectedMob instanceof Player) {
									Player affectedPlayer = (Player) affectedMob;
									affectedPlayer.getActionSender()
											.sendStat(3);
								}
								if (newhp <= 0) {
									affectedMob.killedBy(owner,
											owner.isDueling());
								}
								owner.getActionSender().sendInventory();
								owner.getActionSender().sendStat(6);
								finalizeSpell(owner, spell);
								break;
							} else {
								owner.getActionSender()
										.sendMessage(
												"You need to be wearing the Staff of Guthix to cast this spell!");
								return;
							}
						case 34:
							if (affectedMob instanceof Npc) {
								if (((Npc) affectedMob).getID() == 477) {
									player.getActionSender()
											.sendMessage(
													"The dragon seems immune to this spell");
									return;
								}
							}
							if (owner.getLocation().inWilderness()) {
								owner.getActionSender().sendMessage(
										"Can not use this spell in wilderness");
								return;
							}
							boolean bool = false;
							ListIterator<InvItem> iterat = owner.getInventory()
									.getItems().listIterator();
							while (iterat.hasNext()) {
								InvItem cape = iterat.next();
								if (cape.id == 1218 && cape.wielded) {
									bool = bool || true;
								}
								// else {flag = false;}
							}
							if (bool) {
								if (!owner.isCharged()) {
									owner.getActionSender().sendMessage(
											"@red@You are not charged!");
								}

								if (!checkAndRemoveRunes(owner, spell)) {
									return;
								}
								if (affectedMob instanceof Player
										&& !owner.isDueling()) {
									Player affectedPlayer = (Player) affectedMob;
									owner.setSkulledOn(affectedPlayer);
								}
								// int damag = Rand(0, 25);
								int damag = Formulae.calcGodSpells(owner,
										affectedMob);
								if (affectedMob instanceof Player) {
									Player affectedPlayer = (Player) affectedMob;
									affectedPlayer
											.getActionSender()
											.sendMessage(
													owner.getUsername()
															+ " is shooting at you!");
								}
								Projectile projectil = new Projectile(owner,
										affectedMob, 1);
								godSpellObject(affectedMob, 34);
								affectedMob.setLastDamage(damag);
								int newhp = affectedMob.getHits() - damag;
								affectedMob.setHits(newhp);
								ArrayList<Player> playersToInfor = new ArrayList<Player>();
								playersToInfor.addAll(owner.getViewArea()
										.getPlayersInView());
								playersToInfor.addAll(affectedMob.getViewArea()
										.getPlayersInView());
								for (Player p : playersToInfor) {
									p.informOfProjectile(projectil);
									p.informOfModifiedHits(affectedMob);
								}
								if (affectedMob instanceof Player) {
									Player affectedPlayer = (Player) affectedMob;
									affectedPlayer.getActionSender()
											.sendStat(3);
								}
								if (newhp <= 0) {
									affectedMob.killedBy(owner,
											owner.isDueling());
								}
								owner.getActionSender().sendInventory();
								owner.getActionSender().sendStat(6);
								finalizeSpell(owner, spell);
								break;
							} else {
								owner.getActionSender()
										.sendMessage(
												"You need to be wearing the Staff of Saradomin to cast this spell!");
								return;
							}
						case 35:
							if (affectedMob instanceof Npc) {
								if (((Npc) affectedMob).getID() == 477) {
									player.getActionSender()
											.sendMessage(
													"The dragon seems immune to this spell");
									return;
								}
							}
							if (owner.getLocation().inWilderness()) {
								owner.getActionSender().sendMessage(
										"Can not use this spell in wilderness");
								return;
							}
							boolean flag2 = false;
							ListIterator<InvItem> iterato = owner
									.getInventory().getItems().listIterator();
							while (iterato.hasNext()) {
								InvItem cape = iterato.next();
								if (cape.id == 1216 && cape.wielded) {
									flag2 = flag2 || true;
								}
								// else {flag = false;}
							}
							if (flag2) {
								if (!owner.isCharged()) {
									owner.getActionSender().sendMessage(
											"@red@You are not charged!");
								}

								if (!checkAndRemoveRunes(owner, spell)) {
									return;
								}
								if (affectedMob instanceof Player
										&& !owner.isDueling()) {
									Player affectedPlayer = (Player) affectedMob;
									owner.setSkulledOn(affectedPlayer);
								}
								// int damag = Rand(0, 25);
								int damag = Formulae.calcGodSpells(owner,
										affectedMob);
								if (affectedMob instanceof Player) {
									Player affectedPlayer = (Player) affectedMob;
									affectedPlayer
											.getActionSender()
											.sendMessage(
													owner.getUsername()
															+ " is shooting at you!");
								}
								Projectile projectil = new Projectile(owner,
										affectedMob, 1);
								godSpellObject(affectedMob, 35);
								affectedMob.setLastDamage(damag);
								int newhp = affectedMob.getHits() - damag;
								affectedMob.setHits(newhp);
								ArrayList<Player> playersToInfor = new ArrayList<Player>();
								playersToInfor.addAll(owner.getViewArea()
										.getPlayersInView());
								playersToInfor.addAll(affectedMob.getViewArea()
										.getPlayersInView());
								for (Player p : playersToInfor) {
									p.informOfProjectile(projectil);
									p.informOfModifiedHits(affectedMob);
								}
								if (affectedMob instanceof Player) {
									Player affectedPlayer = (Player) affectedMob;
									affectedPlayer.getActionSender()
											.sendStat(3);
								}
								if (newhp <= 0) {
									affectedMob.killedBy(owner,
											owner.isDueling());
								}
								owner.getActionSender().sendInventory();
								owner.getActionSender().sendStat(6);
								finalizeSpell(owner, spell);
								break;
							} else {
								owner.getActionSender()
										.sendMessage(
												"You need to be wearing the Staff of Zaramorak to cast this spell");
								return;
							}

						default:
							if (spell.getReqLevel() == 62
									|| spell.getReqLevel() == 65
									|| spell.getReqLevel() == 70
									|| spell.getReqLevel() == 75) {
								if (!Config.members) {
									player.getActionSender()
											.sendMessage(
													"Must be on a members server to use this");
									return;
								}
								if (player.getLocation().inWilderness()
										&& Config.f2pWildy) {
									player.getActionSender()
											.sendMessage(
													"You can not cast this Members spell in F2P Wilderness");
									return;
								}
							}
							if (!checkAndRemoveRunes(owner, spell)) {
								return;
							}

							int max = -1;
							for (int i = 0; i < Constants.GameServer.SPELLS.length; i++) {
								if (spell.getReqLevel() == Constants.GameServer.SPELLS[i][0])
									max = Constants.GameServer.SPELLS[i][1];
							}
							if (player.getMagicPoints() > 30)
								max += 1;
							int damage = Formulae.calcSpellHit(max,
									owner.getMagicPoints());
							if (affectedMob instanceof Player) {
								Player affectedPlayer = (Player) affectedMob;
								affectedPlayer.getActionSender().sendMessage(
										owner.getUsername()
												+ " is shooting at you!");
							}

							if (affectedMob instanceof Npc) {
								Npc npcc = (Npc) affectedMob;
								npcc.getSyndicate().addDamage(owner, damage,
										Damage.MAGIC_DAMAGE);
							}
							Projectile projectile = new Projectile(owner,
									affectedMob, 1);
							affectedMob.setLastDamage(damage);
							int newHp = affectedMob.getHits() - damage;
							affectedMob.setHits(newHp);

							ArrayList<Player> playersToInform = new ArrayList<Player>();
							playersToInform.addAll(owner.getViewArea()
									.getPlayersInView());
							playersToInform.addAll(affectedMob.getViewArea()
									.getPlayersInView());
							for (Player p : playersToInform) {
								p.informOfProjectile(projectile);
								p.informOfModifiedHits(affectedMob);
							}
							if (affectedMob instanceof Player) {
								Player affectedPlayer = (Player) affectedMob;
								affectedPlayer.getActionSender().sendStat(3);
							}
							if (newHp <= 0) {
								if (affectedMob instanceof Player)
									affectedMob.killedBy(owner,
											owner.isDueling());

								if (owner instanceof Player) {
									Player toLoot = owner;
									if (affectedMob instanceof Npc) {
										Npc npc = (Npc) affectedMob;
										npc.getSyndicate().distributeExp(npc);
									}
									if (!(affectedMob instanceof Player))
										affectedMob.killedBy(toLoot,
												owner.isDueling());
								}
							}
							finalizeSpell(owner, spell);
							if (newHp > 0) {
								if (affectedMob instanceof Npc) {
									final Npc npc = (Npc) affectedMob;

									if (npc.isBusy()
											|| npc.getChasing() != null)
										return;

									npc.resetPath();
									npc.setChasing(player);

									// Radius is 0 to prevent wallhacking by
									// NPCs. Easiest method I can come up with
									// for now.
									Instance.getDelayedEventHandler().add(
											new WalkMobToMobEvent(affectedMob,
													owner, 0) {
												public void arrived() {
													if (affectedMob.isBusy()
															|| owner.isBusy()) {
														npc.setChasing(null);
														return;
													}
													if (affectedMob.inCombat()
															|| owner.inCombat()) {
														npc.setChasing(null);
														return;
													}

													npc.resetPath();
													player.resetAll();
													player.resetPath();
													player.setBusy(true);
													player.setStatus(Action.FIGHTING_MOB);
													player.getActionSender()
															.sendSound(
																	"underattack");
													player.getActionSender()
															.sendMessage(
																	"You are under attack!");

													npc.setLocation(player
															.getLocation(),
															true);
													for (Player p : npc
															.getViewArea()
															.getPlayersInView())
														p.removeWatchedNpc(npc);

													player.setSprite(9);
													player.setOpponent(npc);
													player.setCombatTimer();

													npc.setBusy(true);
													npc.setSprite(8);
													npc.setOpponent(player);
													npc.setCombatTimer();

													npc.setChasing(null);
													FightEvent fighting = new FightEvent(
															player, npc, true);
													fighting.setLastRun(0);
													world.getDelayedEventHandler()
															.add(fighting);
												}

												public void failed() {
													npc.setChasing(null);
												}
											});
								}
							}
							break;
						}
						owner.getActionSender().sendInventory();
						owner.getActionSender().sendStat(6);
					}
				});
	}

	public void handlePacket(Packet p, IoSession session) throws Exception {

		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();
		if ((player.isBusy() && !player.inCombat()) || player.isRanging()) {
			return;
		}

		if (player.isDueling() && player.getDuelSetting(1)) {
			player.getActionSender().sendMessage(
					"Magic is disabled in this duel");
			return;
		}
		player.resetAllExceptDueling();
		int idx = p.readShort();
		if (idx < 0 || idx >= 49) {
			player.setSuspiciousPlayer(true);
			return;
		}
		if (!canCast(player)) {
			return;
		}
		world.addEntryToSnapshots(new Activity(player.getUsername(), player
				.getUsername()
				+ " tried to cast spell ("
				+ 49
				+ "): "
				+ player.getX() + "/" + player.getY()));

		SpellDef spell = EntityHandler.getSpellDef(idx);
		if (player.getCurStat(6) < spell.getReqLevel()) {
			player.setSuspiciousPlayer(true);
			player.getActionSender().sendMessage(
					"Your magic ability is not high enough for this spell.");
			player.resetPath();
			return;
		}
		if (!Formulae.castSpell(spell, player.getCurStat(6),
				player.getMagicPoints())) {
			player.getActionSender().sendMessage(
					"The spell fails, you may try again in 20 seconds.");
			player.getActionSender().sendSound("spellfail");
			player.setSpellFail();
			player.resetPath();
			return;
		}
		switch (pID) {
		case 206: // Cast on self
			if (player.isDueling()) {
				player.getActionSender().sendMessage(
						"This type of spell cannot be used in a duel.");
				return;
			}

			if (spell.getSpellType() == 0) {

				handleTeleport(player, spell, idx);
				return;
			}
			// if(spell.getSpellType() == 6) {
			handleGroundCast(player, spell, idx);
			// }
			break;
		case 55: // Cast on player
			if (spell.getSpellType() == 1 || spell.getSpellType() == 2) {
				Player affectedPlayer = world.getPlayer(p.readShort());
				if (affectedPlayer == null) { // This shouldn't happen
					player.resetPath();
					return;
				}
				if (player.withinRange(affectedPlayer, 5)) {
					player.resetPath();
				}
				handleMobCast(player, affectedPlayer, idx);
			}
			// if(spell.getSpellType() == 6) {
			// handleGroundCast(player, spell);
			// }
			break;
		case 71: // Cast on npc
			if (player.isDueling()) {
				return;
			}
			if (spell.getSpellType() == 2) {
				Npc affectedNpc = world.getNpc(p.readShort());
				if (affectedNpc == null) { // This shouldn't happen
					player.resetPath();
					return;
				}

				if (affectedNpc.getID() == 35) {
					player.getActionSender()
							.sendMessage(
									"Delrith can not be attacked without the Silverlight sword");
					return;
				}
				if (player.withinRange(affectedNpc, 5)) {
					player.resetPath();
				}
				handleMobCast(player, affectedNpc, idx);
			}
			// if(spell.getSpellType() == 6) {
			// handleGroundCast(player, spell);
			// }
			break;
		case 49: // Cast on inventory item
			if (player.isDueling()) {
				player.getActionSender().sendMessage(
						"This type of spell cannot be used in a duel.");
				return;
			}
			if (spell.getSpellType() == 3) {
				int slot = p.readShort();
				InvItem item = player.getInventory().getSlot(slot);
				if (item == null) { // This shoudln't happen
					player.resetPath();
					return;
				}
				handleInvItemCast(player, spell, idx, item, slot);
			}
			// if(spell.getSpellType() == 6) {
			// handleGroundCast(player, spell);
			// }
			break;
		case 67: // Cast on door - type 4
			if (player.isDueling()) {
				player.getActionSender().sendMessage(
						"This type of spell cannot be used in a duel.");
				return;
			}
			player.getActionSender().sendMessage(
					"@or1@This type of spell is not yet implemented.");
			// if(spell.getSpellType() == 6) {
			// handleGroundCast(player, spell);
			// }
			break;
		case 17: // Cast on game object - type 5
			if (player.isDueling()) {
				player.getActionSender().sendMessage(
						"This type of spell cannot be used in a duel.");
				return;
			}
			player.getActionSender().sendMessage(
					"@or1@This type of spell is not yet implemented.");
			// if(spell.getSpellType() == 6) {
			// handleGroundCast(player, spell);
			// }
			break;
		case 104: // Cast on ground item
			if (player.isDueling()) {
				player.getActionSender().sendMessage(
						"This type of spell cannot be used in a duel.");
				return;
			}
			ActiveTile t = world.getTile(p.readShort(), p.readShort());
			int itemId = p.readShort();
			Item affectedItem = null;
			for (Item i : t.getItems()) {
				if (i.getID() == itemId && i.visibleTo(player)) {
					affectedItem = i;
					break;
				}
			}
			t.cleanItself();
			if (affectedItem == null) { // This shouldn't happen
				return;
			}
			handleItemCast(player, spell, idx, affectedItem);
			break;
		case 232: // Cast on ground - type 6
			if (player.isDueling()) {
				player.getActionSender().sendMessage(
						"This type of spell cannot be used in a duel.");
				return;
			}
			// if(spell.getSpellType() == 6) {
			handleGroundCast(player, spell, idx);
			// }
			break;
		}
		player.getActionSender().sendInventory();
		player.getActionSender().sendStat(6);
	}// System.out

	private void handleTeleport(Player player, SpellDef spell, int id) {

		if (player.inCombat()) {
			return;
		}
		if (player.getLocation().wildernessLevel() >= 20
				|| (player.getLocation().inModRoom() && !player.isMod())) {
			player.getActionSender().sendMessage(
					"A magical force stops you from teleporting.");
			return;
		}
		if (!checkAndRemoveRunes(player, spell)) {
			return;
		}
		switch (id) {
		case 12: // Varrock
			player.teleport(120, 504, true);
			break;
		case 15: // Lumbridge
			player.teleport(120, 648, true);
			break;
		case 18: // Falador
			player.teleport(312, 552, true);
			break;
		case 22: // Camalot
			if (!Server.isMembers()) {
				player.getActionSender().sendMessage(
						GameServer.P2P_LIMIT_MESSAGE);
				return;
			}
			player.teleport(465, 456, true);
			break;
		case 26: // Ardougne
			if (!Server.isMembers()) {
				player.getActionSender().sendMessage(
						GameServer.P2P_LIMIT_MESSAGE);
				return;
			}
			player.teleport(585, 621, true);
			break;
		case 31: // Watchtower
			if (!Server.isMembers()) {
				player.getActionSender().sendMessage(
						GameServer.P2P_LIMIT_MESSAGE);
				return;
			}
			player.teleport(637, 2628, true);
			break;
		case 37: // Lost city
			if (!Server.isMembers()) {
				player.getActionSender().sendMessage(
						GameServer.P2P_LIMIT_MESSAGE);
				return;
			}
			player.teleport(131, 3544, true);
			break;
		}
		finalizeSpell(player, spell);
		player.getActionSender().sendInventory();
	}

	public final int Rand(int low, int high) {
		return low + r.nextInt(high - low);
	}

}
