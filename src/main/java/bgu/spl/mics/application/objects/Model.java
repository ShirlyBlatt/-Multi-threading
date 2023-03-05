package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    public enum Status {PreTrained, Training, Trained, Tested, Published}
    public enum Results {None, Good, Bad}

    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Results result;

    public Model(String name, Data data, Student student) {
        this.name = name;
        this.data = data;
        this.student = student;
        this.status = Status.PreTrained;
        this.result = Results.None;
    }

    public String getName() {
        return name;
    }

    public Data getData() {
        return data;
    }

    public Student getStudent() {
        return student;
    }

    public Status getStatus() {
        return status;
    }

    public Results getResult() {
        return result;
    }

    public int getSize() {
        return data.getSize();
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setResult(Results result) {
        this.result = result;
    }
}
