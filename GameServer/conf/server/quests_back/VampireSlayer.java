import msc.gs.Instance;  import msc.gs.event.SingleEvent;
import msc.gs.Instance;  import msc.gs.model.MenuHandler;
import msc.gs.Instance;  import msc.gs.model.Npc;
import msc.gs.Instance;  import msc.gs.model.Player;
import msc.gs.Instance;  import msc.gs.quest.Quest;
import msc.gs.Instance;  import msc.gs.quest.QuestAction;

/**
 * Quest: Vampire Slayer (v1.0) 6/1/2009 Status: INCOMPLETE Start: Morgan (id
 * 97), 215,615 NPCs: Harlow (id 98), 82,444 Items: Reward: 3 quest points, 1000
 * attack xp
 * 
 * @author punKrockeR
 */
public class VampireSlayer extends Quest {
	private static final int MORGAN_ID = 97;
	private static final int HARLOW_ID = 98;
	private static final int REWARD_XP = 1000;
	private static final int DEFAULT_EVENT_DELAY = 3200;
	private static final int QUEST_POINTS = 3;
	private static final String[] FIRST_MENU = new String[] {
			"No. Vampires are scary", "Ok I'm up for an adventure",
			"I tried fighting him. He wouldn't die" };
	private static final String[] SECOND_MENU = new String[] {
			"No, you've had enough", "Ok mate", "Morgan needs your help" };

	/**
	 * Don't load this quest yet it's incomplete
	 */
	public boolean loadQuest() {
		return false;
	}

	/**
	 * @return the quest's name
	 */
	public String getName() {
		return "Vampire Slayer";
	}

	/**
	 * @return this quest's unique id
	 */
	public int getUniqueID() {
		return 4;
	}

	/**
	 * Initialises the quest
	 */
	public void init() {
		associateNpc(MORGAN_ID);
		associateNpc(HARLOW_ID);
	}

	/**
	 * Handles the given quest action
	 */
	public void handleAction(QuestAction action, Object[] args,
			final Player player) {
		int stage = player.getQuestStage(this);

		if (action == action.TALKED_NPC) {
			if (!(args[0] instanceof Npc))
				return;

			final Npc npc = (Npc) args[0];

			player.setBusy(true);
			npc.blockedBy(player);

			if (npc.getID() == MORGAN_ID) {
				if (stage == -1)
					startQuest(player, npc);
				else
					handleMorganTalk(player, npc);
			} else if (npc.getID() == HARLOW_ID) {
				handleHarlowTalk(player, npc);
			}
		}
	}

	/**
	 * Handles npc chat if the quest hasn't been started yet
	 */
	private void startQuest(final Player player, final Npc npc) {
		sendChat("Please, please help us bold hero!", npc, player);
		addSingleEvent(new SingleEvent(player, DEFAULT_EVENT_DELAY) {
			public void action() {
				sendChat("What's the problem?", player, npc);
				addSingleEvent(new SingleEvent(player, DEFAULT_EVENT_DELAY) {
					public void action() {
						sendChat(
								"Our little village has been dreadfully ravaged by an evil vampire!",
								npc, player);
						addSingleEvent(new SingleEvent(player,
								DEFAULT_EVENT_DELAY) {
							public void action() {
								sendChat("There's hardly any of us left", npc,
										player);
								addSingleEvent(new SingleEvent(player,
										DEFAULT_EVENT_DELAY) {
									public void action() {
										sendChat(
												"We need someone to get rid of him once and for good",
												npc, player);
										addSingleEvent(new SingleEvent(player,
												DEFAULT_EVENT_DELAY) {
											public void action() {
												player.setBusy(false);
												player
														.setMenuHandler(new MenuHandler(
																FIRST_MENU) {
															public void handleReply(
																	final int option,
																	final String reply) {
																player
																		.setBusy(true);
																npc
																		.blockedBy(player);
																sendChat(reply,
																		player,
																		npc);

																addSingleEvent(new SingleEvent(
																		player,
																		DEFAULT_EVENT_DELAY) {
																	public void action() {
																		if (option == 0) // No
																		{
																			sendChat(
																					"I don't blame you",
																					npc,
																					player);
																			player
																					.setBusy(false);
																			npc
																					.unblock();
																		} else // Yes
																		{
																			npc
																					.blockedBy(player);
																			sendChat(
																					"I think first you should seek help",
																					npc,
																					player);
																			addSingleEvent(new SingleEvent(
																					player,
																					DEFAULT_EVENT_DELAY) {
																				public void action() {
																					npc
																							.blockedBy(player);
																					sendChat(
																							"I have a friend who is a retired vampire hunter called Dr Harlow",
																							npc,
																							player);
																					addSingleEvent(new SingleEvent(
																							player,
																							DEFAULT_EVENT_DELAY) {
																						public void action() {
																							npc
																									.blockedBy(player);
																							sendChat(
																									"He may be able to give you some tips",
																									npc,
																									player);
																							addSingleEvent(new SingleEvent(
																									player,
																									DEFAULT_EVENT_DELAY) {
																								public void action() {
																									npc
																											.blockedBy(player);
																									sendChat(
																											"He's usually found in the Jolly Bar Inn these days",
																											npc,
																											player);
																									addSingleEvent(new SingleEvent(
																											player,
																											DEFAULT_EVENT_DELAY) {
																										public void action() {
																											npc
																													.blockedBy(player);
																											sendChat(
																													"He's a bit of an old soak",
																													npc,
																													player);
																											addSingleEvent(new SingleEvent(
																													player,
																													DEFAULT_EVENT_DELAY) {
																												public void action() {
																													npc
																															.blockedBy(player);
																													sendChat(
																															"Mention his old friend Morgan",
																															npc,
																															player);
																													addSingleEvent(new SingleEvent(
																															player,
																															DEFAULT_EVENT_DELAY) {
																														public void action() {
																															npc
																																	.blockedBy(player);
																															sendChat(
																																	"I'm sure he wouldn't want me to be killed by a vampire",
																																	npc,
																																	player);
																															addSingleEvent(new SingleEvent(
																																	player,
																																	DEFAULT_EVENT_DELAY) {
																																public void action() {
																																	sendChat(
																																			"I'll look him up then",
																																			player,
																																			npc);
																																	addSingleEvent(new SingleEvent(
																																			player,
																																			DEFAULT_EVENT_DELAY) {
																																		public void action() {
																																			player
																																					.setQuestStage(
																																							getUniqueID(),
																																							1); // Start
																																								// quest
																																			player
																																					.setBusy(false);
																																			npc
																																					.unblock();
																																		}
																																	});
																																}
																															});
																														}
																													});
																												}
																											});
																										}
																									});
																								}
																							});
																						}
																					});
																				}
																			});
																		}
																	}
																});
															}
														});
												player.getActionSender()
														.sendMenu(FIRST_MENU);
											}
										});
									}
								});
							}
						});
					}
				});
			}
		});
	}

	/**
	 * Handles Morgan's chat if the quest is started but not finished
	 */
	private void handleMorganTalk(final Player player, final Npc npc) {
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("How are you doing with your quest?", npc, player);
		addSingleEvent(new SingleEvent(player, DEFAULT_EVENT_DELAY) {
			public void action() {
				if (player.getQuestStage(getUniqueID()) == 1) {
					npc.blockedBy(player);
					sendChat("I'm working on it still", player, npc);
					addSingleEvent(new SingleEvent(player, DEFAULT_EVENT_DELAY) {
						public void action() {
							npc.blockedBy(player);
							sendChat("Please hurry", npc, player);
							addSingleEvent(new SingleEvent(player,
									DEFAULT_EVENT_DELAY) {
								public void action() {
									npc.blockedBy(player);
									sendChat(
											"Every day we live in fear of our lives",
											npc, player);
									addSingleEvent(new SingleEvent(player,
											DEFAULT_EVENT_DELAY) {
										public void action() {
											sendChat(
													"Afraid that we will be the vampire's next victim",
													npc, player);
											player.setBusy(false);
											npc.unblock();
										}
									});
								}
							});
						}
					});
				} else {
				}
			}
		});
	}

	/**
	 * Handles Harlow's chat if the quest is started but not finished
	 */
	private void handleHarlowTalk(final Player player, final Npc npc) {
		player.setBusy(true);
		npc.blockedBy(player);
		sendChat("Buy me a drrink pleassh", npc, player);
		player.setBusy(false);
		player.setMenuHandler(new MenuHandler(SECOND_MENU) {
			public void handleReply(final int option, final String reply) {
				player.setBusy(true);
				npc.blockedBy(player);
				sendChat(reply, player, npc);
				addSingleEvent(new SingleEvent(player, DEFAULT_EVENT_DELAY) {
					public void action() {
						player.setBusy(false);
						npc.unblock();

						if (option == 0) {
							sendChat("Fuck you", npc, player);
							player.setBusy(false);
							npc.unblock();
						}
					}
				});
			}
		});
		player.getActionSender().sendMenu(SECOND_MENU);
	}

	/**
	 * Finishes the quest
	 */
	private void finishQuest(final Player player, final Npc npc) {
	}

	/**
	 * Construct the quest (empty)
	 */
	public VampireSlayer() {
	}
}