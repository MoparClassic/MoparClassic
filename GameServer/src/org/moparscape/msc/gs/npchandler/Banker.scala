package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.{ Npc, Player }
import org.moparscape.msc.gs.model.dialog.NpcDialog

class Banker extends NpcDialog {

	override def init {
		this + new GenericEnd("I'd like to access my bank account please", npc, player) {
			override def begin {
				this > ("Certainly " + (if (player.isMale()) "sir" else "miss")); breath
				player.setAccessingBank(true)
				player.getActionSender.showBank
				super.begin
			}
		}
		this + new Question
	}

	override def begin {
		this > "Good day, how may I help you?"
		end
	}
	
	private class Question extends NpcDialog("What is this place?", npc, player) {
		
		override def init {
			this + new GenericEnd("And what do you do?", npc, player) {
				override def begin {
					this > "We will look after your items and money for you"; breath
					this > "So leave your valuables with us if you want to keep them safe"
					super.begin
				}
			}
			
			this + new GenericEnd("Didn't you used to be called the bank of Varrock", npc, player) {
				override def begin {
					this > "Yes we did but people kept coming into our branches outside Varrock"; breath
					this > "And telling us our signs were wrong"; breath
					this > "As if we didn't know what town we were in or something!"
					super.begin
				}
			}
		}
		
		override def begin {
			this > "This is a branch of the bank of Runescape"; breath
			this > "We have branches in many towns"; breath
			end
		}
	}

}