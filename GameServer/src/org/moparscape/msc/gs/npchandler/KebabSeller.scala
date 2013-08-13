package org.moparscape.msc.gs.npchandler

import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.InvItem

class KebabSeller extends NpcDialog {

	override def init {
		this + new GenericEnd("I think I'll give it a miss", npc, player)
		this + option1
	}

	override def begin {

		this > "Would you like to buy a nice kebab? Only 1 gold"; breath

		end
	}

	lazy val option1 = new Transact("Yes please", Array((10, 1)), Array((210, 1)),
		Array("Oops I don't seem to have enough money on me"), npc, player) {

		override def success {
			this >> "You buy a kebab"
			super.success
		}

		override def fail {
			this > "Come back when you have some"
			super.fail
		}
	}

}