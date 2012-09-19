package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.event.ShortEvent;
import org.moparscape.msc.gs.event.WalkToMobEvent;
import org.moparscape.msc.gs.model.Bubble;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.states.Action;
import org.moparscape.msc.gs.tools.DataConversions;

public class InvUseOnPlayer implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		final Player affectedPlayer = world.getPlayer(p.readShort());
		final InvItem item = player.getInventory().get(p.readShort());
		if (affectedPlayer == null || item == null) { // This shouldn't happen
			return;
		}
		if (GameEngine.getTime() - affectedPlayer.getLastRun() < 2000) {
			player.resetPath();
			return;
		}
		world.addEntryToSnapshots(new Activity(player.getUsername(), player
				.getUsername()
				+ " used item on player "
				+ item.getDef().getName()
				+ "("
				+ item.getID()
				+ ")"
				+ " [CMD: "
				+ item.getDef().getCommand()
				+ "] ON A NPC "
				+ affectedPlayer.getUsername()
				+ " at: "
				+ player.getX()
				+ "/"
				+ player.getY()
				+ "|"
				+ affectedPlayer.getX()
				+ "/"
				+ affectedPlayer.getY()));

		player.setFollowing(affectedPlayer);
		player.setStatus(Action.USING_INVITEM_ON_PLAYER);
		Instance.getDelayedEventHandler().add(
				new WalkToMobEvent(player, affectedPlayer, 1) {
					public void arrived() {
						owner.resetPath();
						owner.resetFollowing();
						if (!owner.getInventory().contains(item)
								|| !owner.nextTo(affectedPlayer)
								|| owner.isBusy()
								|| owner.isRanging()
								|| owner.getStatus() != Action.USING_INVITEM_ON_PLAYER) {
							return;
						}
						owner.resetAll();
						switch (item.getID()) {
						case 575: // Christmas cracker
							owner.setBusy(true);
							affectedPlayer.setBusy(true);
							owner.resetPath();
							affectedPlayer.resetPath();
							Bubble crackerBubble = new Bubble(owner, 575);
							for (Player p : owner.getViewArea()
									.getPlayersInView()) {
								p.informOfBubble(crackerBubble);
							}
							owner.getActionSender().sendMessage(
									"You pull the cracker with "
											+ affectedPlayer.getUsername()
											+ "...");
							affectedPlayer
									.getActionSender()
									.sendMessage(
											owner.getUsername()
													+ " is pulling a cracker with you...");
							Instance.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											InvItem phat = new InvItem(
													DataConversions.random(576,
															581));
											if (DataConversions.random(0, 1) == 1) {
												owner.getActionSender()
														.sendMessage(
																"Out comes a "
																		+ phat.getDef()
																				.getName()
																		+ "!");
												affectedPlayer
														.getActionSender()
														.sendMessage(
																owner.getUsername()
																		+ " got the contents!");
												owner.getInventory().add(phat);
											} else {
												owner.getActionSender()
														.sendMessage(
																affectedPlayer
																		.getUsername()
																		+ " got the contents!");
												affectedPlayer
														.getActionSender()
														.sendMessage(
																"Out comes a "
																		+ phat.getDef()
																				.getName()
																		+ "!");
												affectedPlayer.getInventory()
														.add(phat);
											}
											owner.getInventory().remove(item);
											owner.setBusy(false);
											affectedPlayer.setBusy(false);
											owner.getActionSender()
													.sendInventory();
											affectedPlayer.getActionSender()
													.sendInventory();
										}
									});
							break;
						default:
							owner.getActionSender().sendMessage(
									"Nothing interesting happens.");
							break;
						}
					}
				});
	}

}