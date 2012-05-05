package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.MiniEvent

class Hit extends ObjectEvent {

	def fire = {
		if (command == "hit") {
			player.setBusy(true)
			player.getActionSender.sendMessage(
				"You attempt to hit the Dummy")
			Instance.getDelayedEventHandler.add(
				new MiniEvent(player, 3500) {
					def action {
						owner.setBusy(false)
						if (owner.getCurStat(0) > 7) {
							owner.getActionSender.sendMessage(
								"There is only so much you can learn from hitting a Dummy"
							)
						} else {
							owner.getActionSender.sendMessage("You hit the Dummy")
							owner.incExp(0, 5, false)
							owner.getActionSender.sendStat(0)
						}
					}
				})
		}
		true
	}

}