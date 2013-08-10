package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.model.GameObject
import org.moparscape.msc.gs.model.Player

class Door extends ObjectEvent {
	override def fire = {
		(o.getX, o.getY) match {
			case (222, 743) => door(1, 222, 743)
			case (224, 737) => door(2, 224, 736)
			case (220, 727) => door(3, 219, 727)
			case (212, 729) => door(4, 211, 729)
			case (206, 730) => door(5, 205, 730)
			case (201, 734) => door(6, 201, 734)
			case (198, 746) => door(7, 198, 746)
			case (204, 752) => door(8, 204, 752)

			case (209, 754) => door(9, 209, 754)
			case (217, 760) => door(10, 217, 760)
			case (222, 760) => door(11, 222, 760)
			case (226, 760) => door(12, 226, 760)
			case (230, 759) => door(13, 230, 758)
			case _ => false
		}
	}

	private def door(stage : Int, x : Int, y : Int) = {
		if (player.quests.get(0).stage >= stage) {
			player.teleport(x, y, false)
			false
		} else {
			player.getActionSender.sendMessage("You have not completed the necessary task yet.")
			false
		}
	}
}