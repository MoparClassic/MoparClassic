package org.moparscape.msc.gs.event.handler.objectaction.impl
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent

class DefaultObjectAction extends ObjectEvent {
	def fire = {
		player.getActionSender.sendMessage("Nothing interesting happens.")
		true
	}
}