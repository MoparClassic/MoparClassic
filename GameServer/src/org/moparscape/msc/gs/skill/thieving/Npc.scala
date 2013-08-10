package org.moparscape.msc.gs.skill.thieving

import org.moparscape.msc.gs.model.Player
import org.moparscape.msc.gs.util.Logger
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.WalkToMobEvent
import org.moparscape.msc.gs.model.Bubble
import org.moparscape.msc.gs.event.ShortEvent
import org.moparscape.msc.gs.model.ChatMessage
import org.moparscape.msc.gs.event.MiniEvent
import org.moparscape.msc.gs.states.Action
import scala.collection.JavaConversions._
import org.moparscape.msc.gs.event.FightEvent

class Npc(p : Player, n : org.moparscape.msc.gs.model.Npc) extends Thieving(p, null) {

	private val caught = List(
		"Oi! Get your hands out of there $name!",
		"Hey thief! get here!",
		"Trying to steal from me hmm?",
		"No one steals from me!",
		"Take those hands off me Thief",
		"Are you trying to steal from me $name?",
		"Dont you dare touch me",
		"Thief get back here now!",
		"Stealing won't get you anywhere",
		"Bad person! you shall die",
		"Die evil thief!",
		"You are going to pay for that",
		"Ill make you wish you were never born",
		"$name i am going to hurt you",
		"$name dont steal from me again",
		"Remove your filthy hands off me",
		"A real man doesn't need to steal")

	private val npc = new Array[Int](6).map(e => -1)

	def pickpocket {
		try {

			n.getID match {
				case 11 | 318 => set(8, 0, g(3))
				case 63 => set(12, 10, g(9))
				case 159 | 320 => set(26, 25, g(18))
				case 342 => set(36, 32, getRand(0))
				case 65 | 100 | 321 => set(46, 40, g(30))
				case 322 => set(85, 55, g(50))
				case 574 | 685 => set(138, 65, Array(10, 60, 138, 1))
				case 323 => set(152, 70, Array(10, 80, 41, 1))
				case 580 | 581 | 582 | 583 => set(198, 75, getRand(1))
				case _ => {
					this > "Sorry, this npc has not been added to the pickpocketing list yet."
					Logger.println("Player " + p.getUsername + " found a NPC (pickpocket) not added, ID: " + n.getID)
					release
					return
				}
			}

			p.setFollowing(n)

			Instance.getDelayedEventHandler.add(new WalkToMobEvent(p, n, 1) {
				def arrived {
					if (p.isPacketSpam) return

					if (n == null || n.inCombat || p.isBusy) {
						release
						p.resetPath
						return
					}

					if (p == null) {
						release
						return
					}

					if (!p.nextTo(n)) {
						release
						return
					}

					p.setSpam(true)
					p.setBusy(true)

					if (p.getCurStat(17) < npc(1)) {
						Npc.this > "You must be at least " + npc(1) + " thieving to pick the " + n.getDef.name + "'s pocket."
						release
						return
					}

					n.resetPath
					new Bubble(p, 16).broadcast
					Npc.this > "You attempt to pick the " + n.getDef.name + "'s pocket..."

					Instance.getDelayedEventHandler.add(new ShortEvent(p) {
						def action {
							p.setBusy(false)
							n.setBusy(false)
							if (!chanceFormula(npc(1))) {
								p.setSpam(false)
								Npc.this > "You fail to pick the " + n.getDef.name + "'s pocket."

								val r = rand.nextInt(10)
								if (r >= 3) {
									release
									return
								}

								n.resetPath
								p.setBusy(true)

								if (n == null || n.inCombat) {
									release
									return
								}

								if (p == null) {
									release
									return
								}

								val msg = caught(rand.nextInt(caught.length)).replaceAll("$name", p.getUsername)
								p.informOfNpcMessage(new ChatMessage(n, msg, p))

								Instance.getDelayedEventHandler.add(new MiniEvent(p, 1000) {
									def action {
										if (n == null || n.inCombat) {
											release
											return
										}

										if (p == null) {
											release
											return
										}

										p.setBusy(false)
										n.resetPath
										p.resetAll
										p.getActionSender.sendSound("underattack")
										p.setStatus(Action.FIGHTING_MOB)
										Npc.this > "You are under attack!"
										n.setLocation(p.getLocation, true)
										n.resetPath
										n.setBusy(true)
										n.getViewArea.getPlayersInView.foreach(_.removeWatchedNpc(n))
										p.setBusy(true)
										p.setSprite(9)
										p.setOpponent(n)
										p.setCombatTimer
										n.setSprite(8)
										n.setOpponent(p)
										n.setCombatTimer
										val fightEvent = new FightEvent(p, n, true)
										fightEvent.setLastRun(0)
										Instance.getDelayedEventHandler.add(fightEvent)
									}
								})
							} else {
								p.setSpam(false)
								Npc.this > "You successfully stole from the " + n.getDef.name
								npc.drop(2).filterNot(-1 == _).sliding(2).foreach(a => p.getInventory.add(a(0), a(1), false))
								p.getActionSender.sendInventory
								p.incExp(17, npc(0), true)
								p.getActionSender.sendStat(17)
								release
							}
						}
					})
				}
			})

		} catch {
			case e : Throwable => {
				release
				e.printStackTrace
			}
		}
	}

	override protected def release {
		try super.release
		try {
			n.setBusy(false)
			n.unblock
		}
	}

	private def g(amt : Int) = Array(10, amt)

	private def set(exp : Int, lvl : Int, loot : Array[Int]) {
		npc(0) = exp
		npc(1) = lvl

		for (i <- 2 until loot.length + 2) {
			npc(i) = loot(i - 2)
		}
	}

	private def getRand(t : Int) = {
		val r = rand.nextInt(100)
		if (t == 0) { // Rogue
			if (r < 20) Array(10, rand.nextInt(20) + 20)
			else if (r < 40) Array(33, 8)
			else if (r < 60) Array(714, 1)
			else if (r < 80) Array(559, 1)
			else Array(142, 1)
		} else if (t == 1) { // Gnome
			if (r < 20) Array(10, rand.nextInt(100) + 200)
			else if (r < 40) Array(34, rand.nextInt(5) + 1)
			else if (r < 50) Array(612, 1)
			else if (r < 60) Array(152, 1)
			else if (r < 80) Array(895, 1)
			else Array(897, 1)
		} else if (t == 2) { // Hero
			if (r <= 5) Array(161, 1)
			else if (r < 15) Array(619, 1)
			else if (r < 30) Array(38, 2)
			else if (r < 40) Array(152, 1)
			else if (r < 50) Array(612, 1)
			else if (r < 60) Array(142, 1)
			else Array(10, rand.nextInt(100) + 200)
		} else Array(-1, -1)
	}
}