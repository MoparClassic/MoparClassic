package org.moparscape.msc.gs.util

import java.security.MessageDigest

class Hash(data: Array[Byte]) {
  lazy val value = hash(data)

  private def hash(data: Array[Byte]) = {
    try {
      val md = MessageDigest.getInstance("SHA-512")
      md.update(data)
      md.digest
    } catch {
      case e: Throwable => {
        e.printStackTrace
        data
      }
    }
  }
}