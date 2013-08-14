package org.moparscape.msc.gs.phandler.ls;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.builders.RSCPacketBuilder;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.config.Formulae;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.PlayerAppearance;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.container.Bank;
import org.moparscape.msc.gs.model.container.Inventory;
import org.moparscape.msc.gs.model.definition.skill.ItemWieldableDef;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.phandler.client.WieldHandler;
import org.moparscape.msc.gs.service.ItemAttributes;
import org.moparscape.msc.gs.tools.DataConversions;

public class PlayerLogin implements PacketHandler {
	/**
	 * World instance
	 */
	private static final World world = Instance.getWorld();

	/**
	 * The player to update
	 */
	private Player player;

	public PlayerLogin(Player player) {
		this.player = player;
	}

	public void handlePacket(org.moparscape.msc.gs.connection.Packet p,
			IoSession session) throws Exception {
		byte loginCode = p.readByte();
		if (world.getPlayer(player.getUsernameHash()) != null) {
			Exception e = new Exception("Double log from "
					+ player.getUsername() + " | " + player.getUsernameHash());
			e.printStackTrace();
			loginCode = 5;
			player.destroy(true);
			player.getSession().close();
			return;
		}
		RSCPacketBuilder pb = new RSCPacketBuilder();
		pb.setBare(true);
		pb.addByte(loginCode);
		player.getSession().write(pb.toPacket());
		if (loginCode == 0 || loginCode == 1 || loginCode == 99) {
			player.setOwner(p.readInt());
			int gid = p.readInt();
			player.setGroupID(gid);

			player.setSubscriptionExpires(p.readLong());

			player.setLastIP(DataConversions.IPToString(p.readLong()));
			player.setLastLogin(p.readLong());
			/**
			 * Check if account is a new account
			 */
			if (player.getLastLogin() == 0L) {
				player.setLocation(Point.location(121, 647), true);
				@SuppressWarnings("unused")
				int x = p.readShort();
				@SuppressWarnings("unused")
				int y = p.readShort();
			} else {
				player.setLocation(
						Point.location(p.readShort(), p.readShort()), true);
			}
			player.setFatigue(p.readShort());
			player.setCombatStyle((int) p.readByte());

			player.setPrivacySetting(0, p.readByte() == 1);
			player.setPrivacySetting(1, p.readByte() == 1);
			player.setPrivacySetting(2, p.readByte() == 1);
			player.setPrivacySetting(3, p.readByte() == 1);

			player.setGameSetting(0, p.readByte() == 1);
			player.setGameSetting(2, p.readByte() == 1);
			player.setGameSetting(3, p.readByte() == 1);
			player.setGameSetting(4, p.readByte() == 1);
			player.setGameSetting(5, p.readByte() == 1);
			player.setGameSetting(6, p.readByte() == 1);

			PlayerAppearance appearance = new PlayerAppearance(p.readShort(),
					p.readShort(), p.readShort(), p.readShort(), p.readShort(),
					p.readShort());
			if (!appearance.isValid()) {
				loginCode = 7;
				player.destroy(true);
				player.getSession().close();
			}

			/*
			 * if(World.isMembers() && !player.isMod()) { loginCode = 7;
			 * player.destroy(true); player.getSession().close(); }
			 */
			player.setAppearance(appearance);
			player.setWornItems(player.getPlayerAppearance().getSprites());

			player.setMale(p.readByte() == 1);
			long skull = p.readLong();
			if (skull > 0)
				player.addSkull(skull);

			for (int i = 0; i < 18; i++) {
				int exp = (int) p.readLong();
				player.setExp(i, exp);
				player.setMaxStat(i, Formulae.experienceToLevel(exp));
				player.setCurStat(i, p.readShort());
			}

			player.setCombatLevel(Formulae.getCombatlevel(player.getMaxStats()));
			Inventory inventory = new Inventory(player);
			int invCount = p.readShort();
			for (int i = 0; i < invCount; i++) {
				InvItem item = new InvItem(p.readShort(), p.readInt());
				inventory.add(item.id, item.amount, false);
				if (p.readByte() == 1 && ItemAttributes.isWieldable(item.id)) {
					inventory.setWield(i, true);
					ItemWieldableDef def = ItemAttributes.getWieldable(item.id);
					player.updateWornItems(def.getWieldPos(), def.getSprite());
				}
			}
			player.setInventory(inventory);

			Bank bank = new Bank();
			int bnkCount = p.readShort();
			for (int i = 0; i < bnkCount; i++)
				bank.add(p.readShort(), p.readInt(), true);

			player.setBank(bank);

			int friendCount = p.readShort();
			for (int i = 0; i < friendCount; i++)
				player.addFriend(p.readLong(), p.readShort());

			int ignoreCount = p.readShort();
			for (int i = 0; i < ignoreCount; i++)
				player.addIgnore(p.readLong());

			int questCount = p.readShort();
			// Logging.debug(questCount);
			for (int i = 0; i < questCount; i++) {
				player.quests.set(p.readShort(), p.readShort());
			}
			/* Muted */

			player.setMuted(p.readLong());
			if (player.isMuted()) {
				player.getActionSender().sendMessage(
						"@red@You are muted for " + player.getDaysMuted()
								+ " days!");
			}

			long eventcd = p.readLong();
			player.setEventCD(eventcd);

			/* End of loading methods */

			/* Send client data */
			world.registerPlayer(player);

			player.updateViewedPlayers();
			player.updateViewedObjects();

			org.moparscape.msc.gs.builders.client.MiscPacketBuilder sender = player
					.getActionSender();
			sender.sendServerInfo();
			sender.sendFatigue();
			sender.sendWorldInfo(); // sends info for the client to load terrain
			sender.sendInventory();
			sender.sendEquipmentStats();
			sender.sendStats();
			sender.sendPrivacySettings();
			sender.sendGameSettings();
			sender.sendFriendList();
			sender.sendIgnoreList();
			sender.sendCombatStyle();
			sender.sendQuestData();
			sender.sendQuestInfo();

			int slot = 0;
			for (InvItem i : player.getInventory().getItems()) {
				if (i.wielded && (i.id == 407 || i.id == 401)) {
					if (player.getCurStat(6) < 31) {
						player.getActionSender().sendMessage(
								"You must have at least 31 magic");
						WieldHandler.unWieldItem(player, i, true, slot);
						player.getActionSender().sendInventory();
					}
				}
				if (i.id == 1288 && i.wielded) {
					boolean found = false;
					for (int it = 0; it < 18; it++) {
						if (player.getMaxStat(it) == 99) {
							found = true;
							break;
						}
					}
					if (!found) {
						player.getActionSender()
								.sendMessage(
										"Sorry, you need any skill of level 99 to wield this cape of legends");
						WieldHandler.unWieldItem(player, i, true, slot);
					}
				}
				slot++;
			}
			if (player.getLocation().inWilderness())
				player.p2pWildy();

			int timeTillShutdown = Instance.getServer().timeTillShutdown();
			if (timeTillShutdown > -1)
				sender.startShutdown((int) (timeTillShutdown / 1000));

			if (player.getLastLogin() == 0L) {
				player.getInventory().add(4, 1, false);
				player.getInventory().add(70, 1, false);
				player.getInventory().add(376, 1, false);
				player.getInventory().add(156, 1, false);
				player.getInventory().add(87, 1, false);
				player.getInventory().add(1263, 1, false);
				player.getActionSender().sendInventory();
				player.setChangingAppearance(true);
				sender.sendAppearanceScreen();
			}

			player.getActionSender().sendWakeUp(false);
			sender.sendLoginBox();
			sender.sendOnlinePlayers();
			for (String m : Config.MOTD.split("\n"))
				sender.sendMessage(m);

			if (player.clientWarn()) {
				player.getActionSender()
						.sendAlert(
								"@red@Alert! @whi@You are using an old client, please download the new client from our website. This client WILL stop working @red@soon.",
								false);
				player.getActionSender()
						.sendMessage(
								"@red@Alert! @whi@You are using an old client, please download the new client from our website. This client WILL stop working on @red@soon.");
			}

			if (player.isAdmin() || player.isPMod()) {
				player.setnopk(true);
				player.setnonaggro(true);
			}

			player.setLoggedIn(true);
			player.setBusy(false);
		} else
			player.destroy(true);
	}
}
