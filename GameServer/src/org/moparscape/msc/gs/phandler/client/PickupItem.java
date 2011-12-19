package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.builders.ls.MiscPacketBuilder;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.db.DBConnection;
import org.moparscape.msc.gs.event.FightEvent;
import org.moparscape.msc.gs.event.WalkToPointEvent;
import org.moparscape.msc.gs.model.ActiveTile;
import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Item;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.states.Action;

public class PickupItem implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	private Item getItem(int id, ActiveTile tile, Player player) {
		if (tile != null)
			for (Item i : tile.getItems()) {
				if (i.getID() == id && i.visibleTo(player)) {
					return i;
				}
			}
		return null;

	}

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		Point location = Point.location(p.readShort(), p.readShort());
		int id = p.readShort();
		final ActiveTile tile = world.getTile(location);
		final Item item = getItem(id, tile, player);

		if (item == null) {
			// player.setSuspiciousPlayer(true);
			player.resetPath();
			return;
		}

		if (!item.getDef().canTrade()) {
			if (item.droppedby() != 0
					&& org.moparscape.msc.gs.tools.DataConversions
							.usernameToHash(player.getUsername()) != item
							.droppedby()) {
				player.getActionSender().sendMessage(
						"This item is non-tradable.");
				return;
			}
		}

		if (player.isPMod() && !player.isMod())
			return;
		if (item.getDef().isMembers() && !World.isMembers()) {
			player.getActionSender().sendMessage(
					"This feature is only avaliable on a members server");
			return;
		}

		player.setStatus(Action.TAKING_GITEM);
		int distance = tile.hasGameObject() ? 1 : 0;
		Instance.getDelayedEventHandler().add(
				new WalkToPointEvent(player, location, distance, true) {
					public void arrived() {
						if (owner.isBusy() || owner.isRanging()
								|| !tile.hasItem(item) || !owner.nextTo(item)
								|| owner.getStatus() != Action.TAKING_GITEM) {
							return;
						}

						if (item.getID() == 23) {
							owner.getActionSender().sendMessage(
									"I can't pick it up!");
							owner.getActionSender().sendMessage(
									"I need a pot to hold it in.");
							return;
						}

						int[] Rares = { 576, 577, 578, 579, 580, 581, 828, 831,
								832, 1278, 593, 594, 795 };
						boolean pickedUpRare = false;
						if (item.getAmount() > 99999)
							pickedUpRare = true;

						for (int i = 0; i < Rares.length; i++) {
							if (item.getID() == Rares[i]) {
								pickedUpRare = true;
								break;
							}
						}
						owner.resetAll();
						InvItem invItem = new InvItem(item.getID(), item
								.getAmount());
						if (!owner.getInventory().canHold(invItem)) {
							owner.getActionSender()
									.sendMessage(
											"You cannot pickup this item, your inventory is full!");
							return;
						}
						try {
							if (item.getID() == 59 && item.getX() == 106
									&& item.getY() == 1476) {
								Npc n = world.getNpc(37, 103, 107, 1476, 1479);

								if (n != null && !n.inCombat()) {
									owner.informOfNpcMessage(new ChatMessage(n,
											"Nobody steals from this gang!",
											owner));
									fight(owner, n);
								}
							} else if (item.getID() == 501
									&& item.getX() == 333 && item.getY() == 434) {
								Npc zam = world.getNpc(140, 328, 333, 433, 438,
										true);
								if (zam != null && !zam.inCombat()) {
									owner.informOfNpcMessage(new ChatMessage(
											zam, "a curse be upon you", owner));
									for (int i = 0; i < 3; i++) {
										int stat = owner.getCurStat(i);
										if (stat < 3)
											owner.setCurStat(i, 0);
										else
											owner.setCurStat(i, stat - 3);
									}
									owner.getActionSender().sendStats();
									fight(owner, zam);
									return;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (pickedUpRare) {
							// Code goes here if they picked up some BS items.
							MiscPacketBuilder loginServer = Instance
									.getServer().getLoginConnector()
									.getActionSender();
							final long droppedby = item.droppedby();
							final long playerLong = org.moparscape.msc.gs.tools.DataConversions
									.usernameToHash(owner.getUsername());
							loginServer.tradeLog(droppedby, playerLong,
									item.getID(), item.getAmount(),
									owner.getX(), owner.getY(), 3);
						}
						world.addEntryToSnapshots(new Activity(owner
								.getUsername(), owner.getUsername()
								+ " picked up an item "
								+ item.getDef().getName() + " (" + item.getID()
								+ ") amount: " + item.getAmount() + " at: "
								+ owner.getX() + "/" + owner.getY() + "|"
								+ item.getX() + "/" + item.getY()));
						if (item.getAmount() > 10000000
								|| Formulae.isRareItem(item.getID()))
							DBConnection.getReport().submitDupeData(
									owner.getUsername(),
									owner.getUsernameHash());

						world.unregisterItem(item);
						owner.getActionSender().sendSound("takeobject");
						owner.getInventory().add(invItem);
						owner.getActionSender().sendInventory();
					}
				});
	}

	void fight(Player owner, Npc n) {
		n.resetPath();
		owner.resetPath();
		owner.resetAll();
		owner.setStatus(Action.FIGHTING_MOB);
		owner.getActionSender().sendSound("underattack");
		owner.getActionSender().sendMessage("You are under attack!");

		n.setLocation(owner.getLocation(), true);
		for (Player p : n.getViewArea().getPlayersInView()) {
			p.removeWatchedNpc(n);
		}

		owner.setBusy(true);
		owner.setSprite(9);
		owner.setOpponent(n);
		owner.setCombatTimer();

		n.setBusy(true);
		n.setSprite(8);
		n.setOpponent(owner);
		n.setCombatTimer();
		FightEvent fighting = new FightEvent(owner, n, true);
		fighting.setLastRun(0);
		Instance.getDelayedEventHandler().add(fighting);
	}

}
