package org.moparscape.msc.gs.event.handler.objectaction.impl
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.plugins.extras.Thieving

class ChestOpen extends ObjectEvent with MembersOnly {
	def fire = {
		if (command == "open" && !player.isPacketSpam && p2pCheck(player)) {
			val lock = new Thieving(player, o);
			// This logic seems wrong. 
			// Shouldn't it only open the chest once,
			// if there is a corresponding id?
			val chest = lock.Chests.filter(_(0) == o.getID).foreach {
				c =>
					player.setSpam(true)
					lock.openThievedChest
			}
			false
		} else true
	}
}