package org.moparscape.msc.gs.npchandler

import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.InvItem

class BananaExchange extends NpcDialog {

	override def init {
		this + option1
		this + new GenericEnd("No sorry, I don't have any.", npc, player)
	}

	override def begin {
		
		this > "Hello, I am after 20 bananas, do you have 20 you can sell?"

		breath

		end
	}

	lazy val option1 = new NpcDialog("Yes, I will sell you 20 bananas.", npc, player) {

		override def begin {
			this > "I will give you 30gp for your 20 bananas, is that ok?"
			breath
			end
		}

		this + new GenericEnd("Sure", npc, player) {
			override def begin {
				breath
				if (player.getInventory.countId(249) >= 20) {
					for (i <- (0 to 20)) player.getInventory.remove(249, 1)
					player.getInventory.add(new InvItem(10, 30))

					this >> "You receive 30gp"
					breath
					player.getActionSender.sendInventory
				} else {
					this > "It looks like you don't have enough Bananas, don't waste my time."
				}

				super.begin
			}
		}

		this + new GenericEnd("Sorry, I would rather eat them.", npc, player)
	}

}