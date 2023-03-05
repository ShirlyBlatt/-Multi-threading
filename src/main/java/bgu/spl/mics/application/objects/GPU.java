package bgu.spl.mics.application.objects;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model;
    private Cluster cluster;
    private int vram;
    private DataBatch processedData;
    private int gpuRunningTime;
    private int numOfDataPieces;
    private int remainingTicksForProcessing;

    public GPU(String type) {
        if (type.charAt(3) == '3') {
            this.type = Type.RTX3090;
            this.vram = 32;
        }
        else if (type.charAt(3) == '2') {
            this.type = Type.RTX2080;
            this.vram = 16;
        }
        else {
            this.type = Type.GTX1080;
            this.vram = 8;
        }
        this.remainingTicksForProcessing = 0;
        this.processedData = null;
        this.gpuRunningTime = 0;
        this.numOfDataPieces = -1;
        this.cluster = Cluster.getInstance();
    }

    public Type getType() {
        return type;
    }

    public Model getModel() {
        return model;
    }

    /**
     * @INV: cluster != null
     * @return cluster
     */
    public Cluster getCluster() {
        return cluster;
    }

    /**
     * @INV: vram >= 0
     * @return
     */
    public int getVram() {
        return vram;
    }


    public DataBatch getProcessedData() {
        return processedData;
    }

    /**
     * @INV: gpuRunningTime >= 0
     * @return
     */
    public int getGpuRunningTime() {
        return gpuRunningTime;
    }

    /**
     * @INV: numOfDataPieces >= 0
     * @return numOfDataPieces
     */
    public int getNumOfDataPieces() {
        return numOfDataPieces;
    }

    /**
     * @INV: remainingTicksForProcessing >= 0
     * @return remainingTicksForProcessing
     */
    public int getRemainingTicksForProcessing() {
        return remainingTicksForProcessing;
    }

    /**
     * @INV: numOfDataPieces >= 0
     * @pre None
     * @post (gpu.getModel().getSize()/1000) + 1 == gpu.getNumOfDataPieces()
     */
    public void split(){
        Data data = this.model.getData();
        int index = 0;
        this.numOfDataPieces++;
        while (index < data.getSize()){
            DataBatch splitedData = new DataBatch(data, index, this);
            this.numOfDataPieces++;
            this.cluster.sendToCpu(splitedData);
            index += 1000;
        }
    }

    /**
     *
     * @pre None
     * @post @pre(getRemainingTicksForProcessing) -1 = getRemainingTicksForProcessing;
     */
    public void setRemainingTicksForProcessing(){
        this.remainingTicksForProcessing--;
    }

    /**
     *
     * @pre None
     * @post set the remainingTicksForProcessing for the appropriate type
     */
    public void initRemainingTicksForProcessing(){
        switch (type){
            case RTX3090:
                this.remainingTicksForProcessing = 1;
                break;
            case RTX2080:
                this.remainingTicksForProcessing = 2;
                break;
            case GTX1080:
                this.remainingTicksForProcessing = 4;
                break;
        }
    }

    /**
     *
     * @pre None
     * @post @pre(getCpuRunningTime) +1 = getCpuRunningTime;
     */
    public void setGpuRunningTime() {
        this.gpuRunningTime++;
    }

    /**
     *
     * @param processedData = the data that the gpu will process
     * @pre None
     * @post processedData.equal(gpu.getProcessedData)) == true
     */
    public void setProcessedData(DataBatch processedData) {
        this.processedData = processedData;
    }

    /**
     *
     * @param model = the model that the gpu will train
     * @pre None
     * @post model.equal(gpu.getModel)) == true
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     *
     * @param numOfDataPieces = the number of piaces that the gpu will process
     * @pre None
     * @post numOfDataPieces.equal(gpu.getNumOfDataPieces)) == true
     */
    public void setNumOfDataPieces(int numOfDataPieces) {
        this.numOfDataPieces = numOfDataPieces;
    }

    /**
     *
     * @pre None
     * @post @pre(gpu.getNumOfDataPieces) -1 == gpu.getNumOfDataPieces
     */
    public void decreaseNumberOfDataPieces(){
        this.numOfDataPieces--;
    }
}
