package org.moparscape.msc.gs.connection.filter;

import java.util.List
import java.util.concurrent.CopyOnWriteArrayList
import org.moparscape.msc.gs.Server
import org.moparscape.msc.gs.core.DelayedEventHandler
import org.moparscape.msc.gs.event.DelayedEvent
import org.moparscape.msc.gs.util.Logger
import org.moparscape.msc.config.Config
import org.moparscape.msc.gs.alert.AlertHandler

object OSLevelBlocking {

  private val blocked = new CopyOnWriteArrayList[String];

  private val events = Server.getServer().getEngine().getEventHandler()

  def block(ip: String) {
    if (!blocked.contains(ip)) {
      events.add(new DelayedEvent(null, Config.IP_BAN_REMOVAL_DELAY) {

        def run() {
          try {
            Runtime.getRuntime.exec(Config.UNBLOCK_COMMAND.replaceAll("${ip}", ip));
            blocked.remove(ip)
            Logger.println("Unblocked " + ip)
          } catch {
            case e: Exception => {
              Logger.error(e)
              Logger.println("Failed to unblock " + ip)
              AlertHandler.sendAlert("Failed to unblock " + ip, 2);
            }
          }
        }
      })
      Runtime.getRuntime.exec(Config.BLOCK_COMMAND.replaceAll("${ip}", ip));
      blocked.add(ip)
      AlertHandler.sendAlert("Blocked " + ip, blocked.size / 10);
      Logger.println("Blocked " + ip)
    }
  }
}
