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

object IPBanManager extends Blocker {
  override def isBlocked(ip: String) = {
    var v = false
    if (Config.APPLICATION_LEVEL_BLOCKING)
      v = ApplicationLevelBlocking.isBlocked(ip)
    if (Config.OS_LEVEL_BLOCKING)
      v = v || OSLevelBlocking.isBlocked(ip)
    v
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

  override def block(ip: String) {
    if (ip != null && ip.length > 0) {
      if (Config.APPLICATION_LEVEL_BLOCKING)
        ApplicationLevelBlocking.block(ip)
      if (Config.OS_LEVEL_BLOCKING)
        OSLevelBlocking.block(ip)
    }

  }

  def block(ip: java.util.List[String]) {
    ip foreach { block(_) }
  }

  def block(ip: SocketAddress) {
    block(lookupIP(ip))
  }

  override def unblock(ip: String) {
    ApplicationLevelBlocking.unblock(ip)
    OSLevelBlocking.unblock(ip)
  }

  def unblock(ip: SocketAddress) {
    unblock(lookupIP(ip))
  }

  private def lookupIP(sa: SocketAddress): String = {
    if (sa != null && sa.isInstanceOf[InetSocketAddress]) {
      val a = sa.asInstanceOf[InetSocketAddress]
      return a.getAddress.getHostAddress
    }
    return null
  }
}

trait Blocker {
  def isBlocked(ip: String): Boolean
  def block(ip: String)
  def unblock(ip: String)
  def throttle(ip: String)
}

private object ApplicationLevelBlocking extends Blocker {
  private val blocked = new CopyOnWriteArrayList[String];

  private val throttled = new CopyOnWriteArrayList[String]

  private val events = Server.getServer().getEngine().getEventHandler()

  override def isBlocked(ip: String) = {
    if (blocked.contains(ip)) {
      true
    }
    false
  }

  override def block(ip: String) {
    blocked.addIfAbsent(ip)
  }

  override def unblock(ip: String) {
    blocked.remove(ip)
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
}

private object OSLevelBlocking extends Blocker {

  private val throttled = new CopyOnWriteArrayList[String]
  private val blocked = new CopyOnWriteArrayList[String]

  private val events = Server.getServer().getEngine().getEventHandler()

  override def isBlocked(ip: String) = {
    if (blocked.contains(ip)) {
      true
    }
    false
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

  override def block(ip: String) {
    Runtime.getRuntime.exec(Config.BLOCK_COMMAND.replaceAll("${ip}", ip))
    blocked addIfAbsent ip
  }

  override def unblock(ip: String) {
    try {
      Runtime.getRuntime.exec(Config.UNBLOCK_COMMAND.replaceAll("${ip}", ip))
      blocked remove ip
      throttled.remove(ip)
      Logger.println("OS - Unblocked " + ip)
    } catch {
      case e: Exception => {
        Logger.println("OS - Failed to unblock " + ip)
        Logger.error(e)
        if (Config.OS_LEVEL_UNBLOCK_FAILED_ALERT)
          AlertHandler.sendAlert("OS - Failed to unblock " + ip, 2)
      }
    }
  }
}
