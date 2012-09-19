package org.moparscape.msc.gs.npchandler;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.event.DelayedEvent;
import org.moparscape.msc.gs.event.ShortEvent;
import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.MenuHandler;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;

public class Tanner implements NpcHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handleNpc(final Npc npc, Player player) throws Exception {
		player.informOfNpcMessage(new ChatMessage(npc,
				"Greeting friend i'm a manufacturer of leather", player));
		player.setBusy(true);
		Instance.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.setBusy(false);
				String[] options = new String[] {
						"Can I buy some leather then?",
						"Here's some cow hides, can I buy some leather now?",
						"Leather is rather weak stuff" };
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						if (owner.isBusy()) {
							return;
						}
						owner.informOfChatMessage(new ChatMessage(owner, reply,
								npc));
						owner.setBusy(true);
						Instance.getDelayedEventHandler().add(
								new ShortEvent(owner) {
									public void action() {
										switch (option) {
										case 0:
											owner.informOfNpcMessage(new ChatMessage(
													npc,
													"I make leather from cow hides",
													owner));
											Instance.getDelayedEventHandler()
													.add(new ShortEvent(owner) {
														public void action() {
															owner.setBusy(false);
															owner.informOfNpcMessage(new ChatMessage(
																	npc,
																	"Bring me some of them and a gold coin per hide",
																	owner));
															npc.unblock();
														}
													});
											break;
										case 1:
											owner.informOfNpcMessage(new ChatMessage(
													npc, "Ok", owner));
											world.getDelayedEventHandler()
													.add(new DelayedEvent(
															owner, 500) {
														public void run() {
															InvItem hides = owner
																	.getInventory()
																	.get(owner
																			.getInventory()
																			.getLastIndexById(
																					147));
															if (hides == null) {
																owner.getActionSender()
																		.sendMessage(
																				"You have run out of cow hides");
																matchRunning = false;
																owner.setBusy(false);
															} else if (owner
																	.getInventory()
																	.countId(10) < 1) {
																owner.getActionSender()
																		.sendMessage(
																				"You have run out of coins");
																matchRunning = false;
																owner.setBusy(false);
															} else if (owner
																	.getInventory()
																	.remove(hides) > -1
																	&& owner.getInventory()
																			.remove(10,
																					1) > -1) {
																owner.getInventory()
																		.add(new InvItem(
																				148,
																				1));
																owner.getActionSender()
																		.sendInventory();
																this.stop();
															} else {
																matchRunning = false;
																owner.setBusy(false);
															}
														}
													});
											npc.unblock();
											break;
										case 2:
											owner.setBusy(false);
											owner.informOfNpcMessage(new ChatMessage(
													npc,
													"Well yes if all you're concerned with is how much it will protect you in a fight",
													owner));
											npc.unblock();
											break;
										default:
											owner.setBusy(false);
											npc.unblock();
											break;
										}

									}
								});
					}
				});
				owner.getActionSender().sendMenu(options);
			}
		});
		npc.blockedBy(player);
	}

}