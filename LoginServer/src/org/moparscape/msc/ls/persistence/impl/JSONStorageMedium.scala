package org.moparscape.msc.ls.persistence.impl

import org.moparscape.msc.ls.persistence.StorageMedium
import org.moparscape.msc.ls.model.PlayerSave
import scala.collection.JavaConversions._
import com.google.gson.GsonBuilder
import java.lang.reflect.Modifier
import java.io.File
import java.io.FileWriter
import org.moparscape.msc.ls.util.DataConversions
import java.io.FileReader
import java.io.FileNotFoundException
import org.moparscape.msc.ls.util.Config
import java.util.Random

class JSONStorageMedium extends StorageMedium {

  private val gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
    .generateNonExecutableJson.create

  private val playerDir = new File("players")
  if (!playerDir.exists) playerDir.mkdirs

  override def savePlayer(s: PlayerSave) = {
    try {
      val fw = new FileWriter(new File(playerDir, s.getUsername + ".json"))
      fw.write(gson.toJson(s))
      fw.close
      true
    } catch {
      case e: Throwable => { e.printStackTrace; false }
    }
  }

  override def shutdown {}

  override def logTrade(from: Long, to: Long, item: Int, amount: Long, x: Int, y: Int, t: Int, date: Long) {}
  override def logReport(user: Long, reported: Long, reason: Byte, x: Int, y: Int, status: String) {}
  override def logKill(user: Long, killed: Long, t: Byte) {}
  override def logBan(user: Long, mod: Long) {}
  override def logLogin(user: Long, ip: String) {}

  override def addFriend(user: Long, friend: Long) {}
  override def removeFriend(user: Long, friend: Long) {}

  override def addIgnore(user: Long, ignored: Long) {}
  override def removeIgnore(user: Long, unignored: Long) {}

  override def playerExists(user: Long) = file(user).exists
  override def isBanned(user: Long) = loadPlayer(user).banned

  override def getGroupID(user: Long) = loadPlayer(user).getGroup
  override def getOwner(user: Long) = new Random().nextInt(Integer.MAX_VALUE)
  override def ban(banned: Boolean, user: Long) = {
    val p = loadPlayer(user)
    p.banned = banned
    savePlayer(p)
  }

  override def loadPlayer(user: Long) = {
    try {
      gson.fromJson(new FileReader(file(user)), classOf[PlayerSave])
    } catch {
      case e: FileNotFoundException => {
        val save = new PlayerSave(user)
        save.setLocation(498, 522)
        save.setAppearance(2, 8, 14, 0, 1, 2, true, 0)

        val exp = Array.fill(Config.statArray.length)(0)
        val stats = Array.fill(Config.statArray.length)(1)

        exp(3) = 1154
        save.setExp(exp)
        stats(3) = 10
        save.setCurStats(stats)

        // //////////////////////////////////
        // //////// For Alpha Only //////////
        // //////////////////////////////////
        save.setOwner(0, 11, 0)
        // //////////////////////////////////
        // ////// End For Alpha Only ////////
        // //////////////////////////////////

        save
      }
      case _: Throwable => null
    }
  }
  override def logIn(ip: String, user: Long) {}
  override def getPass(user: Long) = loadPlayer(user).pass

  private def file(user: Long) = new File(playerDir, DataConversions.hashToUsername(user) + ".json")
}