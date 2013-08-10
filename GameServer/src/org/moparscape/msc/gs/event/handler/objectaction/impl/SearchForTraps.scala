package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.skill.thieving.Chest

class SearchForTraps extends ObjectEvent with MembersOnly {

	override def fire = {
		if (command == "search for traps") {
			if (p2pCheck(player) && !player.isPacketSpam) {
				player.setSpam(true)
				new Chest(player, o).stealFrom
			}
			false
		} else true
	}

}