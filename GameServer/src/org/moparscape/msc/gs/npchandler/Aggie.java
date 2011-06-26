package org.moparscape.msc.gs.npchandler;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.event.MiniEvent;
import org.moparscape.msc.gs.event.ShortEvent;
import org.moparscape.msc.gs.external.EntityHandler;
import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.MenuHandler;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;

/**
 * 
 * @author xEnt
 * 
 */
public class Aggie implements NpcHandler {

	public static final World world = Instance.getWorld();

	int[] dyes = { 238, 239, 272 };

	int[] itemReq = { 236, 241, 281 };
	String[] names = { "Red dye please", "Yellow dye please",
			"Blue dye please", "No thanks" };

	int ourOption = -1;

	public void handleNpc(final Npc npc, Player player) throws Exception {
		player.setBusy(false);
		player.informOfNpcMessage(new ChatMessage(
				npc,
				"Hi traveller, i specialize in creating different colored dyes",
				player));
		Instance.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.informOfNpcMessage(new ChatMessage(npc,
						"Would you like me to create you any dyes?", owner));
				Instance.getDelayedEventHandler().add(new ShortEvent(owner) {
					public void action() {
						owner.setMenuHandler(new MenuHandler(names) {
							public void handleReply(final int option,
									final String reply) {
								if (option < 0 && option > names.length
										|| option == 3)
									return;
								ourOption = option;
								owner.informOfChatMessage(new ChatMessage(
										owner, reply, npc));
								Instance.getDelayedEventHandler().add(
										new ShortEvent(owner) {
											public void action() {
												owner.informOfNpcMessage(new ChatMessage(
														npc,
														"You will need 1 "
																+ EntityHandler
																		.getItemDef(itemReq[ourOption]).name
																+ " and 30gp for me to create this dye",
														owner));
												Instance.getDelayedEventHandler()
														.add(new ShortEvent(
																owner) {
															public void action() {
																String[] diag = {
																		"Yes i have them",
																		"Ill come back when i have the ingrediants" };
																owner.setMenuHandler(new MenuHandler(
																		diag) {
																	public void handleReply(
																			final int option,
																			final String reply) {
																		if (option == 0) {
																			owner.informOfChatMessage(new ChatMessage(
																					owner,
																					reply,
																					npc));
																			world.getDelayedEventHandler()
																					.add(new ShortEvent(
																							owner) {
																						public void action() {
																							if (owner
																									.getInventory()
																									.countId(
																											itemReq[ourOption]) < 1
																									|| owner.getInventory()
																											.countId(
																													10) < 30) {
																								owner.informOfNpcMessage(new ChatMessage(
																										npc,
																										"It seems like you don't have all what's Required, come back later.",
																										owner));
																								owner.setBusy(false);
																								npc.setBusy(false);
																								npc.unblock();
																								return;
																							}

																							owner.informOfNpcMessage(new ChatMessage(
																									npc,
																									"Here is your new Dye, enjoy.",
																									owner));
																							world.getDelayedEventHandler()
																									.add(new MiniEvent(
																											owner,
																											1000) {
																										public void action() {
																											if (owner
																													.getInventory()
																													.remove(itemReq[ourOption],
																															1) > -1
																													&& owner.getInventory()
																															.remove(10,
																																	30) > -1) {

																												owner.getInventory()
																														.add(new InvItem(
																																dyes[ourOption]));
																												owner.getActionSender()
																														.sendInventory();
																											}
																										}
																									});

																							owner.setBusy(false);
																							npc.setBusy(false);
																							npc.unblock();
																							return;
																						}
																					});

																		} else {
																			owner.setBusy(false);
																			npc.setBusy(false);
																			npc.unblock();
																			return;
																		}
																	}
																});
																owner.getActionSender()
																		.sendMenu(
																				diag);
															}
														});
											}
										});
							}
						});
						owner.getActionSender().sendMenu(names);
					}
				});
			}
		});
	}

}