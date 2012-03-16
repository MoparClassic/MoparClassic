package org.moparscape.msc.gs.npchandler

import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.InvItem

class KebabSeller extends NpcDialog {

	override def init {
		this + new GenericEnd("I think I'll give it a miss", npc, player)
		this + option1
	}

	override def begin {

		this > "Would you like to buy a nice kebab? Only 1 gold"

		breath

		end
	}

	lazy val option1 = new GenericEnd("Yes please", npc, player) {
		override def begin {
			if(player.getInventory.remove(10, 1) > -1) {
				player.getInventory.add(new InvItem(210, 1))
				this >> "You buy a kebab"
				breath
				player.getActionSender.sendInventory
			} else {
				this < "Opps I don't seem to have enough money on me"
				breath
				this > "Come back when you have some"
			}
			super.begin
		}
	}

}