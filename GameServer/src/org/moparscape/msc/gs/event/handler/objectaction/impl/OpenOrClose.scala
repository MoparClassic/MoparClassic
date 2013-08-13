package org.moparscape.msc.gs.event.handler.objectaction.impl

import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.event.EventHandler
import org.moparscape.msc.gs.model.{ GameObject, ChatMessage }
import org.moparscape.msc.gs.Instance

class OpenOrClose extends ObjectEvent with MembersOnly {

	override def fire = {
		if (command == "open" || command == "close") {
			o.getID match {
				case 18 => replaceDoor(17, true)

				case 17 => replaceDoor(18, false)

				case 58 => replaceDoor(57, false)

				case 57 => replaceDoor(58, true)

				case 63 => replaceDoor(64, false)

				case 64 => replaceDoor(63, true)

				case 79 => replaceDoor(78, false)

				case 78 => replaceDoor(79, true)

				case 60 => replaceDoor(59, true)

				case 59 => replaceDoor(60, false)

				// Gasmask Cupboard 
				case 452 => replaceGameObject(451)
				case 451 => replaceGameObject(452)

				// Members Gate (Doriks)
				case 137 => {
					if (isAt(341, 487) && p2pCheck(player)) {
						doGate
						if (player.getX <= 341) {
							player.teleport(342, 487, false)
						} else {
							player.teleport(341, 487, false)
						}
					}
				}
				// Members Gate (Crafting Guild)
				case 138 => {
					if (isAt(343, 581) && p2pCheck(player)) {
						doGate
						if (player.getY <= 580) {
							player.teleport(343, 581, false)
						} else {
							player.teleport(343, 580, false)
						}
					}
				}

				// Al-Kharid Gate
				case 180 => {
					if (isAt(92, 649)) {
						doGate
						if (player.getX <= 91) {
							player.teleport(92, 649, false)
						} else {
							player.teleport(91, 649, false)
						}
					}
				}
				// Karamja Gate
				case 254 => {
					if (isAt(434, 682) && p2pCheck(player)) {
						doGate
						if (player.getX <= 434) {
							player.teleport(435, 682, false)
						} else {
							player.teleport(434, 682, false)
						}
					}
				}
				// King Lanthlas Gate
				case 563 => {
					if (isAt(660, 551)) {
						doGate
						if (player.getY <= 551) {
							player.teleport(660, 552, false)
						} else {
							player.teleport(660, 551, false)
						}
					}
				}
				// Gnome Stronghold Gate
				case 626 => {
					if (isAt(703, 531)) {
						doGate
						if (player.getY <= 531) {
							player.teleport(703, 532, false)
						} else {
							player.teleport(703, 531, false)
						}
					}
				}
				// Edgeville Members Gate
				case 305 => {
					if (isAt(196, 3266) && p2pCheck(player)) {
						doGate
						if (player.getY <= 3265) {
							player.teleport(196, 3266, false)
						} else {
							player.teleport(196, 3265, false)
						}
					}
				}
				// Dig Site Gate
				case 1089 => {
					if (isAt(59, 573) && p2pCheck(player)) {
						doGate
						if (player.getX <= 58) {
							player.teleport(59, 573, false)
						} else {
							player.teleport(58, 573, false)
						}
					}
				}
				// Woodcutting Guild Gate
				case 356 => {
					if (isAt(560, 472)) {
						if (player.getY <= 472) {
							doGate
							player.teleport(560, 473, false)
						} else {
							if (player.getCurStat(8) < 70) {
								player.setBusy(true)
								val mcgrubor = Instance.getWorld.getNpc(255,
									556, 564, 473, 476)

								if (mcgrubor != null) {
									player.informOfNpcMessage(
										new ChatMessage(mcgrubor,
											"Hello only the top woodcutters are allowed in here",
											player))
								}
								EventHandler.addShort {
									player.setBusy(false)
									player.getActionSender.sendMessage("You need a woodcutting level of 70 to enter")
								}
							} else {
								doGate
								player.teleport(560, 472, false)
							}
						}
					}
				}
				// Black Knight Big Door
				case 142 => player.getActionSender.sendMessage("The doors are locked")
				// Red dragon gate
				case 93 => {
					if (isAt(140, 180) && p2pCheck(player)) {
						doGate
						if (player.getY <= 180) {
							player.teleport(140, 181, false)
						} else {
							player.teleport(140, 180, false)
						}
					}
				}
				// Lesser demon gate
				case 508 => {
					if (isAt(285, 185)) {
						doGate
						if (player.getX <= 284) {
							player.teleport(285, 185, false)
						} else {
							player.teleport(284, 185, false)
						}
					}
				}
				// Lava Maze Gate
				case 319 => {
					if (isAt(243, 178)) {
						doGate
						if (player.getY <= 178) {
							player.teleport(243, 179, false)
						} else {
							player.teleport(243, 178, false)
						}
					}
				}
				// Shilo inside gate
				case 712 => {
					if (isAt(394, 851))
						player.teleport(383, 851, false)
				}
				// Shilo outside gate
				case 611 => {
					if (isAt(388, 851))
						player.teleport(394, 851, false)
				}
				// Legends guild gate
				case 1079 => {
					if (isAt(512, 550)) {
						if (player.getY <= 550) {
							doGate
							player.teleport(513, 551, false)
						} else {
							if (player.getSkillTotal < 1150) {
								player.getActionSender
									.sendMessage(
										"You need a skill total of 1150 or more to enter")

							}
							doGate
							player.teleport(513, 550, false)
						}
					}
				}
				case _ => new DefaultObjectAction().fire

			}
			false
		} else true
	}

	private def replaceDoor(newID : Int, open : Boolean) {
		Instance.getWorld.registerGameObject(
			new GameObject(o.getLocation, newID, o.getDirection, o.getType))
		player.getActionSender.sendSound(
			if (open) "opendoor" else "closedoor")
	}

	private def replaceGameObject(newID : Int) {
		Instance.getWorld.registerGameObject(
			new GameObject(o.getLocation, newID, o.getDirection, o.getType))
	}

	private def doGate {
		player.getActionSender.sendSound("opendoor")
		Instance.getWorld.registerGameObject(
			new GameObject(o.getLocation, 181, o.getDirection, o.getType))
		Instance.getWorld.delayedSpawnObject(o.getLoc, 1000)
	}

}