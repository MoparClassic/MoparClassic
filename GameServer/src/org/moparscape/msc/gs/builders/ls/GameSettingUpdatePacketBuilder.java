package org.moparscape.msc.gs.builders.ls;

import org.moparscape.msc.gs.builders.LSPacketBuilder;
import org.moparscape.msc.gs.connection.LSPacket;
import org.moparscape.msc.gs.model.Player;

public class GameSettingUpdatePacketBuilder {
	/**
	 * The settings index
	 */
	private int index;
	/**
	 * Has the setting been turned on or off?
	 */
	private boolean on;
	/**
	 * Player to update
	 */
	private Player player;

	public LSPacket getPacket() {
		LSPacketBuilder packet = new LSPacketBuilder();
		packet.setID(7);
		packet.addLong(player.getUsernameHash());
		packet.addByte((byte) (on ? 1 : 0));
		packet.addByte((byte) index);
		return packet.toPacket();
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	/**
	 * Sets the player to update
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

}
