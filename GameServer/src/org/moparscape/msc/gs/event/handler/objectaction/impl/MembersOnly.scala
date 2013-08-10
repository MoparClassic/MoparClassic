package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.model.World
import org.moparscape.msc.gs.config.Constants
import org.moparscape.msc.gs.model.Player

trait MembersOnly {
	def p2pCheck(p : Player) = {
		if (!World.isMembers) {
			p.getActionSender.sendMessage(Constants.GameServer.P2P_LIMIT_MESSAGE)
			false
		} else true
	}
}