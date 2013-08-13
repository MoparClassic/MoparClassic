package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.config.Formulae
import org.moparscape.msc.gs.model.{ ChatMessage, Player }
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.ShortEvent
import org.moparscape.msc.gs.event.EventHandler

class GoUp extends ObjectEvent {

	override def fire = {
		if (command == "climb-up" || command == "climb up" || command == "go up") {
			(o.getX, o.getY) match {
				// Prayer guild
				case (251, 468) => prayerGuild
				case _ => tele
			}
		}
		true
	}

	private def prayerGuild {
		if (player.getMaxStat(5) < 31) {
			player.setBusy(true)
			val abbot = Instance.getWorld.getNpc(174, 249, 252, 458, 468)
			if (abbot != null)
				player.informOfNpcMessage(
					new ChatMessage(
						abbot, "Hello only people with high prayer are allowed in here", player
					)
				)
			EventHandler.addShort {
				player.setBusy(false)
				player.getActionSender.sendMessage("You need a prayer level of 31 to enter")
			}
		} else tele
	}

	private def tele {
		val coords = {
			if (o.getGameObjectDef.getHeight <= 1) {
				Array(player.getX(),
					Formulae.getNewY(player.getY(), true))
			} else {
				val coords = Array(o.getX(),
					Formulae.getNewY(o.getY(), true))
				o.getDirection() match {
					case 0 =>
						coords(1) += o.getGameObjectDef.getHeight
					case 2 =>
						coords(0) += o.getGameObjectDef.getHeight
					case 4 =>
						coords(1) -= 1
					case 6 =>
						coords(0) -= 1
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