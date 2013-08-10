package org.moparscape.msc.ls.service

import scala.collection.mutable.ListBuffer
import org.moparscape.msc.ls.Server
import scala.collection.JavaConversions._

object FriendsListService {
	private val users = ListBuffer[(Long, Int)]()

	def logon(user : Long) {
		val myWorld = Server.getServer.findWorld(user)
		getViewers(user) foreach {
			u =>
				Server.getServer.getWorld(u._2).getActionSender.friendLogin(u._1, user, myWorld.getID)
		}
		users += user -> myWorld.getID
	}

	def logoff(user : Long) {
		users -= user -> Server.getServer.findWorld(user).getID
		getViewers(user) foreach {
			u =>
				Server.getServer.getWorld(u._2).getActionSender().friendLogout(user)
		}
	}

	def getFriendWorld(user : Long, other : Long) = users.find(_._1 == other) match {
		case Some(x) if isVisible(user, other) => x._2
		case _ => 0
	}

	def turnOffPrivate(user : Long) {
		val viewers = getViewers(user)
		val myWorld = Server.getServer.findWorld(user)
		myWorld.getSave(user).setPrivacySetting(1, false)
		getViewers(user).diff(viewers).foreach(f => Server.getServer.getWorld(f._2).getActionSender.friendLogin(f._1, user, myWorld.getID))
	}

	def turnOnPrivate(user : Long) {
		val viewers = getViewers(user)
		Server.getServer.findWorld(user).getSave(user).setPrivacySetting(1, true)
		println(viewers.diff(getViewers(user)).mkString(" - "))
		viewers.diff(getViewers(user)).foreach(f => Server.getServer.getWorld(f._2).getActionSender().friendLogout(user, f._1))
	}

	def isVisible(user : Long, other : Long) = getViewers(user).map(_._1).contains(other)

	private def getViewers(user : Long) = {
		val notify = users.map(x => x)
		try {
			notify -= user -> Server.getServer.findWorld(user).getID
			val p = Server.getServer.findWorld(user).getSave(user)
			notify --= users.filter(u => p.ignoreList.contains(u._1))
			if (p.blockPrivate) {
				val intersection = notify.map(_._1).intersect(p.friendList)
				notify --= users.filterNot(x => intersection.contains(x._1))
				println(notify.mkString(", "))
			}
		} catch {
			case _ : Throwable =>
		}
		notify.toList
	}
}