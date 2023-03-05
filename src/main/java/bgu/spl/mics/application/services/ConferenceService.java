package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private ConfrenceInformation confrence;

    public ConferenceService(ConfrenceInformation confrence) {
        super(confrence.getName());
        this.confrence = confrence;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, c -> {
            if (c.getData() == -1){
                this.terminate();
            }
            else {
                if (confrence.getDate() == confrence.getTick()) {
                    sendBroadcast(new PublishConferenceBroadcast(confrence.getModels()));
                    this.terminate();
                } else {
                    this.confrence.setTick();
                }
            }
        });

        subscribeEvent(PublishResultsEvent.class, c -> {
            this.confrence.addModel(c.getData());
            this.confrence.addModelName(c.getData().getName());
            c.getData().setStatus( Model.Status.Published);
            complete(c, Model.Status.Published);
        });

    }
}
