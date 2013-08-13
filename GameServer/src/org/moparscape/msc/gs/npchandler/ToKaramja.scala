package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog

class ToKaramja extends NpcDialog {
	override def init {

		this + new Transact("Yes please", Array(10 -> 30), Array(), Array("Oh dear I don't seem to have enough money"), npc, player) {

			override def success {
				this >> "You pay 30 gold"; pause(1300)
				this >> "You board the ship"; pause(1000)
				player.teleport(324, 713, false); pause(1500)
				this >> "The ship arrives at Karamja"
				super.success
			}

		}

		this + new GenericEnd("No thankyou", npc, player)
	}

	override def begin {
		this > "Do you want to go on a trip to Karamja?"
		this > "The trip will cost you 30 gold"
		breath
		end
	}
}