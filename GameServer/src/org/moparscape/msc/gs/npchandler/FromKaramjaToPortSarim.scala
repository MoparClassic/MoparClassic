package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.{ Npc, Player }

class FromKaramjaToPortSarim extends NpcDialog {
	override def init {
		this + new Board(npc, player)
		this + new RespondEnd("Does Karamja have any unusal customs then?",
			Array("I'm not that sort of customs officer"), npc, player)
	}

	override def begin {
		end
	}

	private class Board(_npc : Npc, _player : Player)
			extends NpcDialog("Can I board this ship?", _npc, _player) {
		override def begin {
			breath
			this > "You need to be searched before you can board"; breath
			end
		}

		this + new RespondEnd("Why?", Array("Because asgarnia has banned the import of intoxicating spirits"), _npc, _player)

		this + new Search(_npc, _player)

		this + new RespondEnd("You're not putting your hands on my things!", Array("You're not getting on this ship then"), _npc, _player)
	}

	private class Search(_npc : Npc, _player : Player)
			extends NpcDialog("Search away I have nothing to hide", _npc, _player) {
		override def begin {
			if (player.getInventory.removeAll(318) > 0) {
				this >> "The customs officer confiscates your rum"
			} else {
				this > "Well you've got some odd stuff, but it's all legal"; breath
				this > "Now you need to pay a boarding charge of 30 gold"; breath
				end
			}
		}

		this + new Transact("Yes", Array((10, 30)), Array[(Int, Int)](),
			Array("Oh dear it seems i don't have enough money"), _npc, _player) {
			override def begin {
				this >> "You pay 30 gold"
				super.begin
			}

			override def success {
				pause(1500)
				this >> "You board the ship"; pause(3000)
				player.teleport(269, 648, false); pause(1000)
				this >> "You arrive at Port Sarim"; breath
				super.success
			}
		}

		this + new GenericEnd("Oh, I'll not bother then", _npc, _player)
	}
}