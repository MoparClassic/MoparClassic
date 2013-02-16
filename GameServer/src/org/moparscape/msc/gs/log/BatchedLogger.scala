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

/**
 * @author CodeForFame
 */
class BatchedLogger extends Logger {

  private var packets = List[RSCPacket]()
  private val ignoredOpcodes = List(-1, 0, 3, 5)

  Instance.loggingSystem.scheduler.schedule(Duration(1, TimeUnit.MINUTES), Duration(1, TimeUnit.MINUTES)) {
    Instance.IOService ! write _
  }(ExecutionContexts.global)

  private def write {
    val today = Calendar.getInstance.getTime
    val data = packets.map(e => (
      if (e.isBare) "not logged in" else e.getSession.getAttachment.asInstanceOf[Player].getUsername,
      if (e.isBare) -1 else e.getID,
      e.getData.mkString(","),
      new SimpleDateFormat("hh:mm:ss:SSSS").format(e.getCreated))
    ).reverse
    val s = data.foldLeft("")((a, b) => if (ignoredOpcodes.contains(b._2)) a else a + b._4 + "	" + b._1 + "	" + b._2 + "	" + b._3 + "\n")
    val f = new File("." + File.separator + "logs" + File.separator + new SimpleDateFormat("MM.dd.yy").format(today) + File.separator + "event.log")
    if (!f.exists) {
      new File(f.getParent).mkdirs
      f.createNewFile
    }
    val out = new FileWriter(f, true)
    try out.write(s) finally out.close
  }

  override def log(p: RSCPacket) {
    packets ::= p
  }

}