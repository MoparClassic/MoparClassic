package org.moparscape.msc.gs.event.handler.objectaction.impl
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.event.handler.objectaction.ObjectActionChain
import org.moparscape.msc.gs.event.EventHandler

class GnomeCaveRoots extends ObjectEvent {
	override def fire : Boolean = {

		val loc = {
			if (o.getX == 701 && o.getY == 3280) 701 -> 3278
			else if (o.getX == 701 && o.getY == 3279) 701 -> 3281
			else {
				new DefaultObjectAction().fire
				return false
			}
		}

		player.setBusy(true)
		player.getActionSender.sendMessage("You push the roots")
		EventHandler.addShort {
			player.getActionSender.sendMessage("They wrap around you and drag you forwards")
			player.teleport(loc._1, loc._2, false)
			player.setBusy(false)
		}
		false
	}
}