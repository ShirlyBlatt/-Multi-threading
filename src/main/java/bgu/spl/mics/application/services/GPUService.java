package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private GPU gpu;
    private TrainModelEvent currentEvent = null;
    private Queue<TrainModelEvent> trainModelQueue;

    public GPUService(String name, GPU gpu) {
        super(name);
        this.gpu = gpu;
        this.trainModelQueue = new LinkedBlockingQueue<>();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, c -> {
            if (c.getData() == -1){
                if (currentEvent != null){
                    complete(currentEvent, Model.Status.Trained);
                }
                synchronized (this) {
                    this.notifyAll();
                    this.terminate();
                }
            }
            else {
                if (gpu.getNumOfDataPieces() != -1) {
                    if (gpu.getNumOfDataPieces() != 0) {
                        if (gpu.getProcessedData() != null) {
                            if (gpu.getRemainingTicksForProcessing() != 0) {
                                gpu.setRemainingTicksForProcessing();
                                gpu.setGpuRunningTime();
                                gpu.decreaseNumberOfDataPieces();
                            } else {
                                gpu.setProcessedData(null);
                            }
                        } else {
                            DataBatch dataBatch = gpu.getCluster().getProcessedData(this.gpu);
                            gpu.setProcessedData(dataBatch);
                            if (dataBatch != null) {
                                gpu.initRemainingTicksForProcessing();
                                gpu.setRemainingTicksForProcessing();
                                gpu.setGpuRunningTime();
                                gpu.decreaseNumberOfDataPieces();
                            }
                        }
                    }
                    else {

                        gpu.setNumOfDataPieces(-1);
                        complete(currentEvent, Model.Status.Trained);
                        if (!trainModelQueue.isEmpty()){
                            currentEvent = trainModelQueue.poll();
                            gpu.setModel(currentEvent.getData());
                            gpu.split();
                        }
                        else{
                            currentEvent = null;
                            gpu.setModel(null);
                        }
                    }
                }
            }
        });

        subscribeEvent(TrainModelEvent.class, c -> {
            if (gpu.getNumOfDataPieces() != -1){
                this.trainModelQueue.add(c);
            }
            else{
                currentEvent = c;
                gpu.setModel(currentEvent.getData());
                gpu.split();
            }
        });

        subscribeEvent(TestModelEvent.class, c -> {
            switch (c.getData().getStudent().getStatus()){
                case MSc:
                    if (Math.random() <= 0.6){
                        c.getData().setResult(Model.Results.Good);
                        complete(c, Model.Results.Good);
                    }
                    else{
                        c.getData().setResult(Model.Results.Bad);
                        complete(c, Model.Results.Bad);
                    }
                    break;
                case PhD:
                    if (Math.random() <= 0.8){
                        c.getData().setResult(Model.Results.Good);
                        complete(c, Model.Results.Good);
                    }
                    else{
                        c.getData().setResult(Model.Results.Bad);
                        complete(c, Model.Results.Bad);
                    }
                    break;
            }
        });
    }
}
