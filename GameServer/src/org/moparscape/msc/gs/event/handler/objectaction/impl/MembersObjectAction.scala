package org.moparscape.msc.gs.event.handler.objectaction.impl
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.model.World
import org.moparscape.msc.gs.config.Constants

class MembersObjectAction extends ObjectEvent {
	override def fire = {
		if (!World.isMembers && isMembersObject) {
			player.getActionSender.sendMessage(Constants.GameServer.P2P_LIMIT_MESSAGE)
			false
		} else true
	}

	private def isMembersObject = {
		byLocation || byId
	}

	private def byLocation = {
		(o.getX, o.getY) match {
			// Dragon maze?
			case (243, 178) => true
			// To digsite
			case (59, 573) => true
			// Edgeville dungeon wild
			case (196, 3266) => true
			// Ardy lever to wild
			case (621, 596) => true
			case _ => false
		}
	}

	private def byId = {
		o.getID match {
			case _ => false
		}
	}
}