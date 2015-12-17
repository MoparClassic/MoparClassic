package org.moparscape.msc.gs.builders.ls;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;

import org.moparscape.msc.gs.connection.LSPacket;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.PlayerAppearance;
import org.moparscape.msc.gs.model.Property;
import org.moparscape.msc.gs.model.container.Bank;
import org.moparscape.msc.gs.model.container.Inventory;
import org.moparscape.msc.gs.quest.Quest;
import org.moparscape.msc.gs.tools.DataConversions;
import org.moparscape.msc.gs.util.annotation.Transient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SavePacketBuilder {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting()
			.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
			.generateNonExecutableJson().create();
	/**
	 * Player to save
	 */
	private Player player;

	public LSPacket getPacket() {

		LSPacketBuilder packet = new LSPacketBuilder();
		packet.setID(20);
		packet.addLong(player.getUsernameHash());
		packet.addInt(player.getOwner());

		packet.addLong(player.getLastLogin() == 0L
				&& player.isChangingAppearance() ? 0 : player.getCurrentLogin());
		packet.addLong(DataConversions.IPToLong(player.getCurrentIP()));
		packet.addShort(player.getCombatLevel());
		packet.addShort(player.getSkillTotal());
		packet.addShort(player.getX());
		packet.addShort(player.getY());
		packet.addShort(player.getFatigue());

		PlayerAppearance a = player.getPlayerAppearance();
		packet.addByte((byte) a.getHairColour());
		packet.addByte((byte) a.getTopColour());
		packet.addByte((byte) a.getTrouserColour());
		packet.addByte((byte) a.getSkinColour());
		packet.addByte((byte) a.getSprite(0));
		packet.addByte((byte) a.getSprite(1));

		packet.addByte((byte) (player.isMale() ? 1 : 0));
		packet.addLong(player.getSkullTime());
		packet.addByte((byte) player.getCombatStyle());

		for (int i = 0; i < 18; i++) {
			packet.addLong(player.getExp(i));
			packet.addShort(player.getCurStat(i));
		}

		Inventory inv = player.getInventory();
		packet.addShort(inv.size());
		for (InvItem i : inv.getItems()) {
			packet.addShort(i.id);
			packet.addInt(i.amount);
			packet.addByte((byte) (i.wielded ? 1 : 0));
		}

		Bank bnk = player.getBank();
		packet.addShort(bnk.size());
		for (InvItem i : bnk.getItems()) {
			packet.addShort(i.id);
			packet.addInt(i.amount);
		}

		packet.addShort(player.quests.quests().size());
		for (Quest q : player.quests.quests()) {
			packet.addShort(q.id());
			packet.addShort(q.stage());
		}

		Map<String, Property<?>> properties = player.getProperties();
		packet.addShort(properties.size());
		for(Entry<String, Property<?>> e : properties.entrySet()) {
			if(e.getValue().value.getClass().isAnnotationPresent(Transient.class)) {
				continue;
			}
			packet.addInt(e.getKey().length());
			packet.addBytes(e.getKey().getBytes());
			
			String s = gson.toJson(e.getValue());
			packet.addInt(s.length());
			packet.addBytes(s.getBytes());
		}

		return packet.toPacket();
	}

	/**
	 * Sets the player to save
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
}
