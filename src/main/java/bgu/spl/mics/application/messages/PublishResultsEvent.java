package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event<Model.Status> {
    private Model model;

    public PublishResultsEvent(Model model) {
        this.model = model;
    }

    public Model getData() {
        return model;
    }
}
