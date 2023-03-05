package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.List;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student student;
    private int modelIndex = 0;
    private Future<?> trainFuture;
    private Future<?> testFuture;
    private Future<?> publishFuture;

    public StudentService(String name, Student student) {
        super(name);
        this.student = student;
        trainFuture = null;
        testFuture = null;
        publishFuture = null;
    }

    @Override
    protected void initialize() {
        // signing student to Tick conferenceBroadcast
        subscribeBroadcast(TickBroadcast.class, c -> {
            if (c.getData() == -1){
                this.terminate();
            }
            else{
                if (modelIndex < this.student.getModelList().size()) {
                    Model model = this.student.getModelList().get(modelIndex);
                    if (trainFuture != null && trainFuture.isDone()) {
                        if (testFuture == null) {
                            model.setStatus(Model.Status.Tested);
                            testFuture = sendEvent(new TestModelEvent(model));
                        } else if (testFuture != null && testFuture.isDone()) {
                            if (model.getResult().equals(Model.Results.Good)) {
                                if (publishFuture == null) {
                                    model.setStatus(Model.Status.Published);
                                    student.setPublications();
                                    publishFuture = sendEvent(new PublishResultsEvent(model));
                                } else if (publishFuture != null && publishFuture.isDone()) {
                                    modelIndex++;
                                    if (modelIndex < this.student.getModelList().size()) {
                                        model = this.student.getModelList().get(modelIndex);
                                        trainFuture = sendEvent(new TrainModelEvent(model));
                                        model.setStatus(Model.Status.Training);
                                        testFuture = null;
                                        publishFuture = null;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        // signing student to publish conference Broadcast
        subscribeBroadcast(PublishConferenceBroadcast.class, c -> {
            List<Model> modelList = c.getData();
            for(Model model: modelList){
                if (model.getStudent() != student){
                    student.readPaper();
                }
            }
        });

        //send first model for training
        Model model = this.student.getModelList().get(modelIndex);
        trainFuture = sendEvent(new TrainModelEvent(model));
        model.setStatus(Model.Status.Training);
    }
}
