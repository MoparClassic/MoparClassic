package org.moparscape.msc.gs.npchandler

import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.InvItem

class BananaExchange extends NpcDialog {

	override def init {
		this + option1
		this + new GenericEnd("No sorry, I don't have any.", npc, player)
	}

	override def begin {

		this > "Hello, I am after 20 bananas, do you have 20 you can sell?"; breath

		end
	}

	lazy val option1 = new NpcDialog("Yes, I will sell you 20 bananas.", npc, player) {

		override def begin {
			this > "I will give you 30gp for your 20 bananas, is that ok?"; breath
			end
		}

		this + new Transact("Sure", Array((249, 20)), Array((10, 30)),
			Array("It looks like you don't have enough Bananas, don't waste my time."),
			npc, player) {
			override def success {
				this >> "You receive 30gp"
				super.success
			}
		}

		this + new GenericEnd("Sorry, I would rather eat them.", npc, player)
	}

}