import org.rscdaemon.server.quest.*;
import org.rscdaemon.server.model.*;
import org.rscdaemon.server.event.*;

/**
 * Quest: Sheep Shearer (v1.0)
 * Status: COMPLETE
 * Start: Fred the farmer (id 77), 159, 619
 * Items: 207x20
 * Reward: 1 quest point, 60 gold, Crafting 350 xp
 *
 * @author Konijn
 */
public class SheepShearer extends Quest
{
	private final int FRED_ID = 77;
	private final int ITEM_WOOL = 207;
	private final int REWARD_XP = 350;
	private final int REWARD_GP = 330;
	private final String[] FIRST_MENU  = new String[]{"Sure, what do I need to do?", "No thanks, I'm good."};
	private final String[] SECOND_MENU = new String[]{"Sorry, I don't like the sound of that.", "I'd be happy to help."};
	
	public void init()
	{
		associateNpc(FRED_ID);
	}
	
	public SheepShearer()
	{
	}
	
	public String getName()
	{
		return "Sheep Shearer";
	}
	
	public int getUniqueID()
	{
		return 1;
	}
	
	public void handleAction(QuestAction action, Object[] args, final Player player)
	{
		int stage = player.getQuestStage(this);
		
		if(stage == -1) // Quest hasn't been started
		{
			if(action == action.TALKED_NPC)
			{
				if(!(args[0] instanceof Npc))
					return;
				
				final Npc npc = (Npc)args[0];
				
				if(npc.getID() != FRED_ID)
					return;
				
				player.setBusy(true);
				npc.blockedBy(player);
				
				sendChat("Hi there, traveller. Care to make some money?", npc, player);
				
				addSingleEvent(new SingleEvent(player, 2000)
				{
					public void action()
					{
						player.setBusy(false);
						player.setMenuHandler(new MenuHandler(FIRST_MENU)
						{
							public void handleReply(final int option, final String reply)
							{
								player.setBusy(true);
								switch(option)
								{
									case 1:
										player.setBusy(false);
										npc.unblock();
										sendChat("No thanks, I'm good.", player, npc);
										break;
									case 0:
										sendChat("Sure, what do I need to do?", player, npc);
										addSingleEvent(new SingleEvent(player, 2000)
										{
											public void action()
											{
												sendChat("If you collect 20 balls of wool for me, I'll pay you 500 coins.", npc, player);
												addSingleEvent(new SingleEvent(player, 2000)
												{
													public void action()
													{
														sendChat("Maybe I'll teach you a thing or two about crafting, too.", npc, player);
														addSingleEvent(new SingleEvent(player, 2000)
														{
															public void action()
															{
																sendChat("I'm afraid you'll have to find your own shears, but the sheep are outside.", npc, player);
																addSingleEvent(new SingleEvent(player, 2000)
																{
																	public void action()
																	{
																		player.setBusy(false);
																		player.setMenuHandler(new MenuHandler(SECOND_MENU)
																		{
																			public void handleReply(final int option, final String reply)
																			{
																				player.setBusy(true);
																				switch(option)
																				{
																					case 1:
																						sendChat("I'd be happy to help.", player, npc);
																						addSingleEvent(new SingleEvent(player, 2000)
																						{
																							public void action()
																							{
																								sendChat("Great! Come back and see me when you're done.", npc, player);
																								addSingleEvent(new SingleEvent(player, 2000)
																								{
																									public void action()
																									{
																										player.setQuestStage(getUniqueID(), 1);
																										player.setBusy(false);
																										npc.unblock();
																									}
																								});
																							}
																						});
																						break;
																					case 0:
																						sendChat("Sorry, I don't like the sound of that.", player, npc);
																						addSingleEvent(new SingleEvent(player, 2000)
																						{
																							public void action()
																							{
																								sendChat("Suit yourself. Come and see me if you change your mind.", npc, player);
																								player.setBusy(false);
																								npc.unblock();
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
																		owner.getActionSender().sendMenu(SECOND_MENU);
																	}
																});
															}
														});
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
						owner.getActionSender().sendMenu(FIRST_MENU);
					}
				});
			} else
				return;
		} else
		if(stage == 1)
		{
			if(action == action.TALKED_NPC)
			{
				if(!(args[0] instanceof Npc))
					return;
				
				final Npc npc = (Npc)args[0];
				
				if(npc.getID() != FRED_ID)
					return;
				
				player.setBusy(true);
				npc.blockedBy(player);
				sendChat("Ahh, you've returned! Do you have my wool?", npc, player);
				
				addSingleEvent(new SingleEvent(player, 2000)
				{
					public void action()
					{
						player.setBusy(false);
						player.setMenuHandler(new MenuHandler(new String[]{"I'm afraid not.", "Yes, I do."})
						{
							public void handleReply(final int option, final String reply)
							{
								player.setBusy(true);
								sendChat(reply, player, npc);
								
								if(option == 0)
								{
									addSingleEvent(new SingleEvent(player, 2000)
									{
										public void action()
										{
											sendChat("Well, come and see me when you do. The offer still stands", npc, player);
											player.setBusy(false);
											npc.unblock();
										}
									});
								} else
								if(option == 1)
								{
									addSingleEvent(new SingleEvent(player, 2000)
									{
										public void action()
										{
											//check items
											if(player.getInventory().hasItemId(ITEM_WOOL) && player.getInventory().countId(ITEM_WOOL) >= 20)
											{
												finishQuest(player, npc);
											} else
											{
												sendChat("Um, no you don't. Get back to me when you do. The reward still stands!", npc, player);	
												player.setBusy(false);
												npc.unblock();	
											}							
										}
									});
								}
							}
						});
						
						owner.getActionSender().sendMenu(new String[]{"I'm afraid not.", "Yes, I do."});
					}
				});
			}
		} else
		if (stage == 0) {
                        if(action == action.TALKED_NPC)
                        {
                                if(!(args[0] instanceof Npc))
                                        return;
                                
                                final Npc npc = (Npc)args[0];
                                
                                if(npc.getID() != FRED_ID)
                                        return;
                                
                                player.setBusy(true);
                                npc.blockedBy(player);
                                sendChat("Hello " + player.getUsername() + "!", npc, player);
				player.setBusy(false);
				npc.unblock();
			}
		}
			
	}
	
	private void finishQuest(final Player player, final Npc npc)
	{
		sendChat("Thank you very much! As promised, here's your reward.", npc, player);	
		
		addSingleEvent(new SingleEvent(player, 2000)
		{
							public void action()
							{
								player.incExp(12, REWARD_XP, false);
								player.getActionSender().sendStat(12);
								player.setQuestStage(getUniqueID(), Quest.COMPLETE);
								for(int i=0; i < 20; i++) 
									player.getInventory().remove(ITEM_WOOL, 1);
								player.getInventory().add(new InvItem(10, REWARD_GP));
								player.getActionSender().sendInventory();
								player.incQuestPoints(1);
								player.setBusy(false);
								npc.unblock();
							}
						});
	}
}

