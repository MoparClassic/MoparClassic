package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.SingleEvent

class Pick extends ObjectEvent with MembersOnly {

	def fire = {
		if (command == "pick" || command == "pick banana") {
			o.getID match {
				case 72 => pick(29, "You get some gain")
				case 191 => pick(348, "You pick a potato")
				case 183 => pick(249, "You pull a banana off the tree")
				case 313 => {
					if (p2pCheck(player)) {
						pick(675, "You uproot a flax plant")
					}
				}
				case _ => player.getActionSender.sendMessage("Nothing interesting happens.")
			}
			false
		} else true
	}

	private def pick(id : Int, msg : String) {
		player.getActionSender.sendMessage(msg)
		player.getInventory.add(id)
		player.getActionSender.sendSound("potato")
		player.getActionSender.sendInventory
		player.setBusy(true);
		Instance.getDelayedEventHandler().add(new SingleEvent(player, 200) {
			def action = owner.setBusy(false)
		})
	}

}