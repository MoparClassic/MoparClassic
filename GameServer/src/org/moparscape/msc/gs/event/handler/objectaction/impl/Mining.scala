package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.skill.mining.Ore

class Mining extends ObjectEvent {
	override def fire = {
		if (command == "mine") {
			new Ore(player, o).mine
			false
		} else if (command == "prospect") {
			new Ore(player, o).prospect
			false
		} else true
	}
}