package org.moparscape.msc.gs.npchandler

import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.InvItem

class Ned extends NpcDialog {

	override def init {
		this + option1
		this + new GenericEnd("No thanks Ned, I don't need any", npc, player)
	}

	override def begin {
		this > "Why, hello there, lad."; breath
		this > "Me friends call me Ned. I was a man of the sea, but it's past me now"; breath
		this > "Could I be making or selling you some rope?"; breath
		end
	}

	lazy val option1 = new NpcDialog("Yes, I would like some rope", npc, player) {
		override def begin {

			this > "Well, I can sell you some rope for 15 coins"; breath
			this > "Or I can be making you some if you get me 4 balls of wool"; breath
			this > "I strands them together I does, make em string"; breath
			this < "You make rope from wool?"; breath
			this > "Of cource you can!"; breath
			this < "I thought you needed hemp or jute"; breath
			this > "Do you want some or not?"; breath

			end
		}

		this + new Transact("Okay, please sell me some rope", Array(10 -> 15), Array(237 -> 1), Array("It seems I don't have enough money, I'll be back later!"), npc, player) {
			override def success {
				this > "There you go. Finest rope in RuneScape"; breath
			}
		}

		this + new RespondEnd("That's a little more then I want to pay",
			Array(
				"Well, if you ever need rope that's the price. Sorry.",
				"An old sailor needs money for a little drop o' rum."
			), npc, player) {
		}

		this + new GenericEnd("I have wool that I would like to trade", npc, player) {
			override def begin {
				if (player.getInventory.countId(207) >= 4) {
					for (i <- (0 until 4)) player.getInventory.remove(207, 1)
					this >> "You give Ned the balls of wool"
					pause(800)
					player.getActionSender.sendInventory
					pause(1500)
					this >> "Ned fastens the wool into a coiled rope"
					pause(800)
					player.getInventory.add(237)
					this >> "Ned hands you a coil of rope"; breath
					player.getActionSender.sendInventory
				} else {
					this > "Aye matey! You ain't got no wool on yee!"; breath
					this < "Ah! I must of forgot it in the bank!"; breath
					this > "Go fetch me 4 balls of wool and speak with me again"
				}
				super.begin
			}
		}
	}

}