package org.moparscape.msc.gs.db.impl

import org.moparscape.msc.gs.db.DataService
import scala.collection.mutable.ListBuffer
import java.util.List
import collection.JavaConversions._
import org.moparscape.msc.gs.model.World
import java.sql.PreparedStatement
import java.sql.SQLException
import org.moparscape.msc.gs.util.Logger
class MySQLDataService protected() extends DataService {

  private val conn = new DBConnection

  override def banIP(ip: String) = {
    var ret = false
    try {
      block.setString(1, ip)
      block.executeUpdate
      block.close
      ret = true
    } catch {
      case e: SQLException => {
        if (!e.getMessage.startsWith("Duplicate entry")) {
          ret = false
        }
      }
      case e => {
        Logger.error(e)
        ret = false
      }
    }
    ret
  }

  override def unbanIP(ip: String) {
    try {
      unblock.setString(1, ip)
      unblock.executeUpdate
      unblock.close
    } catch {
      case _ =>
    }
  }

  override def requestIPBans: List[String] = {
    val query = "SELECT `ip` from `pk_ipbans`"
    val result = conn.getQuery(query)
    val list = new ListBuffer[String]
    while (result.next) {
      list += result.getString("ip")
    }
    list.toList
  }

  val block: PreparedStatement = {
    conn.getConnection.prepareStatement("INSERT INTO `pk_ipbans` (`ip`) VALUES(?)")
  }

  val unblock: PreparedStatement = {
    conn.getConnection.prepareStatement("DELETE FROM `pk_ipbans` WHERE ip = ?")
  }
}