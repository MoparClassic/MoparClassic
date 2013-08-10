package org.moparscape.msc.gs.event.handler.objectaction.impl
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent

class SpecificLocation(x : Int, y : Int, doOnFail : => Boolean = { new DefaultObjectAction().fire }) extends ObjectEvent {

	override def fire = {
		if (o.getX != x || o.getY != y) {
			doOnFail
		} else {
			true
		}
	}

}