package org.moparscape.msc.gs.util;

import java.text.DecimalFormat;

public class DelayMonitor {
	private static final DecimalFormat formatter = new DecimalFormat("###,###.###");
	private long t, t1, t2;
	private int totalItemsUpdated;
	private int numRepeats;
	
	public void startMeasuring() { t1 = System.nanoTime(); }
	public void setItemsUpdated(int i) { totalItemsUpdated += i; }
	public void endMeasuring() { t2 = System.nanoTime(); processMeasurements(); }
	public double getAvgDelay() { return (t / numRepeats) / 1e6; }
	public double getAvgItemsPerRepeat() { return (totalItemsUpdated / numRepeats); }
	public int getRepeatCount() { return numRepeats; }
	public void reset() { t = t1 = t2 = numRepeats = totalItemsUpdated = 0; }
	private void processMeasurements() {
		t += (t2 - t1);
		numRepeats++;
	}
	public static String toString(double d) { return formatter.format(d); }
}
