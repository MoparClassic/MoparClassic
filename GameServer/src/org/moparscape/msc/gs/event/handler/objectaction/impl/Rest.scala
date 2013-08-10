package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.event.EventHandler

class Rest extends ObjectEvent {

	override def fire = {
		if (command == "rest") {
			player.getActionSender.sendMessage("You rest on the bed")
			EventHandler.addShort(player.getActionSender.sendEnterSleep)
			false
		} else true
	}

}