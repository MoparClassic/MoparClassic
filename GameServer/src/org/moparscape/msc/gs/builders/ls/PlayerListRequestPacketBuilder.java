package org.moparscape.msc.gs.builders.ls;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.builders.LSPacketBuilder;
import org.moparscape.msc.gs.connection.LSPacket;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.util.EntityList;

public class PlayerListRequestPacketBuilder {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();
	/**
	 * Packets uID
	 */
	private long uID;

	public LSPacket getPacket() {
		EntityList<Player> players = world.getPlayers();

		LSPacketBuilder packet = new LSPacketBuilder();
		packet.setUID(uID);
		packet.addInt(players.size());
		for (Player p : players) {
			packet.addLong(p.getUsernameHash());
			packet.addShort(p.getX());
			packet.addShort(p.getY());
		}
		return packet.toPacket();
	}

	/**
	 * Sets the packet to reply to
	 */
	public void setUID(long uID) {
		this.uID = uID;
	}
}
