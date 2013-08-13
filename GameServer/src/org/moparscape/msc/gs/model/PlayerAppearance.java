package org.moparscape.msc.gs.model;

import org.moparscape.msc.gs.config.Formulae;
import org.moparscape.msc.gs.tools.DataConversions;

public class PlayerAppearance {

	private int body;
	private byte hairColour;
	private int head;
	private byte skinColour;

	private byte topColour;
	private byte trouserColour;

	public PlayerAppearance(int hairColour, int topColour, int trouserColour,
			int skinColour, int head, int body) {
		this.hairColour = (byte) hairColour;
		this.topColour = (byte) topColour;
		this.trouserColour = (byte) trouserColour;
		this.skinColour = (byte) skinColour;
		this.head = head;
		this.body = body;
	}

	public byte getHairColour() {
		return hairColour;
	}

	public byte getSkinColour() {
		return skinColour;
	}

	public int getSprite(int pos) {
		switch (pos) {
		case 0:
			return head;
		case 1:
			return body;
		case 2:
			return 3;
		default:
			return 0;
		}
	}

	public int[] getSprites() {
		return new int[] { head, body, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	}

	public byte getTopColour() {
		return topColour;
	}

	public byte getTrouserColour() {
		return trouserColour;
	}

	public boolean isValid() {
		if (!DataConversions.inArray(Formulae.headSprites, head)
				|| !DataConversions.inArray(Formulae.bodySprites, body)) {
			return false;
		}
		if (hairColour < 0 || topColour < 0 || trouserColour < 0
				|| skinColour < 0) {
			return false;
		}
		if (hairColour > 9 || topColour > 14 || trouserColour > 14
				|| skinColour > 4) {
			return false;
		}
		return true;
	}

}
