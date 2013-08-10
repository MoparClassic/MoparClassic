package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.event.handler.objectaction.ObjectActionChain
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.ShortEvent
import org.moparscape.msc.gs.event.EventHandler

class ShiloCart extends ObjectActionChain(new SpecificLocation(384, 851), new Cart)

private class Cart extends ObjectEvent {
	override def fire = {
		player.setBusy(true)
		player.getActionSender.sendMessage("You search for a way over the cart")
		EventHandler.addShort {
			player.getActionSender.sendMessage("You climb across")
			if (player.getX <= 383) {
				player.teleport(386, 851, false)
			} else {
				player.teleport(383, 851, false)
			}
			player.setBusy(false);
		}
		false
	}
}