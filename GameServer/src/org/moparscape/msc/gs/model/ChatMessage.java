package org.moparscape.msc.gs.model;

import org.moparscape.msc.gs.tools.DataConversions;

public class ChatMessage {
	/**
	 * The message it self, in byte format
	 */
	private byte[] message;
	/**
	 * Who the message is for
	 */
	private Mob recipient = null;
	/**
	 * Who sent the message
	 */
	private Mob sender;

	public ChatMessage(Mob sender, byte[] message) {
		this.sender = sender;
		this.message = message;
	}

	public ChatMessage(Mob sender, String message, Mob recipient) {
		this.sender = sender;
		this.message = DataConversions.stringToByteArray(message);
		this.recipient = recipient;
	}

	public int getLength() {
		return message.length;
	}

	public byte[] getMessage() {
		return message;
	}

	public Mob getRecipient() {
		return recipient;
	}

	public Mob getSender() {
		return sender;
	}

}
