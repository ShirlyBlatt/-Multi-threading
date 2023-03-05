package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private static class SingeltonHolder{
		private static Cluster instance = new Cluster();
	}
	private Collection<GPU> gpus;
	private Collection<CPU> cpus;
	private Statistics statistics;
	private BlockingQueue<DataBatch> cpuQueue;
	private ConcurrentHashMap<GPU, BlockingQueue<DataBatch>> gpusQueues;

	private Cluster() {
		this.gpus = new LinkedList<GPU>();
		this.cpus = new LinkedList<CPU>();
		this.statistics = new Statistics();
		this.cpuQueue = new LinkedBlockingQueue<DataBatch>();
		this.gpusQueues = new ConcurrentHashMap<>();
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return SingeltonHolder.instance;
	}

	public void setGpus(Collection<GPU> gpus) {
		this.gpus = gpus;
		for (GPU gpu: this.gpus){
			gpusQueues.put(gpu, new LinkedBlockingQueue<DataBatch>());
		}
	}

	public void setCpus(Collection<CPU> cpus) {
		this.cpus = cpus;
	}

	public  void sendToCpu(DataBatch dataBatch){
			this.cpuQueue.add(dataBatch);
	}

	public  void sendToGpu(DataBatch dataBatch){
		this.gpusQueues.get(dataBatch.getGpu()).add(dataBatch);
		this.statistics.setNumOfBatchesProcessedByCpu();
	}

	public  DataBatch getUnProcessedData(){
		return this.cpuQueue.poll();
	}

	public  DataBatch getProcessedData(GPU gpu){
		return this.gpusQueues.get(gpu).poll();
	}

	public void updateTimeUsed(){
		for (GPU gpu: this.gpus){
			statistics.setGpuTimeUsed(gpu.getGpuRunningTime());
		}
		for (CPU cpu: this.cpus){
			statistics.setCpuTimeUsed(cpu.getCpuRunningTime());
		}
	}

	public Statistics getStatistics() {
		return statistics;
	}
}
