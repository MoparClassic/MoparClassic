package org.moparscape.msc.ls.persistence.impl

import org.moparscape.msc.ls.persistence.StorageMedium
import org.moparscape.msc.ls.model.PlayerSave
import org.moparscape.msc.ls.util.Config
import org.moparscape.msc.ls.util.{ TradeLog, ReportLog, KillLog, BanLog, LoginLog }
import java.util.Random
import com.db4o.Db4oEmbedded
import com.db4o.query.Predicate
import scala.language.implicitConversions
import com.db4o.cs.Db4oClientServer
import com.db4o.ObjectContainer

object DB40StorageMedium {
  val db = Db4oClientServer.openServer(Db4oClientServer
    .newServerConfiguration(), "MoparClassicDB.db4o", 0)
  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run {
      db.close
    }
  })
}

class DB4OStorageMedium extends StorageMedium {
  implicit def toPredicate[T](a: (T) => Boolean): Predicate[T] = new Predicate[T]() {
    override def `match`(t: T) = a(t)
  }

  implicit def toPlayer(db: (ObjectContainer, Long)): PlayerSave = db._1.query[PlayerSave]((p: PlayerSave) => p.getUser == db._2).next

  val db = DB40StorageMedium.db.openClient

  override def savePlayer(s: PlayerSave) = {
    db.store(s)
    db.commit()
    true
  }

  override def shutdown {
    db.close
  }

  override def logTrade(from: Long, to: Long, item: Int, amount: Long, x: Int, y: Int, t: Int, date: Long) {
    db.store(new TradeLog(from, to, item, amount, x, y, t, date))
  }
  override def logReport(user: Long, reported: Long, reason: Byte, x: Int, y: Int, status: String) {
    db.store(new ReportLog(user, reported, reason, x, y, status))
  }
  override def logKill(user: Long, killed: Long, t: Byte) {
    db.store(new KillLog(user, killed, t))
  }
  override def logBan(user: Long, mod: Long) {
    db.store(new BanLog(user, mod))
  }
  override def logLogin(user: Long, ip: String) {
    db.store(new LoginLog(user, ip))
  }

  override def addFriend(user: Long, friend: Long) {}
  override def removeFriend(user: Long, friend: Long) {}

  override def addIgnore(user: Long, ignored: Long) {}
  override def removeIgnore(user: Long, unignored: Long) {}

  override def playerExists(user: Long) = db.query((p: PlayerSave) => p.getUser == user).hasNext
  override def isBanned(user: Long) = db.query((p: PlayerSave) => p.getUser == user && p.banned).hasNext

  override def getGroupID(user: Long) = (db, user).getGroup
  override def getOwner(user: Long) = new Random().nextInt(Integer.MAX_VALUE)
  override def ban(banned: Boolean, user: Long) = {
    val p: PlayerSave = (db, user)
    p.banned = true
    savePlayer(p)
  }
  override def loadPlayer(user: Long) = (db, user)

  override def logIn(ip: String, user: Long) {}
  override def getPass(user: Long) = (db, user).pass

  override def registerPlayer(user: Long, pass: Array[Byte], identifier: String) = {
    val save = new PlayerSave(user)
    save.setLocation(498, 522)
    save.setAppearance(2, 8, 14, 0, 1, 2, true, 0)
    save.pass = pass
    save.identifier = identifier

    val exp = Array.fill(Config.statArray.length)(0)
    val stats = Array.fill(Config.statArray.length)(1)

    exp(3) = 1154
    save.setExp(exp)
    stats(3) = 10
    save.setCurStats(stats)

    save.setOwner(0, 1, 0)

    save
  }
}