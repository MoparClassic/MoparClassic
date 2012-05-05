package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.ShortEvent

class Cupboard extends ObjectEvent {

	def fire = {
		if(command == "search")
		try {
			player.getActionSender.sendMessage("You search the " + o.getGameObjectDef.name + "...");
			Instance.getDelayedEventHandler().add(
				new ShortEvent(player) {
					def action() {
						actions
					}
				});
		} catch {
			case _ =>
		}
		true
	}

	private def actions {
		if (isAt(216, 1562)) {
			player.getActionSender.sendMessage("You find Garlic!")
			player.getInventory.add(218, 1, false)
			player.getActionSender.sendInventory
		} else {
			player.getActionSender.sendMessage("You find nothing")
		}
	}

}