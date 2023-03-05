package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

public class Statistics {

    private List<String> modelsNames;
    private Integer numOfBatchesProcessedByCpu;
    private int CpuTimeUsed;
    private int GpuTimeUsed;

    public Statistics() {
        this.modelsNames = new LinkedList<String>();
        this.numOfBatchesProcessedByCpu = 0;
        this.CpuTimeUsed = 0;
        this.GpuTimeUsed = 0;
    }

    public List<String> getModelsNames() {
        return modelsNames;
    }

    public int getNumOfBatchesProcessedByCpu() {
        return numOfBatchesProcessedByCpu;
    }

    public int getCpuTimeUsed() {
        return CpuTimeUsed;
    }

    public int getGpuTimeUsed() {
        return GpuTimeUsed;
    }

    public synchronized void setNumOfBatchesProcessedByCpu() {
        this.numOfBatchesProcessedByCpu++;
    }

    public void setCpuTimeUsed(int cpuTimeUsed) {
        CpuTimeUsed += cpuTimeUsed;
    }

    public void setGpuTimeUsed(int gpuTimeUsed) {
        GpuTimeUsed += gpuTimeUsed;
    }
}
