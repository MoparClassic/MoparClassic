package org.moparscape.msc.gs.model.snapshot;

/**
 * Activity log class, stores activity string, time of activity and activity
 * owner
 * 
 * @author Pets
 * 
 */
public class Activity extends Snapshot {

	/**
	 * The messages that was sent
	 */
	private String activity;

	/**
	 * Constructor
	 * 
	 * @param player
	 *            player that performed the activity
	 * @param activity
	 *            the activity that was performed
	 */
	public Activity(String sender, String activity) {
		super(sender);
		this.setActivity(activity);
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getActivity() {
		return activity;
	}

	@Override
	public String toString() {
		return activity;
	}
}
