package org.moparscape.msc.gs.connection.filter;

import java.util.List
import java.util.concurrent.CopyOnWriteArrayList
import org.moparscape.msc.gs.Server
import org.moparscape.msc.gs.core.DelayedEventHandler
import org.moparscape.msc.gs.event.DelayedEvent
import org.moparscape.msc.gs.util.Logger
import org.moparscape.msc.gs.config.Config
import org.moparscape.msc.gs.alert.AlertHandler
import java.net.InetSocketAddress
import java.net.SocketAddress
import scala.collection.JavaConversions._
import org.moparscape.msc.gs.db.DataService
import org.moparscape.msc.gs.db.DataManager
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.SingleEvent

object IPBanManager extends Blocker {

	private val throttled = new CopyOnWriteArrayList[String]
	private val blocked = new CopyOnWriteArrayList[String]

	override def isBlocked(ip : String) : Boolean = {
		throttled.contains(ip) || blocked.contains(ip)
	}

	override def throttle(ip : String) {
		if (throttled.addIfAbsent(ip)) {
			if (Config.OS_LEVEL_BLOCKING)
				OSLevelBlocking.block(ip)
			Logger.println("Throttled " + ip)

			Instance.getDelayedEventHandler.add(new SingleEvent(null, Config.IP_BAN_REMOVAL_DELAY) {
				override def action {
					if (throttled.remove(ip)) {
						if (Config.OS_LEVEL_BLOCKING)
							OSLevelBlocking.unblock(ip)
						Logger.println("Removed throttle on " + ip)
					}
				}
			})
		}
	}

	def throttle(ip : java.util.List[String]) {
		ip foreach { throttle(_) }
	}

	override def block(ip : String) {
		if (ip != null && ip.length > 0) {
			if (blocked.addIfAbsent(ip)) {
				DataManager.dataService.banIP(ip)
				if (Config.OS_LEVEL_BLOCKING)
					OSLevelBlocking.block(ip)
			}
		}
	}

	def block(ip : java.util.List[String]) {
		ip foreach { block(_) }
	}

	override def unblock(ip : String) {
		if (ip != null && ip.length > 0 && blocked.remove(ip)) {
			DataManager.dataService.unbanIP(ip)
			if (Config.OS_LEVEL_BLOCKING)
				OSLevelBlocking.unblock(ip)
		}
	}

	def unblock(ip : java.util.List[String]) {
		ip foreach { unblock(_) }
	}

	def reloadIPBans {
		blocked.clear
		load
	}

	private def load {
		val bans = DataManager.dataService.requestIPBans
		block(bans)
	}

	load
}

trait Blocker {
	def isBlocked(ip : String) : Boolean
	def block(ip : String)
	def unblock(ip : String)
	def throttle(ip : String)
}

private object OSLevelBlocking {

	def block(ip : String) = {
		var ret = false
		try {
			Runtime.getRuntime.exec(Config.BLOCK_COMMAND.replaceAll("\\$\\{ip\\}", ip))
			ret = true
		} catch {
			case _ : Throwable => ret = false
		}
		ret
	}

	def unblock(ip : String) = {
		try {
			Runtime.getRuntime.exec(Config.UNBLOCK_COMMAND.replaceAll("\\$\\{ip\\}", ip))
			Logger.println("OS - Unblocked " + ip)
		} catch {
			case e : Any => {
				Logger.println("OS - Failed to unblock " + ip)
				Logger.error(e)
				if (Config.OS_LEVEL_UNBLOCK_FAILED_ALERT)
					AlertHandler.sendAlert("OS - Failed to unblock " + ip, 2)
			}
		}
	}
}
