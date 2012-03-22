package org.moparscape.msc.gs.model.container
import java.util.concurrent.CopyOnWriteArrayList
import org.moparscape.msc.gs.model.Player
import org.moparscape.msc.gs.model.Point
import scala.collection.mutable.ListBuffer
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.DelayedEvent

class Shop(val name : String, val greeting : String,
		val options : List[String], val min : Point,
		val max : Point, val general : Boolean,
		val respawnRate : Int, val buyModifier : Int, val sellModifier : Int) extends Container(40, true) {

	val equilibrium = {
		val buf = new ListBuffer[(Int, Int)]
		items.get.foreach {
			i =>
				buf += ((i.id, i.amount))
		}
		buf.toList
	}

	{
		val shop = this
		Instance.getDelayedEventHandler.add(new DelayedEvent(null, respawnRate) {
			var iterations = 0
			override def run {
				var changed = false

				items.synchronized {
					items.get.foreach {
						i =>
							var contains = false
							val eq = equilibrium.find(_._1 == i.id) match {
								case Some(x) => {
									contains = true
									x._2
								}
								case None => 0
							}

							if (iterations % 4 == 0 && i.amount > eq) {
								shop.remove(i.id, 1, contains)
								changed = true
							} else if (i.amount < eq) {
								add(i.id)
								changed = true
							}
							if (changed) shop.updatePlayers
					}
				}
			}
		})
	}

	private var players = List[Player]()
	def addPlayer(player : Player) = players.synchronized(players = player :: players)
	def removePlayer(player : Player) = players.synchronized()

	def updatePlayers {
		players.synchronized {
			players = players.filter(_.getShop == this)
			players foreach (_.getActionSender.showShop(this))
		}
	}

	def shouldStock(id : Int) = {
		if (general) true
		equilibrium.find(_._1 == id) match {
			case Some(x) => true
			case None => false
		}
	}

	def withinShop(p : Point) = {
		p.getX >= min.getX && p.getX <= max.getX &&
			p.getY >= min.getY && p.getY <= max.getY
	}

	override def equals(o : Any) = {
		if (o.isInstanceOf[Shop]) {
			o.asInstanceOf[Shop].name == name
		}
		false
	}

}