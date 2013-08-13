package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.event.EventHandler

class Cupboard extends ObjectEvent {

	override def fire = {
		if (command == "search")
			try {
				player.getActionSender.sendMessage("You search the " + o.getGameObjectDef.name + "...");
				EventHandler.addShort(actions)
			} catch {
				case _ : Throwable =>
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