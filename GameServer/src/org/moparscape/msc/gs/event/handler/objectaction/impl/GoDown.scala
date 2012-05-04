package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.config.Formulae
import org.moparscape.msc.gs.model.{ ChatMessage, Player }
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.ShortEvent

class GoDown extends ObjectEvent {

	def fire = {
		val command = (
			if (click == 0) o.getGameObjectDef.getCommand1
			else o.getGameObjectDef.getCommand2
		).toLowerCase()
		if (command == "climb-down" || command == "climb down" || command == "go down") {
			tele
		}
		true
	}

	private def tele {
		val coords = {
			if (o.getGameObjectDef.getHeight <= 1) {
				Array(player.getX(),
					Formulae.getNewY(player.getY(), false))
			} else {
				val coords = Array(o.getX(),
					Formulae.getNewY(o.getY(), true))
				o.getDirection() match {
					case 0 =>
						coords(1) -= 1
					case 2 =>
						coords(0) -= 1
					case 4 =>
						coords(1) += o.getGameObjectDef.getHeight
					case 6 =>
						coords(0) += o.getGameObjectDef.getHeight
				}
				coords
			}
		}
		player.teleport(coords(0), coords(1), false)

	}

}