package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.model.World
import org.moparscape.msc.config.Constants
import org.moparscape.msc.gs.plugins.extras.Thieving

class StealFrom extends ObjectEvent with MembersOnly {

	override def fire = {
		if (command == "steal from") {
			if (p2pCheck(player) && !player.isPacketSpam) {
				player.setSpam(true)
				val thiev = new Thieving(player, o)
				thiev.thieveStall
			}
			false
		} else true
	}

}