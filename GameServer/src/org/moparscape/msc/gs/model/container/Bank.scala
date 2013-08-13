package org.moparscape.msc.gs.model.container
import org.moparscape.msc.gs.model.World

class Bank extends Container(if (World.isMembers) 192 else 48, true) {

}