package org.moparscape.msc.gs.event.handler.objectaction.impl
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.ShortEvent
import collection.JavaConversions._

class DamagingApproach extends ObjectEvent {
	def fire = {
		if (o.getID == 88) {
			player.setBusy(true)
			player.getActionSender.sendMessage(
				"The tree seems to lash out at you!")
			Instance.getDelayedEventHandler.add(new ShortEvent(player) {
				def action {
					val damage = owner.getCurStat(3) / 5
					owner.setLastDamage(damage)
					owner.setCurStat(3, owner.getCurStat(3) - damage)
					owner.getActionSender.sendStat(3)
					val playersToInform = owner.getViewArea.getPlayersInView.toList
					playersToInform.foreach(_.informOfModifiedHits(owner))

					owner.getActionSender.sendMessage("You are badly scratched by the tree")
					owner.setBusy(false)
				}
			})
			false
		} else true
	}
}

class NormalApproach extends ObjectEvent {

	val ids = Array(400, 494)
	def fire = {
		if (ids.contains(o.getID)) false
		else true
	}
}

class NothingApproach extends ObjectEvent {
	def fire = {
		player.getActionSender.sendMessage("Nothing interesting happens.")
		true
	}
}