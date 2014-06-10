package org.moparscape.msc.ls.packethandler.frontend

import org.moparscape.msc.ls.packethandler.PacketHandler
import org.moparscape.msc.ls.net.Packet
import org.moparscape.msc.ls.util.{ TradeLog, ReportLog, KillLog, BanLog, LoginLog }
import org.apache.mina.common.IoSession
import org.moparscape.msc.ls.net.FPacket
import org.moparscape.msc.ls.persistence.impl.DB40StorageMedium
import org.moparscape.msc.ls.persistence.impl.DB4OStorageMedium
import org.moparscape.msc.ls.Server
import org.moparscape.msc.ls.packetbuilder.FPacketBuilder
import scala.collection.JavaConversions._
import org.moparscape.msc.ls.util.DataConversions
import scala.collection.mutable.ListBuffer
import java.util.Date
import java.text.DateFormat
import java.text.SimpleDateFormat

class ViewLog extends PacketHandler {
  
  private def longToDateString(date:Long) = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date(date * 1000))
  
  val builder = new FPacketBuilder
  @throws(classOf[Exception]) override def handlePacket(p: Packet, session: IoSession) {
    if (!Server.storage.isInstanceOf[DB4OStorageMedium]) {
      builder.setID(0)
      session.write(builder.toPacket)
      return
    }

    val args = p.asInstanceOf[FPacket].getParameters
    args(0) match {
      // Implement other log types
      case "login" => {
        builder.setID(3)
        val db = DB40StorageMedium.db.openClient
        try {
          val l = db.query(classOf[LoginLog])
          val buf = new ListBuffer[LoginLog]()
          for (i <- l) buf += i
          builder.setParameters(("login\n" + buf.toList.map(a => DataConversions.hashToUsername(a.user) + "-" + a.ip + "-" + longToDateString(a.date)).mkString("\n")).split("\n"))
        } catch {
          case e: Exception => e.printStackTrace
        } finally {
          db.close
        }
        session.write(builder.toPacket)
      }
    }
  }
}