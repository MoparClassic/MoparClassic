package org.moparscape.msc.gs.model.container
import org.moparscape.msc.gs.model.{ InvItem, Player }
import org.moparscape.msc.gs.phandler.client.WieldHandler
import org.moparscape.msc.gs.service.ItemAttributes

class Inventory(player : Player) extends Container(30) {

	def wielding(id : Int) = items.get.find(i => i.id == id && i.wielded) match {
		case Some(x) => true
		case None => false
	}

	override def remove(id : Int, amount : Int = 1, ignoreAmount : Boolean = false) : Boolean = {
		items.synchronized {
			val index = this.getLastItemSlot(id)
			if (index == -1) {
				return false
			}
			val wasWielding = this.getSlot(index).wielded
			val ret = super.remove(id, amount, ignoreAmount)
			if (ret && wasWielding) {
				val d = ItemAttributes.getWieldable(id)
				player.updateWornItems(d.getWieldPos(), player.getPlayerAppearance()
					.getSprite(d.getWieldPos()));
			}
			ret
		}
	}

	def clear {
		items.synchronized(items.get.foreach(i => remove(i.id, i.amount)))
	}

	def getSlot(slot : Int) = {
		items.synchronized(items.get()(slot))
	}

	def sortByValue {
		items.synchronized {
			items.set(items.get.sortWith(_.getDef.getBasePrice > _.getDef.getBasePrice).
				sortWith((a, b) => !isStackable(a.id) && isStackable(b.id)))
		}
	}

	def setWield(slot : Int, wield : Boolean) {
		items.synchronized {
			var itm = items.get
			val old = itm(slot)
			itm = itm.updated(slot, new InvItem(old.id, old.amount, wield))
			items.set(itm)
		}
	}

	def doThenSend(f : => Unit) {
		f
		player.getActionSender.sendInventory
	}
}