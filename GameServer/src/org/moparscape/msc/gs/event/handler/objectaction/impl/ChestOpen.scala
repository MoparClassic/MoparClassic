package org.moparscape.msc.gs.event.handler.objectaction.impl
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.skill.thieving.Chest

class ChestOpen extends ObjectEvent with MembersOnly {
	override def fire = {
		if (command == "open" && !player.isPacketSpam && p2pCheck(player)) {
			player.setSpam(true)
			new Chest(player, o).open
			false
		} else true
	}
}