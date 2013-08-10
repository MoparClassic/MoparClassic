package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.definition.EntityHandler
import org.moparscape.msc.gs.model.definition.extra.CerterDef
import org.moparscape.msc.gs.model.{ Npc, Player }
import org.moparscape.msc.gs.model.InvItem

class Certer extends NpcDialog {

	private var certerDef : CerterDef = null

	override def init {
		certerDef = EntityHandler.getCerterDef(npc.getID)
		this + option1
		this + option2
	}

	override def begin {

		this > "Welcome to my " + certerDef.getType + " exchange stall"; breath

		end
	}

	lazy val option1 = new NpcDialog("I have some certificates to trade in", npc, player) {
		override def begin {
			this > "What sort of certificate do you want to trade in?"; breath
			end
		}

		var index = 0
		certerDef.getCertNames foreach {
			n =>
				this + new CertType(n, index, npc, player)
				index += 1
		}
	}

	lazy val option2 = new NpcDialog("I have some " + certerDef.getType + " to trade in", npc, player) {
		override def begin {
			this > "What sort of " + certerDef.getType + " do you want to trade in?"; breath
			end
		}

		var index = 0
		certerDef.getCertNames foreach {
			n =>
				this + new ItemType(index, npc, player)
				index += 1
		}
	}

	private def msgForAmount(i : Int) = {
		i match {
			case 1 => "One"
			case 2 => "Two"
			case 3 => "Three"
			case 4 => "Four"
			case 5 => "Five"
			case 10 => "Ten"
			case 15 => "Fifteen"
			case 20 => "Twenty"
			case 25 => "Twentyfive"
			case _ => ""
		}
	}

	private class CertType(msg : String, index : Int, _npc : Npc, _player : Player) extends NpcDialog(msg, _npc, _player) {
		override def begin {
			val certId = certerDef.getCertID(index)
			val itemId = certerDef.getItemID(index)
			for (amount <- (1 to 5))
				this + new Amount(certId, itemId, amount, false, npc, player)
			this > "How many certificates do you wish to trade in?"; breath
			end
		}
	}

	private class ItemType(index : Int, _npc : Npc, _player : Player) extends NpcDialog(EntityHandler.getItemDef(certerDef.getItemID(index)).getName, _npc, _player) {
		override def begin {
			val certId = certerDef.getCertID(index)
			val itemId = certerDef.getItemID(index)
			for (amount <- (1 to 5))
				this + new Amount(certId, itemId, amount * 5, true, npc, player)
			this > "How many " + optionText + " do you wish to trade in?"; breath
			end
		}
	}

	private class Amount(certId : Int, itemId : Int, amount : Int, certing : Boolean, _npc : Npc, _player : Player) extends GenericEnd(msgForAmount(amount), _npc, _player) {

		override def begin {
			// Creating certificates
			if (enough && certing) {
				for (i <- (0 until amount))
					if (player.getInventory.remove(itemId, 1)) {
						player.getInventory.add(certId, 5)
					}

				breath
				this > ("You exchange the " + EntityHandler.getItemDef(itemId).getName + "s")
			} else if (enough) {
				if (player.getInventory.remove(certId, amount)) {
					for (i <- (0 until amount * 5))
						player.getInventory.add(itemId, 1)
					breath
					this > "You exchange the certificates."
				}
			}
			breath
			player.getActionSender.sendInventory
			super.begin
		}

		private def enough = {
			if (certing && player.getInventory.countId(itemId) < amount / 5) {
				this >> ("You don't have enough "
					+ EntityHandler.getItemDef(itemId).getName + "s"); breath
				false
			} else if (!certing && player.getInventory.countId(certId) < amount * 5) {
				this >> "You don't have enough certificates"; breath
				false
			} else {
				true
			}
		}

	}

}