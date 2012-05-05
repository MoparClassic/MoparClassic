package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.plugins.extras.Thieving

class SearchForTraps extends ObjectEvent with MembersOnly {

	def fire = {
		if (command == "search for traps") {
			if (p2pCheck(player) && !player.isPacketSpam) {
				player.setSpam(true)
				val thiev = new Thieving(player, o)
				thiev.thieveChest
			}
			false
		} else true
	}

}