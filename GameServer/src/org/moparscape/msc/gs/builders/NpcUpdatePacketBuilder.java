package org.moparscape.msc.gs.builders;

import java.util.ConcurrentModificationException;
import java.util.List;

import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;

public class NpcUpdatePacketBuilder {
	private Player playerToUpdate;

	public RSCPacket getPacket() {
		List<Npc> npcsNeedingHitsUpdate = playerToUpdate
				.getNpcsRequiringHitsUpdate();
		List<ChatMessage> npcMessagesNeedingDisplayed = playerToUpdate
				.getNpcMessagesNeedingDisplayed();

		int updateSize = npcMessagesNeedingDisplayed.size()
				+ npcsNeedingHitsUpdate.size();
		if (updateSize > 0) {
			try {
				RSCPacketBuilder updates = new RSCPacketBuilder();
				updates.setID(190);
				updates.addShort(updateSize);
				for (ChatMessage cm : npcMessagesNeedingDisplayed) {
					updates.addShort(cm.getSender().getIndex());
					updates.addByte((byte) 1);
					updates.addShort(cm.getRecipient().getIndex());
					updates.addByte((byte) cm.getLength());
					updates.addBytes(cm.getMessage());
					// show text above heads of players around
					if (cm.getRecipient() != null
							&& cm.getRecipient() instanceof Player) {
						RSCPacketBuilder updatez = new RSCPacketBuilder();
						updatez.setID(190);
						updatez.addShort(1);
						updatez.addShort(cm.getSender().getIndex());
						updatez.addByte((byte) 1);
						updatez.addShort(-1);
						updatez.addByte((byte) cm.getLength());
						updatez.addBytes(cm.getMessage());
						for (Player pl : playerToUpdate.getViewArea()
								.getPlayersInView()) {
							if (pl.equals(playerToUpdate)
									|| pl == playerToUpdate)
								continue;
							pl.getActionSender().addPacket(updatez.toPacket());
						}
					}
				}
				for (Npc n : npcsNeedingHitsUpdate) {
					updates.addShort(n.getIndex());
					updates.addByte((byte) 2);
					updates.addByte((byte) n.getLastDamage());
					updates.addByte((byte) n.getHits());
					updates.addByte((byte) n.getDef().getHits());
				}
				return updates.toPacket();
			} catch (ConcurrentModificationException e) {
				return null;
				/**
				 * Should fix the GS packet queue from piling up
				 */
			}
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
