package org.moparscape.msc.gs.npchandler

import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.definition.EntityHandler
import org.moparscape.msc.gs.model.InvItem

class Aggie extends NpcDialog {

	private val dyes = Map(
		("Red", 238) -> 236,
		("Yellow", 239) -> 241,
		("Blue", 272) -> 281
	)

	override def begin {
		// Set busy flag and block
		player.setBusy(true)
		npc.blockedBy(player)
		
		this > "Hi traveller, I specialize in creating different colored dyes."
		breath
		this > "Would you like me to make you a dye?"
		breath
		end
	}

	dyes foreach {
		dye =>
			this + new NpcDialog(dye._1._1 + " dye please.", npc, player) {

				override def begin {
					this > ("You will need 1 "
						+ EntityHandler.getItemDef(dye._2).name
						+ " and 30gp for me to create the dye.")
					breath
					end
				}

				this + new NpcDialog("Yes I have them", npc, player) {
					override def begin {
						breath
						if (player.getInventory.remove(dye._2, 1) > -1
							&& player.getInventory.remove(10, 30) > -1) {

							this > "Here is your new dye, enjoy."
							player.getInventory.add(new InvItem(dye._1._2))
							player.getActionSender.sendInventory

						} else {
							this > "It seems that you don't have all of the ingredients."
							breath
							this > "Come back when you've obtained them"
						}
						cleanup
					}
				}

				this + new NpcDialog("I'll come back when I have the ingredients.", npc, player) {
					override def begin {
						cleanup
					}
				}

			}
	}

	this + new NpcDialog("No thanks", npc, player) {
		override def begin {
			cleanup
		}
	}

	private def cleanup {
		breath
		player.setBusy(false)
		npc.unblock
	}

}