package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.PlayerAppearance;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.container.Inventory;
import org.moparscape.msc.gs.model.definition.skill.ItemWieldableDef;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.service.ItemAttributes;

public class PlayerAppearanceUpdater implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (!player.isChangingAppearance()) {
			player.setSuspiciousPlayer(true);
			return;
		}
		player.setChangingAppearance(false);

		byte headGender = p.readByte();
		byte headType = p.readByte();
		byte bodyGender = p.readByte();

		p.readByte(); // wtf is this?

		int hairColour = (int) p.readByte();
		int topColour = (int) p.readByte();
		int trouserColour = (int) p.readByte();
		int skinColour = (int) p.readByte();

		int headSprite = headType + 1;
		int bodySprite = bodyGender + 1;

		PlayerAppearance appearance = new PlayerAppearance(hairColour,
				topColour, trouserColour, skinColour, headSprite, bodySprite);
		if (!appearance.isValid()) {
			player.setSuspiciousPlayer(true);
			return;
		}

		player.setMale(headGender == 1);

		if (player.isMale()) {
			Inventory inv = player.getInventory();
			for (int slot = 0; slot < inv.size(); slot++) {
				InvItem i = inv.getSlot(slot);
				ItemWieldableDef def = ItemAttributes.getWieldable(i.id);
				if (ItemAttributes.isWieldable(i.id) && def.getWieldPos() == 1
						&& i.wielded && def.femaleOnly()) {
					inv.setWield(slot, false);
					player.updateWornItems(def.getWieldPos(), player
							.getPlayerAppearance().getSprite(def.getWieldPos()));
					player.getActionSender().sendUpdateItem(slot);
					break;
				}
			}
		}

		int[] oldWorn = player.getWornItems();
		int[] oldAppearance = player.getPlayerAppearance().getSprites();
		player.setAppearance(appearance);
		int[] newAppearance = player.getPlayerAppearance().getSprites();
		for (int i = 0; i < 12; i++) {
			if (oldWorn[i] == oldAppearance[i]) {
				player.updateWornItems(i, newAppearance[i]);
			}
		}
	}

}
