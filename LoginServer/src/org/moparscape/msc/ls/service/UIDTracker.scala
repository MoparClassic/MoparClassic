package org.moparscape.msc.ls.service

import scala.collection.mutable.ListBuffer
import org.moparscape.msc.ls.util.Config

object UIDTracker {
  private val activeUIDs = ListBuffer[String]()

  def activate(uid: String) {
    activeUIDs += uid
  }

  def deactivate(uid: String) {
    activeUIDs -= uid
  }

  def isActive(uid: String) = if (Config.ALLOW_MULTILOGGING) false else activeUIDs.contains(uid)
}