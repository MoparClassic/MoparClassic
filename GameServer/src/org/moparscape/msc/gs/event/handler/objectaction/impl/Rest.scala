package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.ShortEvent

class Rest extends ObjectEvent {

	def fire = {
		if (command == "rest") {
			player.getActionSender.sendMessage("You rest on the bed")
			Instance.getDelayedEventHandler.add(
				new ShortEvent(player) {
					def action {
						owner.getActionSender.sendEnterSleep
					}
				})
			false
		} else true
	}

}