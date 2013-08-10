package org.moparscape.msc.gs.model.snapshot;

import java.util.ArrayList;

/**
 * Chat log class, stores chat string, time of message, message owner and set of
 * players that saw the message
 * 
 * @author Pets
 * 
 */
public class Chatlog extends Snapshot {

	/**
	 * The messages that was sent
	 */
	private String message;
	/**
	 * Set of players that received the message;
	 */
	private ArrayList<String> recievers = new ArrayList<String>();

	/**
	 * Constructor
	 * 
	 * @param sender
	 *            player that sent the message
	 * @param chatstring
	 *            the message that was sent
	 * @param recievers
	 *            players that saw the public message
	 * 
	 */
	public Chatlog(String sender, String chatstring, ArrayList<String> recievers) {
		super(sender);
		this.setMessage(chatstring);
		this.recievers = recievers;
	}

	public ArrayList<String> getRecievers() {
		return recievers;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String toString() {
		return this.owner + " said " + message;
	}
}
