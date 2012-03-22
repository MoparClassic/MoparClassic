package org.moparscape.msc.gs.model.container
import org.moparscape.msc.gs.model.InvItem

class Inventory extends Container(30) {
	def wielding(id : Int) = items.get.find(i => i.id == id && i.wielded) match {
		case Some(x) => true
		case None => false
	}
	def clear {
		items.synchronized(items.set(List[InvItem]()))
	}

	def getSlot(slot : Int) = {
		val itm = items.get
		itm(slot)
	}
	
	def sortByValue {
		items.synchronized {
			items.set(items.get.sortWith(_.getDef.getBasePrice > _.getDef.getBasePrice))
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
}