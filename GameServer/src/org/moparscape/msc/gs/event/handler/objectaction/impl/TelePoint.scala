package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.model.definition.EntityHandler
import org.moparscape.msc.gs.model.Point

class TelePoint extends ObjectEvent {

	override def fire = {
		val tp = EntityHandler.getObjectTelePoint(new Point(o.getX, o.getY), command)
		if (tp != null) {
			player.teleport(tp.getX, tp.getY, false)
			false
		} else true
	}

}