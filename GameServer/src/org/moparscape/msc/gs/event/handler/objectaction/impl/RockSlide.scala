package org.moparscape.msc.gs.event.handler.objectaction.impl
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent

class RockSlide extends ObjectEvent {
	override def fire = {
		if (player.withinRange(o.getLocation(), 2)) {
			player.getActionSender().sendMessage("You slide down the rocks")
			player.teleport(579, 3357, false)
		}
		true
	}
}