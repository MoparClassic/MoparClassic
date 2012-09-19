package org.moparscape.msc.gs.db
import org.moparscape.msc.gs.model.World
import scala.collection.mutable.ListBuffer

object DataRequestHandler {

  def requestIPBans: List[String] = {
    val query = "SELECT `ip` from `pk_ipbans`"
    val db = World.getWorld.getDB
    val result = db.getQuery(query)
    val list = new ListBuffer[String]
    while (result.next) {
      list += result.getString("ip")
    }
    list.toList
  }
}