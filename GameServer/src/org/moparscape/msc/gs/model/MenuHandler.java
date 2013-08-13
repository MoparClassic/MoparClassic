package org.moparscape.msc.gs.model;

public abstract class MenuHandler {
	/**
	 * Array of possible options that can be chosen
	 */
	protected String[] options;
	/**
	 * The Player this handler is responsible for
	 */
	protected Player owner;

	/**
	 * Creates a new MenuHandler with the given options
	 */
	public MenuHandler(String[] options) {
		this.options = options;
	}

	/**
	 * Gets the appropriate option string
	 */
	public final String getOption(int index) {
		if (index < 0 || index >= options.length) {
			return null;
		}
		return options[index];
	}

	public final String[] getOptions() {
		return options;
	}

	/**
	 * Abstract method for handling the reply
	 */
	public abstract void handleReply(int option, String reply);

	public void abort() {
	}

	/**
	 * Set the Player this MenuHandler is responsible for
	 */
	public final void setOwner(Player owner) {
		this.owner = owner;
	}
}