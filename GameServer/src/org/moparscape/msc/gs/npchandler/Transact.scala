package org.moparscape.msc.gs.npchandler

import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.{ Npc, Player }

class Transact(msg : String, give : Array[(Int, Int)],
		receive : Array[(Int, Int)], failMsg : Array[String],
		_npc : Npc, _player : Player) extends NpcDialog(msg, _npc, _player) {

	protected var failed = false

	override def begin {
		if (hasItems(give)) {
			give foreach (i => player.getInventory.removeAmount(i._1, i._2))
			receive foreach (i => player.getInventory.addAmount(i._1, i._2))
			breath
			player.getActionSender.sendInventory
			success
		} else {
			failMsg foreach { e => this < e; breath }
			failed = true
			fail
		}
	}

	protected def success {}

	protected def fail {}

	private def hasItems(items : Array[(Int, Int)]) = {
		items find {
			item =>
				if (_player.getInventory.countId(item._1) >= item._2) false
				else true
		} match {
			case Some(x) => false
			case None => true
		}
	}

}