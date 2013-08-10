package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.{ Npc, Player }
import org.moparscape.msc.gs.model.InvItem

class Wyson extends NpcDialog {
	override def init {
		this + option1
		this + new GenericEnd("Not right now thanks", npc, player)
	}

	override def begin {
		this > "I am the gardener round here"; breath
		this > "Do you have any gardening that needs doing?"; breath
		end
	}

	lazy val option1 = new NpcDialog("I'm looking for woad leaves", npc, player) {

		this + new NoGood("How about 5 coins?", npc, player)
		this + new NoGood("How about 10 coins?", npc, player)

		this + new GenericEnd("How about 15 coins?", npc, player) {
			override def begin {
				this > "Mmmm ok that sounds fair"; breath
				if (player.getInventory.remove(10, 15)) {
					this >> "You give Wyson 15 coins"; breath
					player.getInventory.add(281); breath
					this >> "Wyson the gardener gives you some woad leaves"
					player.getActionSender.sendInventory
				} else {
					this > "I don't have enough coins to buy the leaves. I'll come back later"
				}
				super.begin
			}
		}

		this + new GenericEnd("How about 20 coins?", npc, player) {
			override def begin {
				this > "Ok that's more than fair."; breath
				if (player.getInventory.remove(10, 20)) {
					this >> "You give Wyson 20 coins"; breath
					player.getInventory.add(281, 2)
					this >> "Wyson the gardener gives you some woad leaves"; breath
					this > "Here have some more you're a generous person"
					player.getActionSender.sendInventory
				} else {
					this > "I don't have enough coins to buy the leaves. I'll come back later"
				}
				super.begin
			}
		}

		override def begin {
			this > "Well luckily for you I may have some around here"; breath
			this < "Can I buy one please?"; breath
			this > "How much are you willing to pay?"; breath
			end
		}

		private class NoGood(msg : String, _npc : Npc, _player : Player) extends GenericEnd(msg, _npc, _player) {

			override def begin {
				this > "No no that's far too little. Woad leaves are hard to get you know"; breath
				this > "I used to have plenty but someone kept stealing them off me"; breath
				super.begin
			}
		}
	}
}