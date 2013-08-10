package org.moparscape.msc.gs.log

import akka.actor.Actor
import org.moparscape.msc.gs.connection.RSCPacket

/**
 * @author CodeForFame
 */
case class Action(p : RSCPacket)

/**
 * @author CodeForFame
 */
class LoggingService(logger : Logger = new BatchedLogger) extends Actor {
	def receive = {
		case packet : RSCPacket => logger.log(packet)
	}
}