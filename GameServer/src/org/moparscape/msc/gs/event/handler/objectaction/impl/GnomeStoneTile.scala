package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectActionChain
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.event.EventHandler

class GnomeStoneTile extends ObjectActionChain(new SpecificLocation(416, 161), new StoneTile)

private class StoneTile extends ObjectEvent {
	override def fire = {
		player.getActionSender.sendMessage("You twist the stone tile to one side")
		EventHandler.addShort {
			player.getActionSender.sendMessage("It reveals a ladder, you climb down")
			player.teleport(703, 3284, false)
			player.setBusy(false)
		}
		false
	}
}