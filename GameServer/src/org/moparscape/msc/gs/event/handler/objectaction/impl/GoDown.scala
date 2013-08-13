package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.config.Formulae
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.event.EventHandler
import org.moparscape.msc.gs.model.ChatMessage
import org.moparscape.msc.gs.Instance

class GoDown extends ObjectEvent {

	override def fire = {
		if (command == "climb-down" || command == "climb down" || command == "go down") {
			(o.getX, o.getY) match {
				// Mining guild
				case (274, 566) => miningGuild
				case _ => tele
			}
		}
		true
	}

	def miningGuild {
		if (player.getCurStat(14) < 60) {
			player.setBusy(true)
			val dwarf = Instance.getWorld.getNpc(191, 272, 277, 563, 567)
			if (dwarf != null) {
				player.informOfNpcMessage(new ChatMessage(dwarf, "Hello only the top miners are allowed in here", player))
			}
			EventHandler.addShort {
				player.setBusy(false)
				player.getActionSender.sendMessage("You need a mining level of 60 to enter")
			}
		} else {
			player.teleport(274, 3397, false);
		}
	}

	private def tele {
		val coords = {
			if (o.getGameObjectDef.getHeight <= 1) {
				Array(player.getX(),
					Formulae.getNewY(player.getY(), false))
			} else {
				val coords = Array(o.getX(),
					Formulae.getNewY(o.getY(), false))
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
		val regionX = coords(0) / 48 + 48
		val regionY = ((coords(1) % 944) / 48) + 37
		val regionZ = Formulae.getHeight(coords(1))
		player.teleport(coords(0), coords(1), false)

	}

}