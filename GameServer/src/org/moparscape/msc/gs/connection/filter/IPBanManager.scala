package org.moparscape.msc.gs.connection.filter;

import java.util.List
import java.util.concurrent.CopyOnWriteArrayList
import org.moparscape.msc.gs.Server
import org.moparscape.msc.gs.core.DelayedEventHandler
import org.moparscape.msc.gs.event.DelayedEvent
import org.moparscape.msc.gs.util.Logger
import org.moparscape.msc.config.Config
import org.moparscape.msc.gs.alert.AlertHandler
import java.net.InetSocketAddress
import java.net.SocketAddress
import scala.collection.JavaConversions._
import org.moparscape.msc.gs.db.DataRequestHandler

object IPBanManager extends Blocker {

  override def isBlocked(ip: String): Boolean = {
    if (Config.APPLICATION_LEVEL_BLOCKING && ApplicationLevelBlocking.isBlocked(ip))
      return true
    if (Config.OS_LEVEL_BLOCKING && OSLevelBlocking.isBlocked(ip))
      return true
    return false
  }

  def isBlocked(ip: SocketAddress): Boolean = {
    isBlocked(lookupIP(ip))
  }

  override def throttle(ip: String) {
    if (Config.APPLICATION_LEVEL_BLOCKING)
      ApplicationLevelBlocking.throttle(ip)
    if (Config.OS_LEVEL_BLOCKING)
      OSLevelBlocking.throttle(ip)
  }

  def throttle(ip: java.util.List[String]) {
    ip foreach { throttle(_) }
  }

  def throttle(ip: SocketAddress) {
    throttle(lookupIP(ip))
  }

  override def block(ip: String) = {
    var ret = false
    if (ip != null && ip.length > 0) {
      if (Config.APPLICATION_LEVEL_BLOCKING)
        ret = ApplicationLevelBlocking.block(ip)
      if (Config.OS_LEVEL_BLOCKING)
        ret = ret || OSLevelBlocking.block(ip)
    }
    ret
  }

  def block(ip: java.util.List[String]) {
    ip foreach { block(_) }
  }

  def block(ip: SocketAddress): Boolean = {
    block(lookupIP(ip))
  }

  override def unblock(ip: String) = {
    var ret = false
    if (ip != null && ip.length > 0) {
      ret = ApplicationLevelBlocking.unblock(ip)
      ret = ret || OSLevelBlocking.unblock(ip)
    }
    ret
  }

  def unblock(ip: SocketAddress): Boolean = {
    unblock(lookupIP(ip))
  }

  private def lookupIP(sa: SocketAddress): String = {
    if (sa != null && sa.isInstanceOf[InetSocketAddress]) {
      val a = sa.asInstanceOf[InetSocketAddress]
      return a.getAddress.getHostAddress
    }
    return null
  }

  def reloadIPBans {
    load
  }

  private def load {
    block(DataRequestHandler.requestIPBans)
  }

  load
}

trait Blocker {
  def isBlocked(ip: String): Boolean
  def block(ip: String): Boolean
  def unblock(ip: String): Boolean
  def throttle(ip: String)
}

private object ApplicationLevelBlocking extends Blocker {
  import org.moparscape.msc.gs.model.World
  import java.sql.PreparedStatement
  import java.sql.SQLException

  private val blocked = new CopyOnWriteArrayList[String];

  private val throttled = new CopyOnWriteArrayList[String]

  private val events = Server.getServer().getEngine().getEventHandler()

  override def isBlocked(ip: String) = {
    blocked.contains(ip) || throttled.contains(ip)
  }

  override def block(ip: String) = {
    var ret = false
    try {
      block.setString(1, ip)
      block.executeUpdate
      blocked.addIfAbsent(ip)
      ret = true
    } catch {
      case e: SQLException => {
        if (!e.getMessage.startsWith("Duplicate entry")) {
          blocked.remove(ip)
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

  override def unblock(ip: String) = {
    val removed = blocked.remove(ip)
    if (removed) {
      unblock.setString(1, ip)
      unblock.executeUpdate
    }
    removed
  }

  override def throttle(ip: String) {
    if (!throttled.contains(ip)) {
      events.add(new DelayedEvent(null, Config.IP_BAN_REMOVAL_DELAY) {

        override def run() {
          unblock(ip)
          throttled.remove(ip)
          Logger.println("Application - Unblocked " + ip)
        }
      })
      block(ip)
      throttled.add(ip)
      if (Config.APPLICATION_LEVEL_THROTTLE_ALERT)
        AlertHandler.sendAlert("Application - Throttled " + ip, throttled.size / 10)
      Logger.println("Application - Throttled " + ip)
    }
  }

  val block: PreparedStatement = {
    val conn = World.getWorld.getDB.getConnection
    conn.prepareStatement("INSERT INTO `pk_ipbans` (`ip`) VALUES(?)")
  }

  val unblock: PreparedStatement = {
    val conn = World.getWorld.getDB.getConnection
    conn.prepareStatement("DELETE FROM `pk_ipbans` WHERE ip = ?")
  }

}

private object OSLevelBlocking extends Blocker {

  private val throttled = new CopyOnWriteArrayList[String]
  private val blocked = new CopyOnWriteArrayList[String]

  private val events = Server.getServer().getEngine().getEventHandler()

  override def isBlocked(ip: String) = {
    blocked.contains(ip) || throttled.contains(ip)
  }

  override def throttle(ip: String) {
    if (!throttled.contains(ip)) {
      events.add(new DelayedEvent(null, Config.IP_BAN_REMOVAL_DELAY) {

        override def run() {
          unblock(ip)
          throttled.remove(ip)
        }
      })
      block(ip)
      throttled.add(ip)
      if (Config.OS_LEVEL_THROTTLE_ALERT)
        AlertHandler.sendAlert("OS - Throttled " + ip, throttled.size / 10)
      Logger.println("OS - Throttled " + ip)
    }
  }

  override def block(ip: String) = {
    var ret = false
    try {
      Runtime.getRuntime.exec(Config.BLOCK_COMMAND.replaceAll("\\$\\{ip\\}", ip))
      ret = true
    } catch {
      case _ => ret = false
    }
    ret
  }

  override def unblock(ip: String) = {
    var ret = false
    try {
      Runtime.getRuntime.exec(Config.UNBLOCK_COMMAND.replaceAll("\\$\\{ip\\}", ip))
      blocked remove ip
      throttled.remove(ip)
      Logger.println("OS - Unblocked " + ip)
      ret = true
    } catch {
      case e: Any => {
        Logger.println("OS - Failed to unblock " + ip)
        Logger.error(e)
        if (Config.OS_LEVEL_UNBLOCK_FAILED_ALERT)
          AlertHandler.sendAlert("OS - Failed to unblock " + ip, 2)
        ret = false
      }
    }
    ret
  }
}
