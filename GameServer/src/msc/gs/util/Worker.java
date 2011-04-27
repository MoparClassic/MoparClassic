package msc.gs.util;

import java.util.concurrent.*;

import msc.gs.model.Player;

/**
 * The Worker class is a runnable class that can be used for splitting up a
 * workload into multiple Workers.
 * This class should only ever be used through a WorkGroup.
 */
public class Worker<E> implements Runnable {
	private WorkGroup parent;
	private BlockingQueue<Player> workload;
	private Processor processor;
	private boolean running = true;
	
	public Worker(WorkGroup wg, BlockingQueue<Player> workload, Processor p) {
		this.parent = wg;
		this.workload = workload;
		this.processor = p;
	}
	
	public void stop() { 
		running = false;
	}
	
	public void run() {
		while(running) {
			try {
				if(parent.shouldProcess()) { 
					Player element = workload.take();
					processor.process(element);
				}
			} catch(InterruptedException ie) {
				//thread has been asked to stop
				running = false;
			} catch(Exception e) {
				parent.addUncaughtException(e);
			}
		}
	}
}
