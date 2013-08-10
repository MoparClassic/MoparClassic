package org.moparscape.msc.gs.db;

import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.db.impl.DataServiceFactory;

public class DataManager {
	public static final DataService dataService = getDataService();
	public static final ReportHandler reportHandler = getReportHandler();

	private static DataService getDataService() {
		try {
			return DataServiceFactory
					.createDataRequestService(Config.DATA_SERVICE);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static ReportHandler getReportHandler() {
		try {
			return DataServiceFactory
					.createReportHandler(Config.REPORT_HANDLER);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
