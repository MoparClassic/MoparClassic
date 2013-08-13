package org.moparscape.msc.ls.packetbuilder.gameserver;

import org.moparscape.msc.ls.model.BankItem;
import org.moparscape.msc.ls.model.InvItem;
import org.moparscape.msc.ls.model.PlayerSave;
import org.moparscape.msc.ls.net.LSPacket;
import org.moparscape.msc.ls.packetbuilder.LSPacketBuilder;
import org.moparscape.msc.ls.service.FriendsListService;

public class PlayerLoginPacketBuilder {
	/**
	 * Players Login Code
	 */
	private byte loginCode;
	/**
	 * Players Saved Data
	 */
	private PlayerSave save;
	/**
	 * Packets uID
	 */
	private long uID;

	public LSPacket getPacket() {

		LSPacketBuilder packet = new LSPacketBuilder();
		packet.setUID(uID);
		packet.addByte(loginCode);
		if (save != null) {
			packet.addInt(save.getOwner());
			packet.addInt(save.getGroup());

			packet.addLong(save.getSubscriptionExpires());
			packet.addLong(save.getLastIP());
			packet.addLong(save.getLastLogin());

			packet.addShort(save.getX());
			packet.addShort(save.getY());
			packet.addShort(save.getFatigue());

			packet.addByte(save.getCombatStyle());
			packet.addByte((byte) (save.blockChat() ? 1 : 0));
			packet.addByte((byte) (save.blockPrivate() ? 1 : 0));
			packet.addByte((byte) (save.blockTrade() ? 1 : 0));
			packet.addByte((byte) (save.blockDuel() ? 1 : 0));
			packet.addByte((byte) (save.cameraAuto() ? 1 : 0));
			packet.addByte((byte) (save.oneMouse() ? 1 : 0));
			packet.addByte((byte) (save.soundOff() ? 1 : 0));
			packet.addByte((byte) (save.showRoof() ? 1 : 0));
			packet.addByte((byte) (save.autoScreenshot() ? 1 : 0));
			packet.addByte((byte) (save.combatWindow() ? 1 : 0));

			packet.addShort(save.getHairColour());
			packet.addShort(save.getTopColour());
			packet.addShort(save.getTrouserColour());
			packet.addShort(save.getSkinColour());
			packet.addShort(save.getHeadSprite());
			packet.addShort(save.getBodySprite());

			packet.addByte((byte) (save.isMale() ? 1 : 0));
			packet.addLong(save.getSkullTime());

			for (int i = 0; i < 18; i++) {
				packet.addLong(save.getExp(i));
				packet.addShort(save.getStat(i));
			}

			int invCount = save.getInvCount();
			packet.addShort(invCount);
			for (int i = 0; i < invCount; i++) {
				InvItem item = save.getInvItem(i);
				packet.addShort(item.getID());
				packet.addInt(item.getAmount());
				packet.addByte((byte) (item.isWielded() ? 1 : 0));
			}

			int bankCount = save.getBankCount();
			packet.addShort(bankCount);
			for (int i = 0; i < bankCount; i++) {
				BankItem item = save.getBankItem(i);
				packet.addShort(item.getID());
				packet.addInt(item.getAmount());
			}

			int friendCount = save.getFriendCount();
			packet.addShort(friendCount);
			for (int i = 0; i < friendCount; i++) {
				long friend = save.getFriend(i);
				packet.addLong(friend);
				packet.addShort(FriendsListService.getFriendWorld(
						save.getUser(), friend));
			}

			int ignoreCount = save.getIgnoreCount();
			packet.addShort(ignoreCount);
			for (int i = 0; i < ignoreCount; i++) {
				packet.addLong(save.getIgnore(i));
			}
			java.util.Set<Integer> keys = save.getQuestStages().keySet();
			packet.addShort(keys.size());

			for (int id : keys) {
				packet.addShort(id);
				packet.addShort(save.getQuestStage(id));
			}
			packet.addLong(save.getMuted());
			packet.addLong(save.getEventCD());
		}
		return packet.toPacket();

	}

	/**
	 * Sets the packet to reply to
	 */
	public void setPlayer(PlayerSave save, byte loginCode) {
		this.save = save;
		this.loginCode = loginCode;
	}

	/**
	 * Sets the packet to reply to
	 */
	public void setUID(long uID) {
		this.uID = uID;
	}
}
