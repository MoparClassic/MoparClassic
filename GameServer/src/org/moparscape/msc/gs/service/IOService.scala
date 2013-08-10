package org.moparscape.msc.gs.service

import akka.actor.Actor

/**
 * @author CodeForFame
 */
class IOService extends Actor {

	private type F = () => Unit

	override def receive = {
		case f : F => f()
	}
}