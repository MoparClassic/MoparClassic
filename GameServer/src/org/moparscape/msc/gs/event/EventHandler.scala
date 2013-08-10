package org.moparscape.msc.gs.event
import org.moparscape.msc.gs.Instance

object EventHandler {
	def addShort(f : => Unit) = add(new ShortEvent(null) { override def action = f })

	def addSingle(delay : Int)(f : => Unit) = add(new SingleEvent(null, delay) { override def action = f })

	def add(event : DelayedEvent) = Instance.getDelayedEventHandler.add(event)
}