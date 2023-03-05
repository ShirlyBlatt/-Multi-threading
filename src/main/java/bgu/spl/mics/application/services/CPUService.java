package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.DataBatch;

/**
 * CPU service is responsible for handling the
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private CPU cpu;

    public CPUService(String name, CPU cpu) {
        super(name);
        this.cpu = cpu;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, c -> {
            if (c.getData() == -1){
                synchronized (this){
                    this.notifyAll();
                    this.terminate();
                }
            }
            else {
                if (cpu.getUnprocessedData() == null) {
                    DataBatch dataBatch = cpu.getCluster().getUnProcessedData();
                    cpu.setUnprocessedData(dataBatch);
                    if (dataBatch != null) {
                        cpu.initRemainingTicksForProcessing();
                        cpu.setRemainingTicksForProcessing();
                        cpu.setCpuRunningTime();
                    }
                } else {
                    if (cpu.getRemainingTicksForProcessing() == 0) {
                        cpu.getCluster().sendToGpu(cpu.getUnprocessedData());
                        cpu.setUnprocessedData(null);
                    } else {
                        cpu.setRemainingTicksForProcessing();
                        cpu.setCpuRunningTime();
                    }
                }
            }
        });

    }
}
