package org.moparscape.msc.gs.model.container
import java.util.concurrent.CopyOnWriteArrayList
import org.moparscape.msc.gs.model.Player
import org.moparscape.msc.gs.model.Point
import scala.collection.mutable.ListBuffer
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.DelayedEvent
import scala.collection.JavaConversions._

class Shop(val name : String, val greeting : String,
		_options : java.util.List[String], val min : Point,
		val max : Point, val general : Boolean,
		val respawnRate : Int, val buyModifier : Int, val sellModifier : Int) extends Container(40, true, true) {
	val options = _options.toList

	var inited = false
	lazy val equilibrium = {
		val buf = new ListBuffer[(Int, Int)]
		items.get.foreach {
			i =>
				buf += ((i.id, i.amount))
		}
		buf.toList
	}

	def init {
		equilibrium
		this.synchronized {
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
									shop.remove(i.id, 1, true)
									if (!contains && i.amount <= 1) {
										shop.remove(i.id)
									}
									changed = true
								} else if (contains && i.amount < eq) {
									add(i.id)
									changed = true
								}

								if (changed) shop.updatePlayers
						}
					}
				}
			})
			inited = true
		}
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
		else equilibrium.find(_._1 == id) match {
			case Some(x) => true
			case None => false
		}
	}

	def equilibriumCount(id : Int) = {
		equilibrium.find(_._1 == id) match {
			case Some(x) => x._2
			case None => -1
		}
	}

	def withinShop(p : Point) = {
		p.getX >= min.getX && p.getX <= max.getX &&
			p.getY >= min.getY && p.getY <= max.getY
	}

	override def equals(o : Any) : Boolean = {
		if (o.isInstanceOf[Shop]) {
			return o.asInstanceOf[Shop].name == name
		} else false
	}

}