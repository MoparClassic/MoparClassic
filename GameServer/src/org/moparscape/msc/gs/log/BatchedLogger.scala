package org.moparscape.msc.gs.log

import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.connection.RSCPacket
import org.moparscape.msc.gs.model.Player
import akka.dispatch.ExecutionContexts
import scala.concurrent.duration.Duration
import org.moparscape.msc.gs.config.Config

/**
 * @author CodeForFame
 */
class BatchedLogger extends Logger {

	private var packets = List[RSCPacket]()
	private var strings = List[String]()
	private var exceptions = List[Throwable]()
	Instance.loggingSystem.scheduler.schedule(Duration(Config.BATCH_LOG_INTERVAL, TimeUnit.MINUTES), Duration(Config.BATCH_LOG_INTERVAL, TimeUnit.MINUTES)) {
		Instance.IOService ! writeData _
	}(ExecutionContexts.global)

	private def dir = {
		val today = Calendar.getInstance.getTime
		(new SimpleDateFormat("yy.MM.dd").format(today).replace(".", File.separator)) + File.separator
	}

	def writeData {
		writePackets
		writeStrings
		writeErrors
	}

	private def writePackets {
		val ignoredOpcodes = List(-1, 0, 3, 5)

		val data = packets.map(e => (
			if (e.isBare) "not logged in" else e.getSession.getAttachment.asInstanceOf[Player].getUsername,
			if (e.isBare) -1 else e.getID,
			e.getData.mkString(","),
			new SimpleDateFormat("hh:mm:ss:SSSS").format(e.getCreated))).reverse
		val users = data.map(_._1).filterNot(null == _).distinct
		users.foreach {
			u =>
				val s = data.filter(_._1 == u).foldLeft("")((a, b) => if (ignoredOpcodes.contains(b._2)) a else a + b._4 + "	" + b._2 + "	" + b._3 + "\n")
				write(dir + u + File.separator + "raw.log", s)
		}
		packets = List()
	}

	def writeStrings {
		val string = strings.reverse.mkString("\n")
		if (string.length > 0) {
			write(dir + "strings.log", string + '\n')
			strings = List()
		}
	}

	def writeErrors {
		val string = ("" /: exceptions.reverse)(_ + _ + "\n")
		if (string.length > 0) {
			write(dir + "errors.log", string + '\n')
			exceptions = List()
		}
	}

	private def write(fileName : String, data : String) {
		val f = new File(Config.LOG_DIR, fileName)
		if (!f.exists) {
			new File(f.getParent).mkdirs
			f.createNewFile
		}
		val out = new FileWriter(f, true)
		try out.write(data) finally out.close

	}

	override def log(p : RSCPacket) {
		packets ::= p
	}

	override def log(s : String) {
		println(s)
		strings ::= s
	}

	override def log(e : Throwable) {
		println(e)
		exceptions ::= e
	}

}