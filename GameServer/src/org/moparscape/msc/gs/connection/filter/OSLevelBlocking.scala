package org.moparscape.msc.gs.connection.filter;

import java.util.List
import java.util.concurrent.CopyOnWriteArrayList
import org.moparscape.msc.gs.Server
import org.moparscape.msc.gs.core.DelayedEventHandler
import org.moparscape.msc.gs.event.DelayedEvent
import org.moparscape.msc.gs.util.Logger;
import org.moparscape.msc.config.Config

object OSLevelBlocking {

  private val block_ = {
    println(System.getProperty("os.name"))
    if (System.getProperty("os.name") startsWith "(?i)linux") {
      def b(ip: String) {
        Runtime.getRuntime().exec("sudo route add -host " + ip + " reject")
      }
      b _
    } else {
      // Windows blocking - untested - won't work on Windows 7
      def b(ip: String) {
        Runtime.getRuntime().exec("route ADD " + ip + " MASK 255.255.255.255 " + Config.UNUSED_IP)
      }
      b _
    }
  }
  private val unblock_ = {
    if (System.getProperty("os.name") startsWith "(?i)linux") {
      def u(ip: String) {
        Runtime.getRuntime().exec("sudo route del " + ip + " reject")
      }
      u _
    } else {
      // Windows blocking - untested - won't work on Windows 7
      def b(ip: String) {
        Runtime.getRuntime().exec("route DELETE " + ip)
      }
      b _
    }
  }

  private val blocked = new CopyOnWriteArrayList[String];

  private val events = Server.getServer().getEngine().getEventHandler()

  def block(ip: String) {
    if (!blocked.contains(ip)) {
      events.add(new DelayedEvent(null, Config.IP_BAN_REMOVAL_DELAY) {

        def run() {
          unblock(ip)

        }
      })
      block_(ip)
      blocked.add(ip)
      Logger.println("Blocked " + ip)
    }
  }

  def unblock(ip: String) {
    try {
      unblock_(ip)
      blocked.remove(ip)
      Logger.println("Unblocked " + ip)
    } catch {
      case e: Exception => {
        Logger.error(e)
        Logger.println("Failed to unblock " + ip)
      }
    }
  }
}
