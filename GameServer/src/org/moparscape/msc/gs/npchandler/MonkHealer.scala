package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.{ Npc, Player }

class MonkHealer(_npc : Npc, _player : Player) extends GenericEnd("", _npc, _player) {

	override def begin {
		this > "Greetings traveller"; breath; breath
		this < "Can you heal me? I'm injured."; breath
		this > "Ok"; breath
		this >> "The monk places his hands on you head"; breath

		var newHp = player.getCurStat(3) + 10;
		if (newHp > player.getMaxStat(3)) {
			newHp = player.getMaxStat(3)
		}
		player.setCurStat(3, newHp)
		player.getActionSender.sendStat(3)
		this >> "You feel a little better."

		super.begin
	}

}