package org.moparscape.msc.gs.event.handler.objectaction.impl
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.event.EventHandler
import org.moparscape.msc.gs.model.Item
import org.moparscape.msc.gs.Instance

class Hopper extends ObjectEvent {
	override def fire = {
		if (o.containsItem == 29) {
			player.getActionSender.sendMessage("You operate the hopper...")
			EventHandler.addSingle(1000)(player.getActionSender.sendMessage("The grain slides down the chute"))
			if (isAt(179, 2371)) {
				Instance.getWorld.registerItem(new Item(23, 179, 481, 1, player))
			} else {
				Instance.getWorld.registerItem(new Item(23, 166, 599, 1, player))
			}
			o.containsItem(-1)
		}
		true
	}
}