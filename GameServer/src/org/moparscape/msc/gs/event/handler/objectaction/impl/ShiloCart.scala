package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.event.handler.objectaction.ObjectActionChain
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.ShortEvent

class ShiloCart extends ObjectActionChain(new SpecificLocation(384, 851), new Cart)

private class Cart extends ObjectEvent {
	override def fire = {
		player.setBusy(true)
		player.getActionSender.sendMessage("You search for a way over the cart")
		Instance.getDelayedEventHandler.add(new ShortEvent(player) {
			override def action {
				owner.getActionSender.sendMessage("You climb across")
				if (owner.getX <= 383) {
					owner.teleport(386, 851, false)
				} else {
					owner.teleport(383, 851, false)
				}
				owner.setBusy(false);
			}
		})
		false
	}
}