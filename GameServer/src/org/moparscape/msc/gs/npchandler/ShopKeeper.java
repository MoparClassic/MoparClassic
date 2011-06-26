package org.moparscape.msc.gs.npchandler;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.event.ShortEvent;
import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.MenuHandler;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.Shop;
import org.moparscape.msc.gs.model.World;

public class ShopKeeper implements NpcHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handleNpc(final Npc npc, Player player) throws Exception {
		final Shop shop = world.getShop(npc.getLocation());
		if (shop == null) {
			return;
		}
		try {

			if (shop.getGreeting() != null) {

				if (shop.getGreeting()
						.equals("Good day monsieur, Would you like ze nice freshly baked bread?")) {
					if (player.getNpcThief()[0] == true) {
						player.informOfNpcMessage(new ChatMessage(
								npc,
								org.moparscape.msc.gs.plugins.extras.Thieving.StealChats[org.moparscape.msc.gs.plugins.extras.Thieving
										.Rands(org.moparscape.msc.gs.plugins.extras.Thieving.StealChats.length)],
								player));
						return;
					}
				}
				if (player.getNpcThief()[3] == true) {
					if (shop.getGreeting().equals("Silver! Silver!")) {
						player.informOfNpcMessage(new ChatMessage(
								npc,
								org.moparscape.msc.gs.plugins.extras.Thieving.StealChats[org.moparscape.msc.gs.plugins.extras.Thieving
										.Rands(org.moparscape.msc.gs.plugins.extras.Thieving.StealChats.length)],
								player));
						return;
					}
				}
				if (player.getNpcThief()[4] == true) {
					if (shop.getGreeting()
							.equals("Get your exotic spices here, rare very valuable spices here")) {
						player.informOfNpcMessage(new ChatMessage(
								npc,
								org.moparscape.msc.gs.plugins.extras.Thieving.StealChats[org.moparscape.msc.gs.plugins.extras.Thieving
										.Rands(org.moparscape.msc.gs.plugins.extras.Thieving.StealChats.length)],
								player));
						return;
					}
				}
				if (player.getNpcThief()[5] == true) {
					if (shop.getGreeting().equals(
							"Here, look at my lovely gems")) {
						player.informOfNpcMessage(new ChatMessage(
								npc,
								org.moparscape.msc.gs.plugins.extras.Thieving.StealChats[org.moparscape.msc.gs.plugins.extras.Thieving
										.Rands(org.moparscape.msc.gs.plugins.extras.Thieving.StealChats.length)],
								player));
						return;
					}
				}
				player.informOfNpcMessage(new ChatMessage(npc, shop
						.getGreeting(), player));
			}
			player.setBusy(true);
			Instance.getDelayedEventHandler().add(new ShortEvent(player) {
				public void action() {
					owner.setBusy(false);
					owner.setMenuHandler(new MenuHandler(shop.getOptions()) {
						public void handleReply(final int option,
								final String reply) {
							if (owner.isBusy()) {
								return;
							}
							owner.informOfChatMessage(new ChatMessage(owner,
									reply, npc));
							owner.setBusy(true);
							Instance.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											owner.setBusy(false);
											if (option == 0) {
												owner.setAccessingShop(shop);
												owner.getActionSender()
														.showShop(shop);
											}
											npc.unblock();
										}
									});
						}
					});
					owner.getActionSender().sendMenu(shop.getOptions());
				}
			});
			npc.blockedBy(player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
