package org.moparscape.msc.gs.npchandler;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.event.ShortEvent;
import org.moparscape.msc.gs.external.CerterDef;
import org.moparscape.msc.gs.external.EntityHandler;
import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.MenuHandler;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;

public class Certer implements NpcHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handleNpc(final Npc npc, Player player) throws Exception {
		final CerterDef certerDef = EntityHandler.getCerterDef(npc.getID());
		if (certerDef == null) {
			return;
		}
		final String[] names = certerDef.getCertNames();
		player.informOfNpcMessage(new ChatMessage(npc, "Welcome to my "
				+ certerDef.getType() + " exchange stall", player));
		player.setBusy(true);
		Instance.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.setBusy(false);
				String[] options = new String[] {
						"I have some certificates to trade in",
						"I have some " + certerDef.getType() + " to trade in" };
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
										owner.setBusy(false);
										switch (option) {
										case 0:
											owner.getActionSender()
													.sendMessage(
															"What sort of certificate do you wish to trade in?");
											owner.setMenuHandler(new MenuHandler(
													names) {
												public void handleReply(
														final int index,
														String reply) {
													owner.getActionSender()
															.sendMessage(
																	"How many certificates do you wish to trade in?");
													String[] options = new String[] {
															"One", "Two",
															"Three", "Four",
															"Five",
															"All to bank" };
													owner.setMenuHandler(new MenuHandler(
															options) {
														public void handleReply(
																int certAmount,
																String reply) {
															owner.resetPath();
															int certID = certerDef
																	.getCertID(index);
															if (certID < 0) { // This
																// shouldn't
																// happen
																return;
															}
															int itemID = certerDef
																	.getItemID(index);
															if (certAmount == 5) {
																certAmount = owner
																		.getInventory()
																		.countId(
																				certID);
																if (certAmount <= 0) {
																	owner.getActionSender()
																			.sendMessage(
																					"You don't have any "
																							+ names[index]
																							+ " certificates");
																	return;
																}
																// MIGHT
																// BE
																// SMART
																// TO
																// CHECK
																// THEIR
																// BANK
																// ISN'T
																// FULL
																InvItem bankItem = new InvItem(
																		itemID,
																		certAmount * 5);
																if (owner
																		.getInventory()
																		.remove(new InvItem(
																				certID,
																				certAmount)) > -1) {
																	owner.getActionSender()
																			.sendMessage(
																					"You exchange the certificates, "
																							+ bankItem
																									.getAmount()
																							+ " "
																							+ bankItem
																									.getDef()
																									.getName()
																							+ " is added to your bank");
																	owner.getBank()
																			.add(bankItem);
																}
															} else {
																certAmount += 1;
																int itemAmount = certAmount * 5;
																if (owner
																		.getInventory()
																		.countId(
																				certID) < certAmount) {
																	owner.getActionSender()
																			.sendMessage(
																					"You don't have that many certificates");
																	return;
																}
																if (owner
																		.getInventory()
																		.remove(certID,
																				certAmount) > -1) {
																	owner.getActionSender()
																			.sendMessage(
																					"You exchange the certificates for "
																							+ certerDef
																									.getType()
																							+ ".");
																	for (int x = 0; x < itemAmount; x++) {
																		owner.getInventory()
																				.add(new InvItem(
																						itemID,
																						1));
																	}
																}
															}
															owner.getActionSender()
																	.sendInventory();
														}
													});
													owner.getActionSender()
															.sendMenu(options);
												}
											});
											owner.getActionSender().sendMenu(
													names);
											break;
										case 1:
											owner.getActionSender()
													.sendMessage(
															"What sort of "
																	+ certerDef
																			.getType()
																	+ " do you wish to trade in?");
											owner.setMenuHandler(new MenuHandler(
													names) {
												public void handleReply(
														final int index,
														String reply) {
													owner.getActionSender()
															.sendMessage(
																	"How many "
																			+ certerDef
																					.getType()
																			+ " do you wish to trade in?");
													String[] options = new String[] {
															"Five", "Ten",
															"Fifteen",
															"Twenty",
															"Twentyfive",
															"All from bank" };
													owner.setMenuHandler(new MenuHandler(
															options) {
														public void handleReply(
																int certAmount,
																String reply) {
															owner.resetPath();
															int certID = certerDef
																	.getCertID(index);
															if (certID < 0) { // This
																// shouldn't
																// happen
																return;
															}
															int itemID = certerDef
																	.getItemID(index);
															if (certAmount == 5) {
																certAmount = (int) (owner
																		.getBank()
																		.countId(
																				itemID) / 5);
																int itemAmount = certAmount * 5;
																if (itemAmount <= 0) {
																	owner.getActionSender()
																			.sendMessage(
																					"You don't have any "
																							+ names[index]
																							+ " to cert");
																	return;
																}
																if (owner
																		.getBank()
																		.remove(itemID,
																				itemAmount) > -1) {
																	owner.getActionSender()
																			.sendMessage(
																					"You exchange the "
																							+ certerDef
																									.getType()
																							+ ", "
																							+ itemAmount
																							+ " "
																							+ EntityHandler
																									.getItemDef(
																											itemID)
																									.getName()
																							+ " is taken from your bank");
																	owner.getInventory()
																			.add(new InvItem(
																					certID,
																					certAmount));
																}
															} else {
																certAmount += 1;
																int itemAmount = certAmount * 5;
																if (owner
																		.getInventory()
																		.countId(
																				itemID) < itemAmount) {
																	owner.getActionSender()
																			.sendMessage(
																					"You don't have that many "
																							+ certerDef
																									.getType());
																	return;
																}
																owner.getActionSender()
																		.sendMessage(
																				"You exchange the "
																						+ certerDef
																								.getType()
																						+ " for certificates.");
																for (int x = 0; x < itemAmount; x++) {
																	owner.getInventory()
																			.remove(itemID,
																					1);
																}
																owner.getInventory()
																		.add(new InvItem(
																				certID,
																				certAmount));
															}
															owner.getActionSender()
																	.sendInventory();
														}
													});
													owner.getActionSender()
															.sendMenu(options);
												}
											});
											owner.getActionSender().sendMenu(
													names);
											break;
										}
										npc.unblock();
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