package org.moparscape.msc.gs.db;

import org.moparscape.msc.gs.model.Player;

public interface ReportHandler {
	public void submitReport(long from, long about, byte reason, Player from2);

	void submitDupeData(String username, Long hash);
}
