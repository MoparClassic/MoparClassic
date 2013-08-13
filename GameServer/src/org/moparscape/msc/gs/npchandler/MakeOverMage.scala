package org.moparscape.msc.gs.npchandler

import org.moparscape.msc.gs.model.dialog.NpcDialog

class MakeOverMage extends NpcDialog {

	override def init {
		this + new GenericEnd("I'm happy with how I look, thank you", npc, player)
		this + option2
	}

	override def begin {
		this > "Are you happy with your looks?"; breath
		this > "If not I can change them for a small fee of 3000gp"; breath
		end
	}

	lazy val option2 = new Transact("Yes change my looks please", Array((10, 3000)),
		Array[(Int, Int)](), Array("Opps, I'll go get the money"), npc, player) {
		override def success {
			player.setChangingAppearance(true)
			player.getActionSender.sendAppearanceScreen
			super.success
		}
	}

}