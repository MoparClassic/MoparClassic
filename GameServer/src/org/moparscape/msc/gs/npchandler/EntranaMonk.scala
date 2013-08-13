package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.Server
import org.moparscape.msc.gs.config.Constants.GameServer

class EntranaMonk extends NpcDialog {
	override def init {
		this + option1
		this + new GenericEnd("No thanks", npc, player)
	}

	override def begin {
		if (Server.isMembers) {
			this >> GameServer.P2P_LIMIT_MESSAGE
		}

		val toEntrana = !player.getLocation.inBounds(390, 530, 440, 580)

		if (toEntrana) {
			this > "Are you looking to take passage to our holy island?"
		} else {
			this > "Are you ready to go back to the mainland?"
		}

		end
	}

	lazy val option1 = new GenericEnd("Yes okay I'm ready to go", npc, player) {
		override def begin {

			val toEntrana = !player.getLocation.inBounds(390, 530, 440, 580)
			if (toEntrana) {
				// TODO: Check for weapons and such
				breath
				player.teleport(418, 570, false)
				breath
				this >> "The ship arrives at Entrana"
			} else {
				breath
				player.teleport(263, 659, false)
				breath
				this >> "The ship arrives at Port Sarim"
			}

			super.begin
		}
	}
}