package org.moparscape.msc.gs.event.handler.objectaction.impl
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.MiniEvent
import org.moparscape.msc.gs.model.Item

class Hopper extends ObjectEvent {
	def fire = {
		if (o.containsItem == 29) {
			player.getActionSender.sendMessage("You operate the hopper...")
			Instance.getDelayedEventHandler
				.add(new MiniEvent(
					player, 1000) {
					def action() {
						owner.getActionSender()
							.sendMessage(
								"The grain slides down the chute");
					}
				})
			if (isAt(179, 2371)) {
				Instance.getWorld.registerItem(new Item(23, 179, 481,
					1, player));
			} else {
				Instance.getWorld.registerItem(new Item(23, 166, 599,
					1, player));
			}
			o.containsItem(-1)
		}
		true
	}
}