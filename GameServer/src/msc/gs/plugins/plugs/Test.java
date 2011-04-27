package msc.gs.plugins.plugs;

import msc.gs.model.GameObject;
import msc.gs.model.Player;
import msc.gs.plugins.listeners.ObjectListener;

/**
 * Testting some triggering.
 * 
 * @author xEnt
 * 
 */
public class Test implements ObjectListener {

    public int[] getAssociatedIDS() {
	return new int[] {/* 19 */}; // altar.
    }

    public boolean onObjectAction(GameObject obj, String command, Player player) {
	player.getActionSender().sendMessage("You used the " + obj.getGameObjectDef().name);
	return true;
    }

    public boolean onObjectCreation(GameObject obj, Player player) {
	return true;
    }

    public boolean onObjectRemoval(GameObject obj) {
	return true;
    }

}
