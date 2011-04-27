package msc.gs.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import msc.gs.core.ClientUpdater;
import msc.gs.model.Player;

/**
 * This class manages a set of Workers.
 */
public class WorkGroup<E> {
	private List<Throwable> uncaughtExceptions;
	private Worker[] workers;
	private Thread[] workerThreads;
	private BlockingQueue<Player> workload = null;
	private Processor processor;
	public WorkGroup(Processor p) {
		this(p, Runtime.getRuntime().availableProcessors() + 1);
	}
	
	/**
	 * Start the workgroup.
	 * All threads are initiated into a sleeping state.
	 */
	public WorkGroup(Processor p, int numWorkers) {
		this.workload = new ArrayBlockingQueue<Player>(1000);
		this.processor = p;
		this.workers = new Worker[numWorkers];
		this.uncaughtExceptions = new ArrayList<Throwable>();
		workerThreads = new Thread[workers.length];
		for(int i=0; i<workers.length; i++) {
			workers[i] = new Worker<E>(this, workload, processor);
			workerThreads[i] = new Thread(workers[i], "-"+i);
			workerThreads[i].start();
		}
	}
	
	public void processWorkload(Collection<Player> players) throws InterruptedException {
		uncaughtExceptions.clear();
		setWorkload(players);
		waitFor();
	}
	public boolean shouldProcess() {
		return !((ClientUpdater)processor).updatingCollections;
	}
	private void waitFor() throws InterruptedException {
		while(!workload.isEmpty())
			Thread.sleep(10);
		
		((ClientUpdater)processor).updateCollections();
	}
	
	public int remainingJobs() {
		return workload.size();
	}
	
	public int getNumWorkers() { 
		return workers.length; 
	}
	
	public void addUncaughtException(Throwable t) {
		t.printStackTrace();
		uncaughtExceptions.add(t);
	}
	
	public void setWorkload(Collection<Player> c) {
		workload.clear();
		workload.addAll(c);
	}
}
