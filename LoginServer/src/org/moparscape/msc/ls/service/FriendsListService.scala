package org.moparscape.msc.ls.service

import scala.collection.mutable.ListBuffer
import org.moparscape.msc.ls.Server
import scala.collection.JavaConversions._

object FriendsListService {
  private val users = ListBuffer[(Long, Int)]()

  def logon(user: Long) {
    val myWorld = Server.getServer.findWorld(user)
    users += user -> myWorld.getID
    getViewers(user) foreach {
      u =>
        Server.getServer.getWorld(u._2).getActionSender.friendLogin(user, u._1, myWorld.getID)
    }
  }

  def logoff(user: Long) {
    users -= user -> Server.getServer.findWorld(user).getID
    getViewers(user) foreach {
      u =>
        Server.getServer.getWorld(u._2).getActionSender.friendLogout(user)
    }
  }

  def getFriendWorld(user: Long, other: Long) = users.find(_._1 == other) match {
    case Some(x) => if (canSee(other, user)) x._2 else -1
    case None => 0
  }

  def turnOffPrivate(user: Long) {
    val myWorld = Server.getServer.findWorld(user)
    myWorld.getSave(user).setPrivacySetting(1, false)
    getViewers(user).foreach(f => Server.getServer.getWorld(f._2).getActionSender.friendLogin(user, f._1, myWorld.getID))
  }

  def turnOnPrivate(user: Long) {
    val viewers = getViewers(user)
    Server.getServer.findWorld(user).getSave(user).setPrivacySetting(1, true)
    viewers.diff(getViewers(user)).foreach(f => Server.getServer.getWorld(f._2).getActionSender().friendLogout(user, f._1))
  }

  def canSee(user: Long, other: Long) = getViewers(other).map(_._1).contains(user)

  private def getViewers(user: Long) = {
    val notify = users.filterNot(u => user == u._1)
    val userWorld = Server.getServer.findWorld(user).getID
    val p = Server.getServer.getWorld(userWorld).getSave(user)

    try {
      excludePrivateBlocked
      excludeIgnored
    } catch {
      case e: Exception =>
    }

    def excludePrivateBlocked {
      notify --= notify.filter {
        u =>
          if (p.blockPrivate && !p.friendList.contains(u._1)) true
          else false
      }
    }

    def excludeIgnored {
      notify --= notify.filter {
        u =>
          if (p.ignoreList.contains(u._1)) true
          else false
      }
    }
    notify.toList
  }
}