package bgu.spl.mics.application.objects;


import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private int tick;
    private List<Model> models;
    private List<String> modelsNames;


    public ConfrenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
        this.tick = 1;
        this.models = new LinkedList<Model>();
        this.modelsNames = new LinkedList<String>();
    }

    public String getName() {
        return name;
    }

    public int getDate() {
        return date;
    }

    public List<Model> getModels() {
        return models;
    }

    public List<String> getModelsNames() {
        return modelsNames;
    }

    public void addModel(Model model){
        this.models.add(model);
    }

    public void addModelName(String modelName){
        this.modelsNames.add(modelName);
    }

    public int getTick() {
        return tick;
    }

    public void setTick() {
        this.tick++;
    }
}
