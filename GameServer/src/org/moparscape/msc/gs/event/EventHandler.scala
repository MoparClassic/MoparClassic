package org.moparscape.msc.gs.event
import org.moparscape.msc.gs.Instance

object EventHandler {
	def addShort(f : => Unit) {
		Instance.getDelayedEventHandler.add(new ShortEvent(null) {
			override def action = f
		})
	}
}