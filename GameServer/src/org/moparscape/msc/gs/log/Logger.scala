package org.moparscape.msc.gs.log

import org.moparscape.msc.gs.connection.RSCPacket

/**
 * @author CodeForFame
 */
abstract class Logger {
	def log(p : RSCPacket)
	def log(s : String)
	def log(e : Throwable)
}