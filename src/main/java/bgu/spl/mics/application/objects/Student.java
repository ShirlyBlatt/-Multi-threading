package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private List<Model> modelList;

    public Student(String name, String department, String status) {
        this.name = name;
        this.department = department;
        if (status.charAt(0) == 'M') {this.status = Degree.MSc;}
        else {this.status = Degree.PhD;}
        modelList = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public Degree getStatus() {
        return status;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public List<Model> getModelList() {
        return modelList;
    }

    public void setModelList(Model model) {
        this.modelList.add(model);
    }

    public void readPaper() {
        this.papersRead++;
    }

    public void setPublications(){
        this.publications++;
    }

}
