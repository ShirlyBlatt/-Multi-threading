package bgu.spl.mics.application.objects;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private int cores;
    private DataBatch UnprocessedData;
    private Cluster cluster;
    private int cpuRunningTime;
    private int remainingTicksForProcessing;

    public CPU(int cores) {
        this.cores = cores;
        this.UnprocessedData = null;
        this.cpuRunningTime = 0;
        this.remainingTicksForProcessing = 0;
        this.cluster = Cluster.getInstance();
    }

    /**
     * @INV: cores >= 0
     * @return cores
     */
    public int getCores() {
        return cores;
    }

    public DataBatch getUnprocessedData() {
        return UnprocessedData;
    }

    /**
     * @INV: remainingTicksForProcessing >= 0
     * @return remainingTicksForProcessing
     */
    public int getRemainingTicksForProcessing() {
        return remainingTicksForProcessing;
    }

    /**
     * @INV: cluster != null
     * @return cluster
     */
    public Cluster getCluster() {
        return cluster;
    }

    /**
     * @INV: cpuRunningTime >= 0
     * @return
     */
    public int getCpuRunningTime() {
        return cpuRunningTime;
    }

    /**
     *
     * @param dataBatch = the data that the cpu will process
     * @pre None
     * @post data.equal(cpu.getUnprocessedData)) == true
     */
    public void setUnprocessedData(DataBatch dataBatch) {
        this.UnprocessedData = dataBatch;
    }

    /**
     *
     * @pre None
     * @post @pre(getRemainingTicksForProcessing) -1 = getRemainingTicksForProcessing;
     */
    public void setRemainingTicksForProcessing() {
        this.remainingTicksForProcessing--;
    }

    /**
     *
     * @pre None
     * @post set the remainingTicksForProcessing for the appropriate type
     */
    public void initRemainingTicksForProcessing(){
        switch (this.UnprocessedData.getData().getType()){
            case Images:
                this.remainingTicksForProcessing = ((32 / this.cores) * 4);
                break;
            case Text:
                this.remainingTicksForProcessing = ((32 / this.cores) * 2);
                break;
            case Tabular:
                this.remainingTicksForProcessing = ((32 / this.cores));
                break;
        }
    }


    /**
     *
     * @pre None
     * @post @pre(getCpuRunningTime) +1 = getCpuRunningTime;
     */
    public void setCpuRunningTime() {
        this.cpuRunningTime++;
    }
}
