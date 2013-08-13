package org.moparscape.msc.gs.model;

public class Bubble {
	/**
	 * What to draw in it
	 */
	private int itemID;
	/**
	 * Who the bubble belongs to
	 */
	private Player owner;

	public Bubble(Player owner, int itemID) {
		this.owner = owner;
		this.itemID = itemID;
	}

	public int getID() {
		return itemID;
	}

	public Player getOwner() {
		return owner;
	}

	public void broadcast() {
		for (Player p : owner.getViewArea().getPlayersInView()) {
			p.informOfBubble(this);
		}
	}

}
