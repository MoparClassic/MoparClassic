package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent

class Board extends ObjectEvent {

	override def fire = {
		player.getActionSender.sendMessage("You must talk to the owner about this.")
		false
	}

}