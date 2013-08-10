package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.InvItem

class Apothecary extends NpcDialog {

	override def init {
		this + option1
		this + option2
		this + option3
	}

	override def begin {
		this > "Hello, may I help you?"; breath
		end
	}

	lazy val option1 = new Transact("Do you have a potion to make my hair fall out?",
		Array[(Int, Int)](), Array((58, 1)), Array[String](), npc, player) {
		override def success {
			this > "Here you are, enjoy this"
			this >> "Apothecary hands you a mysterious potion"
			super.success
		}
	}

	lazy val option2 = new NpcDialog("I am in need of a strength potion.") {

		override def begin {
			breath
			this > "Bring me a limpwurt root and a spider egg and i will make you one"; breath
			end
		}

		this + new Transact("I have the ingredients.",
			Array((220, 1), (219, 1)), Array((221, 1)), Array(),
			npc, player) {
			override def success {
				this >> "Apothecary hands you a Strength Potion (4 dose)"
				super.success
			}

			override def fail {
				breath; this > "It seems you don't have everything i asked for, come back later."
				super.fail
			}

		}

		this + new GenericEnd("Ok, I'll find them", npc, player)
	}

	lazy val option3 = new GenericEnd("No thanks.", npc, player)

}