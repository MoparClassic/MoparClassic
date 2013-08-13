package org.moparscape.msc.gs.event.handler.objectaction.impl
import scala.collection.JavaConversions.asScalaBuffer

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.event.EventHandler

class DamagingApproach extends ObjectEvent {
	override def fire = {
		if (o.getID == 88) {
			player.setBusy(true)
			player.getActionSender.sendMessage(
				"The tree seems to lash out at you!")
			EventHandler.addShort {
				val damage = player.getCurStat(3) / 5
				player.setLastDamage(damage)
				player.setCurStat(3, player.getCurStat(3) - damage)
				player.getActionSender.sendStat(3)
				val playersToInform = player.getViewArea.getPlayersInView.toList
				playersToInform.foreach(_.informOfModifiedHits(player))

				player.getActionSender.sendMessage("You are badly scratched by the tree")
				player.setBusy(false)
			}
			false
		} else true
	}
}

class NormalApproach extends ObjectEvent {

	val ids = Array(400, 494)
	override def fire = {
		if (ids.contains(o.getID)) false
		else true
	}
}

class NothingApproach extends ObjectEvent {
	override def fire = {
		player.getActionSender.sendMessage("Nothing interesting happens.")
		true
	}
}