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
		
		this > "Hello, may I help you?"

		breath
		end
	}

	lazy val option1 = new GenericEnd("Do you have a potion to make my hair fall out?", npc, player) {
		override def begin {
			this > "Here you are, enjoy this"

			breath

			player.getInventory.add(new InvItem(58))
			
			breath
			
			this >> "Apothecary hands you a mysterious potion"
			player.getActionSender.sendInventory

			super.begin
		}
	}

	lazy val option2 = new NpcDialog("I am in need of a strength potion.") {

		override def begin {
			this > "Bring me a limpwurt root and a spider egg and i will make you one"
			breath
			end
		}

		this + new GenericEnd("I have the ingredients.", npc, player) {
			override def begin {
				if (player.getInventory.countId(220) > 0
					&& player.getInventory.countId(219) > 0) {

					player.getInventory.remove(220, 1)
					player.getInventory.remove(219, 1)
					player.getInventory.add(new InvItem(221))
					
					breath
					
					this >> "Apothecary hands you a Strength Potion (4 dose)"
					player.getActionSender.sendInventory

				} else {
					this > "It seems you don't have everything i asked for, come back later."
				}
				super.begin
			}
		}

		this + new GenericEnd("Ok, I'll find them", npc, player)
	}

	lazy val option3 = new GenericEnd("No thanks.", npc, player)

}