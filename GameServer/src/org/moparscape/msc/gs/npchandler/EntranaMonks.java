package org.moparscape.msc.gs.npchandler;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.event.ShortEvent;
import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.MenuHandler;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;

public class EntranaMonks implements NpcHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handleNpc(final Npc npc, Player player) throws Exception {
		final boolean toEntrana = !player.getLocation().inBounds(390, 530, 440,
				580);
		player.informOfNpcMessage(new ChatMessage(
				npc,
				toEntrana ? "Are you looking to take passage to our holy island?"
						: "Are you ready to go back to the mainland?", player));
		player.setBusy(true);
		Instance.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.setBusy(false);
				String[] options = { "Yes okay I'm ready to go", "No thanks" };
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						if (owner.isBusy()) {
							npc.unblock();
							return;
						}
						owner.informOfChatMessage(new ChatMessage(owner, reply,
								npc));
						owner.setBusy(true);
						Instance.getDelayedEventHandler().add(
								new ShortEvent(owner) {
									public void action() {
										if (option == 0) {
											owner.getActionSender()
													.sendMessage(
															"You board the ship");
											Instance.getDelayedEventHandler()
													.add(new ShortEvent(owner) {
														public void action() {
															if (toEntrana) {
																owner.teleport(
																		418,
																		570,
																		false);
															} else {
																owner.teleport(
																		263,
																		659,
																		false);
															}
															owner.getActionSender()
																	.sendMessage(
																			"The ship arrives at "
																					+ (toEntrana ? "Entrana"
																							: "Port Sarim"));
															owner.setBusy(false);
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
		return;

	}

}