package org.moparscape.msc.gs.quest;

import org.moparscape.msc.gs.event.DelayedEvent;
import org.moparscape.msc.gs.model.Player;

/**
 * Version: 17/5/2009
 * 
 * Defines a quest event
 * 
 * @author punKrockeR
 */
public class QuestEvent extends DelayedEvent {
	// The quest action
	private QuestAction action = null;
	// The quest parameters
	private Object[] args = null;
	// The quest itself
	private Quest quest = null;

	/**
	 * Defines a new quest event with an instant delay time (-1)
	 */
	public QuestEvent(final QuestAction action, final Object[] args,
			final Player player, final Quest quest) {
		super(player, -1);
		this.action = action;
		this.args = args;
		this.quest = quest;
	}

	/**
	 * Runs the event and stops itself from running again
	 */
	public void run() {
		super.matchRunning = false;

		final Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					quest.handleAction(action, args, owner);
				} catch (java.util.ConcurrentModificationException cme) {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, new String("qt-" + getIdentifier()));

		quest.setThread(t);
		t.start();
	}

	/**
	 * @return this quest event's action type
	 */
	public QuestAction getAction() {
		return action;
	}

	/**
	 * @return this quest event's args
	 */
	public Object[] getArgs() {
		return args;
	}

	/**
	 * @return this quest event's quest
	 */
	public Quest getQuest() {
		return quest;
	}

	/**
	 * @return this event's identifer
	 */
	public Object getIdentifier() {
		if (quest == null || action == null)
			return null;

		return new String(quest.getUniqueID() + "-" + action.getID() + "-"
				+ owner.getUsernameHash());
	}

	/**
	 * @return if this quest event is identical to the given quest event
	 */
	public boolean equals(QuestEvent event) {
		if (event != null) {
			if (event.getIdentifier() == this.getIdentifier())
				return true;
		}

		return false;
	}
}