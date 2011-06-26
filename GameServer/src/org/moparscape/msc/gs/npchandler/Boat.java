package org.moparscape.msc.gs.npchandler;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.event.ShortEvent;
import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.MenuHandler;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;

public class Boat implements NpcHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handleNpc(final Npc npc, Player player) throws Exception {

		player.informOfNpcMessage(new ChatMessage(npc,
				"G'day sailor, where would you like to go?", player));
		player.setBusy(true);
		final String[] temp;
		if (npc.getID() == 163) {
			temp = new String[] { "Port Sarim" };
		} else {
			temp = new String[] { "Karamja" };
		}
		Instance.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.setBusy(false);
				owner.setMenuHandler(new MenuHandler(temp) {
					public void handleReply(final int option, final String reply) {
						if (owner.isBusy() || option < 0
								|| option >= temp.length) {
							npc.unblock();
							return;
						}
						owner.informOfChatMessage(new ChatMessage(owner, reply
								+ " please", npc));
						owner.setBusy(true);
						Instance.getDelayedEventHandler().add(
								new ShortEvent(owner) {
									public void action() {
										owner.getActionSender().sendMessage(
												"You board the ship");
										Instance.getDelayedEventHandler().add(
												new ShortEvent(owner) {
													public void action() {
														int count = owner
																.getInventory()
																.countId(318);
														if (count > 0) {
															for (int i = 0; i < count; i++) {
																owner.getActionSender()
																		.sendMessage(
																				"The Officer confiscates your Karamaja rum");
																if (owner
																		.getInventory()
																		.remove(new InvItem(
																				318)) > -1)
																	continue;
																else
																	break;
															}
															owner.getActionSender()
																	.sendInventory();
														}
														if (npc.getID() == 163) {
															owner.teleport(269,
																	648, false);
														} else {
															owner.teleport(324,
																	713, false);
														}

														owner.getActionSender()
																.sendMessage(
																		"The ship arrives at "
																				+ reply);
														owner.setBusy(false);
														npc.unblock();
													}
												});
									}
								});
					}
				});
				owner.getActionSender().sendMenu(temp);
			}
		});
		npc.blockedBy(player);
	}
}