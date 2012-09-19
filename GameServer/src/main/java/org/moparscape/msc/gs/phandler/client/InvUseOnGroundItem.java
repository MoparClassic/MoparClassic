package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.event.DelayedEvent;
import org.moparscape.msc.gs.event.ShortEvent;
import org.moparscape.msc.gs.event.WalkToPointEvent;
import org.moparscape.msc.gs.external.EntityHandler;
import org.moparscape.msc.gs.external.FiremakingDef;
import org.moparscape.msc.gs.model.ActiveTile;
import org.moparscape.msc.gs.model.Bubble;
import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Item;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.states.Action;
import org.moparscape.msc.gs.tools.DataConversions;

public class InvUseOnGroundItem implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	private Item getItem(int id, ActiveTile tile, Player player) {
		for (Item i : tile.getItems()) {
			if (i.getID() == id && i.visibleTo(player)) {
				return i;
			}
		}
		return null;
	}

	public void handlePacket(Packet p, IoSession session) throws Exception {
		try {
			Player player = (Player) session.getAttachment();
			if (player.isBusy()) {
				player.resetPath();
				return;
			}
			// incExp
			player.resetAll();
			Point location = Point.location(p.readShort(), p.readShort());
			int id = p.readShort();
			final ActiveTile tile = world.getTile(location);
			if (tile == null)
				return;
			final InvItem myItem = player.getInventory().get(p.readShort());
			if (myItem == null)
				return;
			if (tile.hasGameObject() && myItem.getID() != 135) {
				player.getActionSender().sendMessage(
						"You cannot do that here, please move to a new area.");
				return;
			}
			final Item item = getItem(id, tile, player);

			if (item == null || myItem == null) {
				player.setSuspiciousPlayer(true);
				player.resetPath();
				return;
			}
			world.addEntryToSnapshots(new Activity(player.getUsername(), player
					.getUsername()
					+ " used item "
					+ myItem.getDef().getName()
					+ "("
					+ myItem.getID()
					+ ")"
					+ " [CMD: "
					+ myItem.getDef().getCommand()
					+ "] ON A GROUND ITEM "
					+ myItem.getDef().getName()
					+ "("
					+ myItem.getID()
					+ ")"
					+ " [CMD: "
					+ myItem.getDef().getCommand()
					+ "] at: "
					+ player.getX() + "/" + player.getY()));

			player.setStatus(Action.USING_INVITEM_ON_GITEM);
			Instance.getDelayedEventHandler().add(
					new WalkToPointEvent(player, location, 1, false) {
						public void arrived() {
							if (owner.isBusy()
									|| owner.isRanging()
									|| !tile.hasItem(item)
									|| !owner.nextTo(item)
									|| owner.getStatus() != Action.USING_INVITEM_ON_GITEM) {
								return;
							}
							if (myItem == null || item == null)
								return;
							switch (item.getID()) {
							case 23:
								if (myItem.getID() == 135) {
									if (owner.getInventory().remove(myItem) < 0)
										return;
									owner.getActionSender().sendMessage(
											"You put the flour in the pot.");
									Bubble bubble = new Bubble(owner, 135);
									for (Player p : owner.getViewArea()
											.getPlayersInView()) {
										p.informOfBubble(bubble);
									}
									world.unregisterItem(item);
									owner.getInventory().add(new InvItem(136));
									owner.getActionSender().sendInventory();
									return;
								}
							case 14:
							case 632:
							case 633:
							case 634:
							case 635:
							case 636:
								handleFireMaking();
								break;
							default:
								owner.getActionSender().sendMessage(
										"Nothing interesting happens.");
								return;
							}
						}

						private void handleFireMaking() {
							handleFireMaking((int) Math.ceil(owner
									.getMaxStat(11) / 10));

						}

						private void handleFireMaking(int tries) {
							final int retries = --tries;
							final FiremakingDef def = EntityHandler
									.getFiremakingDef(item.getID());
							if (!itemId(new int[] { 166 }) || def == null) {
								owner.getActionSender().sendMessage(
										"Nothing interesting happens.");
								return;
							}
							if (owner.getCurStat(11) < def.getRequiredLevel()) {
								owner.getActionSender()
										.sendMessage(
												"You need at least "
														+ def.getRequiredLevel()
														+ " firemaking to light these logs.");
								return;
							}
							owner.setBusy(true);
							Bubble bubble = new Bubble(owner, 166);
							for (Player p : owner.getViewArea()
									.getPlayersInView()) {
								p.informOfBubble(bubble);
							}
							owner.getActionSender().sendMessage(
									"You attempt to light the logs...");
							Instance.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											if (Formulae.lightLogs(def,
													owner.getCurStat(11))) {
												owner.getActionSender()
														.sendMessage(
																"They catch fire and start to burn.");
												world.unregisterItem(item);
												final GameObject fire = new GameObject(
														item.getLocation(), 97,
														0, 0);
												world.registerGameObject(fire);
												world.getDelayedEventHandler()
														.add(new DelayedEvent(
																null,
																def.getLength()) {
															public void run() {
																if (tile.hasGameObject()
																		&& tile.getGameObject()
																				.equals(fire)) {
																	world.unregisterGameObject(fire);
																	world.registerItem(new Item(
																			181,
																			tile.getX(),
																			tile.getY(),
																			1,
																			null));
																}
																matchRunning = false;
															}
														});
												owner.incExp(
														11,
														Formulae.firemakingExp(
																owner.getMaxStat(11),
																def.getExp()),
														true);
												owner.getActionSender()
														.sendStat(11);
												owner.setBusy(false);
											} else {
												owner.getActionSender()
														.sendMessage(
																"You fail to light them.");
												owner.setBusy(false);
												if (retries > 0) {
													handleFireMaking(retries);
												}
											}

										}
									});
						}

						private boolean itemId(int[] ids) {
							return DataConversions.inArray(ids, myItem.getID());
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
