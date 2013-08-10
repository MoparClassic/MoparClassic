package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.model.World
import org.moparscape.msc.gs.config.Constants
import org.moparscape.msc.gs.skill.thieving.Stall

class StealFrom extends ObjectEvent with MembersOnly {

	override def fire = {
		if (command == "steal from") {
			if (p2pCheck(player) && !player.isPacketSpam) {
				player.setSpam(true)
				new Stall(player, o).stealFrom
			}
			false
		} else true
	}

}