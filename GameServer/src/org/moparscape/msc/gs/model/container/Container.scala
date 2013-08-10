package org.moparscape.msc.gs.model.container
import java.util.concurrent.CopyOnWriteArrayList
import org.moparscape.msc.gs.model.definition.EntityHandler
import java.util.concurrent.atomic.AtomicReference
import net.jcip.annotations.ThreadSafe
import org.moparscape.msc.gs.model.InvItem
import scala.collection.JavaConversions._

@ThreadSafe
class Container(val maxSize : Int, allStackable : Boolean = false, allowZeros : Boolean = false) {
	protected var items = new AtomicReference(List[InvItem]())

	def add(id : Int, amount : Int = 1, ignoreMaxSize : Boolean = false) : Boolean = {
		if (amount < 0) {
			return false
		} else if (!allowZeros && amount == 0) {
			return false
		} else {
			items.synchronized {
				var itm = items.get
				if (!ignoreMaxSize && !canHold(id, amount)) {
					return false
				}

				if (isStackable(id)) {
					if (contains(id))
						itm = itm map (e => if (e.id == id) new InvItem(id, e.amount + amount) else e)
					else
						itm = itm ::: List(new InvItem(id, amount))
				} else {

					for (i <- (0 until amount)) itm = itm ::: List(new InvItem(id))

				}
				items.set(itm)
			}
			return true
		}
	}

	def removeAll(id : Int) = {
		items.synchronized {
			var itm = items.get
			val size = itm.size
			if (allowZeros) {
				itm = itm.map(e => if (e.id == id) new InvItem(id, 0) else e)
			} else {
				itm = itm.filterNot(_.id == id)
			}
			items.set(itm)
			size - itm.size
		}
	}

	def remove(id : Int, amount : Int = 1, ignoreAmount : Boolean = false) : Boolean = {
		items.synchronized {
			var ret = false
			var itm = items.get.reverse
			if (countId(id) >= amount) {
				if (isStackable(id)) {
					itm = itm.map(e => if (id == e.id) new InvItem(id, e.amount - amount) else e)
				} else {
					var count = 0
					itm = itm.filter {
						i =>
							if (count < amount && i.id == id) {
								count += 1
								false
							} else true
					}
				}
				ret = true
			}
			if (allowZeros && !ignoreAmount) {
				itm = itm.map(e => if (e.id == id && e.amount < 0) new InvItem(id, 0) else e)
			} else {
				itm = if (!ignoreAmount) itm.filterNot(_.amount <= 0) else itm
			}
			items.set(itm.reverse)
			return ret
		}
	}

	def countId(id : Int) = {
		items.synchronized(items.get.foldLeft(0)((c, i) => if (i.id == id) c + i.amount else c))
	}

	def contains(id : Int) = {
		items.synchronized {
			items.get.find(e => e.id == id) match {
				case Some(x) => true
				case None => false
			}
		}
	}

	def isFull = items.get.size == maxSize

	def getItems : java.util.List[InvItem] = items.get.map(i => i)

	def getLastItemSlot(id : Int) = items.get.lastIndexWhere(_.id == id)

	private def getSize(id : Int, amount : Int, size : Int = items.get.size) = {
		if (id != -1) {
			if (isStackable(id) && countId(id) <= amount) {
				size - 1
			} else if (!isStackable(id)) {
				size - amount
			}
		}
		size
	}

	def canHold(added : java.util.List[InvItem], _taken : java.util.List[InvItem]) : Boolean = {
		var taken = _taken
		items.synchronized {
			var itms = items.get
			if (taken == null) {
				taken = List[InvItem]()
			}
			// Calculates the size after everything but the first element in added is done
			var size = added.drop(1).foldLeft(taken.foldLeft(itms.size)((s, i) => getSize(i.id, i.amount, s)))((s, i) => getSize(i.id, -i.amount, s))
			if (added.size < 1) true else canHold(added(0).id, added(0).amount, size)
		}
	}

	def canHold(id : Int, amount : Int = 1) : Boolean = {
		items.synchronized {
			val itm = items.get
			canHold(id, amount, itm.size)
		}
	}

	protected def canHold(id : Int, amount : Int, size : Int) = {
		items.synchronized {
			val itm = items.get
			if (isStackable(id) && contains(id)) true
			else if (isStackable(id) && size + 1 <= maxSize) true
			else if (size + amount <= maxSize) true
			else false
		}
	}

	protected def isStackable(id : Int) = {
		if (allStackable) true
		else EntityHandler.getItemDef(id).isStackable
	}

	def size = items.get.size
}