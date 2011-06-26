package org.moparscape.msc.gs.quest;

import java.util.Iterator;
import java.util.Vector;

import org.moparscape.msc.gs.event.DelayedEvent;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.util.Logger;

/**
 * Version: 17/5/2009
 * 
 * Handles QuestEvents and their queues and accessor/manipulator methods
 * 
 * @author punKrockeR
 */
public final class QuestEventManager {
	// The quest events queued for adding
	private Vector<QuestEvent> toAdd = null;
	// The quest events list
	private Vector<QuestEvent> events = null;
	// Is this event manager currently processing?
	private boolean running = true;

	/**
	 * Contructs a new QuestEvent handler with the given initial array size
	 */
	public QuestEventManager(int initialCapacity) {
		toAdd = new Vector<QuestEvent>(initialCapacity);
		events = new Vector<QuestEvent>(initialCapacity);
	}

	/**
	 * @return if the event handler contains the given event
	 */
	public boolean contains(DelayedEvent event) {
		return events.contains(event);
	}

	/**
	 * @return the list of existing events
	 */
	public Vector<QuestEvent> getEvents() {
		return events;
	}

	/**
	 * Adds the given event to the queue
	 * 
	 * TODO: Make sure this is bugless
	 */
	public void add(QuestEvent event) {
		if (!events.contains(event) && !toAdd.contains(event))
			toAdd.add(event);
	}

	/**
	 * Removes the given event from the list
	 */
	public void remove(QuestEvent event) {
		events.remove(event);
	}

	/**
	 * Removes any of the given player's events
	 */
	public void removePlayersEvents(Player player) {
		try {
			Iterator<QuestEvent> iterator = events.iterator();
			while (iterator.hasNext()) {
				QuestEvent event = iterator.next();
				if (event.belongsTo(player))
					iterator.remove();
			}
		} catch (Exception e) {
			Logger.println("Error @ removePlayer, IP address: "
					+ player.getCurrentIP() + " Name: " + player.getUsername()
					+ " message : " + e.getMessage());
		}

	}

	/**
	 * Processes all the events in the queues
	 */
	public void process() {
		if (!running)
			return;

		try {
			if (toAdd.size() > 0) {
				events.addAll(toAdd);
				toAdd.clear();
			}

			Iterator<QuestEvent> iterator = events.iterator();
			while (iterator.hasNext()) {
				QuestEvent event = iterator.next();

				if (event == null) {
					iterator.remove();
					continue;
				}

				if (event.shouldRun()) {
					event.run();
					event.updateLastRun();
				}

				if (event.shouldRemove())
					iterator.remove();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Kills this event manager
	 */
	public void kill() {
		running = false;
		toAdd.clear();
		events.clear();
	}
}