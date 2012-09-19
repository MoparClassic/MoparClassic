package org.moparscape.msc.gs.npchandler;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.event.ShortEvent;
import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.MenuHandler;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;

public class MonkHealer implements NpcHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handleNpc(final Npc npc, Player player) throws Exception {
		player.informOfNpcMessage(new ChatMessage(npc, "Greetings traveller",
				player));
		player.setBusy(true);
		Instance.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.setBusy(false);
				String[] options = new String[] { "Can you heal me? I'm injured" };
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
										if (option == 0) {
											owner.informOfNpcMessage(new ChatMessage(
													npc, "Ok", owner));
											owner.getActionSender()
													.sendMessage(
															"The monk places his hands on your head");
											Instance.getDelayedEventHandler()
													.add(new ShortEvent(owner) {
														public void action() {
															owner.setBusy(false);
															owner.getActionSender()
																	.sendMessage(
																			"You feel a little better");
															int newHp = owner
																	.getCurStat(3) + 10;
															if (newHp > owner
																	.getMaxStat(3)) {
																newHp = owner
																		.getMaxStat(3);
															}
															owner.setCurStat(3,
																	newHp);
															owner.getActionSender()
																	.sendStat(3);
															npc.unblock();
														}
													});
										} else {
											owner.setBusy(false);
											npc.unblock();
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