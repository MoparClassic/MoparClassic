package org.moparscape.msc.gs.event.handler.objectaction.impl
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent

class Recharge extends ObjectEvent {

	override def fire = {
		player.getActionSender.sendMessage("You recharge at the altar.")
		player.getActionSender.sendSound("recharge")
		val maxPray = player.getMaxStat(5) + (if (o.getID == 200) 2 else 0)
		if (player.getCurStat(5) < maxPray) {
			player.setCurStat(5, maxPray);
		}
		player.getActionSender.sendStat(5);
		false
	}

}