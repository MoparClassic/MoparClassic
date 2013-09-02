package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog

class Tramp extends NpcDialog {

override def init {
		this + option1
		this + option2
		this + option3
		//option4 shield of arrav
	}

	override def begin {
		this > "Spare some change guv?"; breath
		end
	}

	lazy val option1 = new GenericEnd("Sorry I haven't got any", npc, player)

	lazy val option2 = new Transact("Ok here you go",
			Array((10, 1)), Array[(Int, Int)](), Array(".."), npc, player) {
		override def success {
			this >> "You give a coin to the tramp"; pause(1500)
			this > "Hey thanks!"
			super.success
		}
		override def fail {
			this >> "You don't have any coins"; pause(1500)
			super.fail
		}
	}

	lazy val option3 = new RespondEnd("Go get a job",
			Array("You startin?"), npc, player)
}
