import org.moparscape.msc.gs.event.SingleEvent;
import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.MenuHandler;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.quest.Quest;
import org.moparscape.msc.gs.quest.QuestAction;

/**
 * Quest: Black Knight's Fortress (v1.0) Status: INCOMPLETE Start: 304, 1507
 * Stage1: 269, 441 Items: - Rewards: -
 * 
 * @author youKnowWho
 */
public class BlackKnightFortress extends Quest {
	private static final int QUEST_POINTS = 1;
	private static final int NPC_VARZE = 110;
	private static final int WALL_ID = 22;
	private static final int WALL_X = 273;
	private static final int WALL_Y = 435;
	private static final int GRILL_ID = -1;
	private static final int GRILL_X = -1;
	private static final int GRILL_Y = -1;
	private static final int REQUIRED_QPOINTS = 0; // 13
	private static final String[] FIRST_MENU = new String[] {
			"Well what's the problem?", "Well, ah.. good luck with that" };
	private static final String[] SECOND_MENU = new String[] {
			"Secret weapon? That sounds really scary!",
			"I can take care of that" };
	private static final String[] THIRD_MENU = new String[] {
			"Sure, but it'll cost you", "No, actually, I lied." };
	private static final String[] FOURTH_MENU = new String[] {
			"I'll get right on it then", "Sorry, I can't be bothered" };

	/**
	 * Don't load this quest yet it's incomplete
	 */
	public boolean loadQuest() {
		return false;
	}

	public void init() {
		associateNpc(NPC_VARZE);
		associateObject(WALL_ID, WALL_X, WALL_Y);
	}

	public BlackKnightFortress() {
	}

	public String getName() {
		return "Black Knight's Fortress";
	}

	public int getUniqueID() {
		return 2;
	}

	public void handleAction(QuestAction action, Object[] args,
			final Player player) {
		int stage = player.getQuestStage(this);

		if (action == QuestAction.TALKED_NPC) {
			if (!(args[0] instanceof Npc))
				return;

			final Npc npc = (Npc) args[0];

			if (npc.getID() != NPC_VARZE)
				return;

			player.setBusy(true);
			npc.blockedBy(player);

			// handle quest complete chat?

			if (stage == -1)
				startQuest(player, npc);
			else
				handleTalk(player, npc);
		} else if (action == QuestAction.USED_OBJECT) {
			if (!(args[0] instanceof GameObject))
				return;

			final GameObject obj = (GameObject) args[0];

			if (obj.getID() != WALL_ID && obj.getID() != GRILL_ID)
				return;

			player.setBusy(true);

			if (obj.getID() == WALL_ID) {
				if (player.getY() < 435 || stage >= 1) {
					player.getActionSender().sendMessage(
							"You push on the wall...");
					player.getActionSender().sendSound("secretdoor");
					world.unregisterGameObject(obj);
					world.delayedSpawnObject(obj.getLoc(), 1000);

					if (player.getY() < 435)
						player.teleport(273, 435, false);
					else
						player.teleport(273, 434, false);

					addDelayedMessage("It slides out of the way!", player, 1000);
				} else
					player.getActionSender().sendMessage(
							"You see no reason to push on the wall");
			} else if (obj.getID() == GRILL_ID) {
				// listen
			}

			player.setBusy(false);
		}
	}

	private void handleTalk(final Player player, final Npc npc) {
		int stage = player.getQuestStage(this);

		if (stage >= 1 && stage <= 5) {
			sendChat("I don't have time to talk right now", npc, player);
			addDelayedChat("Come back when you've destroyed that weapon!", npc,
					player);
			player.setBusy(false);
			npc.unblock();
		}
	}

	private void startQuest(final Player player, final Npc npc) {
		sendChat("Good day to you, " + (player.isMale() ? "sir" : "miss"), npc,
				player);

		addSingleEvent(new SingleEvent(player, 2000) {
			public void action() {
				sendChat(
						"Hello sir. I've heard rumours that you're going to war with the black knights",
						player, npc);
				addSingleEvent(new SingleEvent(player, 4000) {
					public void action() {
						sendChat("Is this true?", player, npc);
						addSingleEvent(new SingleEvent(player, 2000) {
							public void action() {
								if (player.getQuestPoints() >= REQUIRED_QPOINTS) // Confirm
																					// rumour
								{
									sendChat("Who told you that?", npc, player);
									addSingleEvent(new SingleEvent(player, 2000) {
										public void action() {
											sendChat(
													"Oh, it makes no difference. Yes, the rumours are true.",
													npc, player);
											addSingleEvent(new SingleEvent(
													player, 3000) {
												public void action() {
													npc.blockedBy(player);
													player.setBusy(false);
													player.setMenuHandler(new MenuHandler(
															FIRST_MENU) {
														public void handleReply(
																final int option,
																final String reply) {
															npc.blockedBy(player);
															player.setBusy(true);
															sendChat(
																	reply
																			+ (option == 0 ? " Surely you'll defeat them easily?"
																					: ""),
																	player, npc);
															addSingleEvent(new SingleEvent(
																	player,
																	3000) {
																public void action() {
																	npc.blockedBy(player);
																	switch (option) {
																	case 0:
																		sendChat(
																				"Yeah, well.. You're lucky you've earned yourself a good reputation",
																				npc,
																				player);
																		addSingleEvent(new SingleEvent(
																				player,
																				4000) {
																			public void action() {
																				sendChat(
																						"Or I wouldn't tell you.",
																						npc,
																						player);
																				addSingleEvent(new SingleEvent(
																						player,
																						4000) {
																					public void action() {
																						sendChat(
																								"The black knights have a secret weapon, and they say it will destroy us.",
																								npc,
																								player);
																						npc.blockedBy(player);
																						addSingleEvent(new SingleEvent(
																								player,
																								4000) {
																							public void action() {
																								player.setBusy(false);
																								player.setMenuHandler(new MenuHandler(
																										SECOND_MENU) {
																									public void handleReply(
																											final int option,
																											final String reply) {
																										npc.blockedBy(player);
																										player.setBusy(true);

																										if (option == 1)
																											sendChat(
																													"Well it just so happens that I'm an expert in secret weapon thwarting.",
																													player,
																													npc);
																										else
																											sendChat(
																													reply,
																													player,
																													npc);

																										addSingleEvent(new SingleEvent(
																												player,
																												3000) {
																											public void action() {
																												npc.blockedBy(player);
																												switch (option) {
																												case 0:
																													sendChat(
																															"What kind of sissy talk is that? Get out of my sight!",
																															npc,
																															player);
																													player.setBusy(false);
																													npc.unblock();
																													break;
																												case 1:
																													sendChat(
																															"I like your spirit, young warrior!",
																															npc,
																															player);
																													addSingleEvent(new SingleEvent(
																															player,
																															3000) {
																														public void action() {
																															sendChat(
																																	"Do you think you could help us?",
																																	npc,
																																	player);
																															npc.blockedBy(player);
																															addSingleEvent(new SingleEvent(
																																	player,
																																	2000) {
																																public void action() {
																																	player.setBusy(false);
																																	player.setMenuHandler(new MenuHandler(
																																			THIRD_MENU) {
																																		public void handleReply(
																																				final int option,
																																				final String reply) {
																																			sendChat(
																																					reply,
																																					player,
																																					npc);
																																			npc.blockedBy(player);
																																			player.setBusy(true);
																																			addSingleEvent(new SingleEvent(
																																					player,
																																					3000) {
																																				public void action() {
																																					npc.blockedBy(player);

																																					switch (option) {
																																					case 0:
																																						sendChat(
																																								"Money won't be a problem, assuming you can do the job.",
																																								npc,
																																								player);
																																						addSingleEvent(new SingleEvent(
																																								player,
																																								3000) {
																																							public void action() {
																																								sendChat(
																																										"Well then, what do you want me to do?",
																																										player,
																																										npc);
																																								addSingleEvent(new SingleEvent(
																																										player,
																																										3000) {
																																									public void action() {
																																										sendChat(
																																												"First of all, you'll need to find out what the weapon is... then destroy it!",
																																												npc,
																																												player);
																																										addSingleEvent(new SingleEvent(
																																												player,
																																												4500) {
																																											public void action() {
																																												sendChat(
																																														"Head north and search the black knight's castle.",
																																														npc,
																																														player);
																																												npc.blockedBy(player);
																																												player.setBusy(false);
																																												player.setMenuHandler(new MenuHandler(
																																														FOURTH_MENU) {
																																													public void handleReply(
																																															final int option,
																																															final String reply) {
																																														sendChat(
																																																reply,
																																																player,
																																																npc);
																																														player.setBusy(true);
																																														npc.blockedBy(player);
																																														addSingleEvent(new SingleEvent(
																																																player,
																																																3000) {
																																															public void action() {
																																																npc.blockedBy(player);

																																																switch (option) {
																																																case 0:
																																																	player.setBusy(false);
																																																	npc.unblock();
																																																	addDelayedChat(
																																																			"Good luck, "
																																																					+ player.getUsername()
																																																					+ ".",
																																																			npc,
																																																			player);
																																																	player.setQuestStage(
																																																			getUniqueID(),
																																																			1);
																																																	break;
																																																case 1:
																																																	addDelayedChat(
																																																			"Don't waste my time! Every valuable second, doom draws nearer!",
																																																			npc,
																																																			player);
																																																default:
																																																	player.setBusy(false);
																																																	npc.unblock();
																																																	break;
																																																}
																																															}
																																														});
																																													}
																																												});
																																												player.getActionSender()
																																														.sendMenu(
																																																FOURTH_MENU);
																																												player.setBusy(false);
																																											}
																																										});
																																									}
																																								});
																																							}
																																						});
																																						break;
																																					case 1:
																																						addDelayedChat(
																																								"Don't waste my time "
																																										+ player.getUsername()
																																										+ "!",
																																								npc,
																																								player);
																																					default:
																																						player.setBusy(false);
																																						npc.unblock();
																																						break;
																																					}
																																				}
																																			});
																																		}
																																	});
																																	player.getActionSender()
																																			.sendMenu(
																																					THIRD_MENU);
																																	player.setBusy(false);
																																}
																															});
																														}
																													});
																													break;
																												default:
																													player.setBusy(false);
																													npc.unblock();
																													break;
																												}
																											}
																										});
																									}
																								});
																								player.getActionSender()
																										.sendMenu(
																												SECOND_MENU);
																								player.setBusy(false);
																							}
																						});
																					}
																				});
																			}
																		});
																		break;
																	case 1:
																		sendChat(
																				"Um, thanks. I guess...",
																				npc,
																				player);
																	default:
																		player.setBusy(false);
																		npc.unblock();
																		break;
																	}
																}
															});
														}
													});
													player.getActionSender()
															.sendMenu(
																	FIRST_MENU);
													player.setBusy(false);
												}
											});
										}
									});
								} else // Deny rumour
								{
									sendChat(
											"Most certainly not. The Black Knights are no threat to us!",
											npc, player);
									player.setBusy(false);
									npc.unblock();
									addDelayedMessage("You need "
											+ REQUIRED_QPOINTS
											+ " quest points before "
											+ npc.getDef().getName()
											+ " will trust you", player);
								}
							}
						});
					}
				});
			}
		});
	}
}
