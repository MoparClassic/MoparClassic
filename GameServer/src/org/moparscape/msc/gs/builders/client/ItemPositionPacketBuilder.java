package org.moparscape.msc.gs.builders.client;

import java.util.Collection;

import org.moparscape.msc.gs.builders.RSCPacketBuilder;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.model.Item;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.tools.DataConversions;
import org.moparscape.msc.gs.util.StatefulEntityCollection;

public class ItemPositionPacketBuilder {
	private Player playerToUpdate;

	/**
	 * Creates a packet for the given player that can be used to notify the
	 * player of the positions of items.
	 * 
	 * @return A <code>RSCPacket</code> to update the given player's knowledge
	 *         of item positions.
	 */
	public RSCPacket getPacket() {
		StatefulEntityCollection<Item> watchedItems = playerToUpdate
				.getWatchedItems();
		if (watchedItems.changed()) {
			Collection<Item> newItems = watchedItems.getNewEntities();
			Collection<Item> knownItems = watchedItems.getKnownEntities();
			RSCPacketBuilder packet = new RSCPacketBuilder();
			packet.setID(109);

			for (Item i : knownItems) {
				// nextTo
				if (watchedItems.isRemoving(i)) {
					byte[] offsets = DataConversions.getObjectPositionOffsets(
							i.getLocation(), playerToUpdate.getLocation());
					// if(it's miles away) {
					// packet.addByte((byte)255);
					// packet.addByte((byte)sectionX);
					// packet.addByte((byte)sectionY);
					// }
					// else {
					packet.addShort(i.getID() + 32768);
					packet.addByte(offsets[0]);
					packet.addByte(offsets[1]);
					// }
				}
			}
			for (Item i : newItems) {
				byte[] offsets = DataConversions.getObjectPositionOffsets(
						i.getLocation(), playerToUpdate.getLocation());
				packet.addShort(i.getID());
				packet.addByte(offsets[0]);
				packet.addByte(offsets[1]);
			}
			return packet.toPacket();
		}
		return null;
	}

	/**
	 * Sets the player to update
	 */
	public void setPlayer(Player p) {
		playerToUpdate = p;
	}
}
