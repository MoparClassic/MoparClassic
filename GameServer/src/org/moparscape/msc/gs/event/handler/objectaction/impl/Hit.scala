package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.event.EventHandler

class Hit extends ObjectEvent {

	override def fire = {
		if (command == "hit") {
			player.setBusy(true)
			player.getActionSender.sendMessage(
				"You attempt to hit the Dummy")
			EventHandler.addSingle(3500) {
				player.setBusy(false)
				if (player.getCurStat(0) > 7) {
					player.getActionSender.sendMessage(
						"There is only so much you can learn from hitting a Dummy"
					)
				} else {
					player.getActionSender.sendMessage("You hit the Dummy")
					player.incExp(0, 5, false)
					player.getActionSender.sendStat(0)
				}
			}
		}
		true
	}

}