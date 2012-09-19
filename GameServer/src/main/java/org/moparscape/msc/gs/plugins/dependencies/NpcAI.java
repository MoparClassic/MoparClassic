package org.moparscape.msc.gs.plugins.dependencies;

import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;

/**
 * Fires off all combat related events for NPCs.
 * 
 * @author xEnt
 * 
 */
public interface NpcAI {

	/**
	 * fired off each time the health of an implemented NPC script changes
	 * 
	 * @param npc
	 *            - the NPC object
	 * @param percent
	 *            - the percentage out of 100, that the health of the NPC is on.
	 */
	public void onHealthPercentage(Npc npc, int percent);

	/**
	 * fired off when a player attacks the implemented NPC
	 * 
	 * @param attacker
	 * @param npc
	 */
	public void onMeleeAttack(Player attacker, Npc npc);

	/**
	 * fired off when the NPC dies
	 * 
	 * @param npc
	 * @param player
	 */
	public void onNpcDeath(Npc npc, Player player);

	/**
	 * fired off when someone shoots magic at the NPC
	 * 
	 * @param attacker
	 * @param npc
	 */
	public void onMageAttack(Player attacker, Npc npc);

	/**
	 * fired off when someone ranges the NPC
	 * 
	 * @param p
	 * @param npc
	 */
	public void onRangedAttack(Player p, Npc npc);

	/**
	 * fired off when the NPC goes to attack the player
	 * 
	 * @param npc
	 * @param player
	 */
	public void onNpcAttack(Npc npc, Player player);

	/**
	 * gets the ID of the NPC script in use
	 * 
	 * @return
	 */
	public int getID();

}
