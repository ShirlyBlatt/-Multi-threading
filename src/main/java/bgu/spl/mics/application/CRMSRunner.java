package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {

    public static void main(String[] args) {
        Gson gson = new Gson();
        String inputFilePath = "./example_input.json";
        try{
            Reader reader = Files.newBufferedReader(Paths.get(inputFilePath));
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray studentsList = gson.fromJson(jsonObject.get("Students"), JsonArray.class);
            List<Student> studentsObjList = new LinkedList<Student>();
            List<Model> modelsObjList = new LinkedList<Model>();
            for(JsonElement student: studentsList){
                JsonObject studentJsonObj = student.getAsJsonObject();

                // create student object
                Student s = new Student(studentJsonObj.get("name").getAsString(),
                        studentJsonObj.get("department").getAsString(),
                        studentJsonObj.get("status").getAsString());

                studentsObjList.add(s);

                // create model object
                JsonArray modelsList = gson.fromJson(studentJsonObj.get("models"), JsonArray.class);
                for(JsonElement model: modelsList){
                    JsonObject modelJsonObj = model.getAsJsonObject();

                    // create Data object
                    Data data = new Data(modelJsonObj.get("type").getAsString(),
                            modelJsonObj.get("size").getAsInt());

                    // create model object
                    Model m = new Model(modelJsonObj.get("name").getAsString(),
                            data,
                            s);
                    modelsObjList.add(m);
                }


            }

            for (Model model: modelsObjList){
                model.getStudent().setModelList(model);
            }
            // gpu objects
            JsonObject gpus = gson.fromJson(reader, JsonObject.class);
            JsonArray gpusList = gson.fromJson(jsonObject.get("GPUS"), JsonArray.class);
            List<GPU> gpusObjList = new LinkedList<GPU>();
            for (JsonElement gpu: gpusList){
                gpusObjList.add(new GPU(gpu.getAsString()));
            }

            // cpu objects
            JsonObject cpus = gson.fromJson(reader, JsonObject.class);
            JsonArray cpusList = gson.fromJson(jsonObject.get("CPUS"), JsonArray.class);
            List<CPU> cpusObjList = new LinkedList<CPU>();
            for (JsonElement cpu: cpusList){
                cpusObjList.add(new CPU(cpu.getAsInt()));
            }

            Cluster cluster = gpusObjList.get(0).getCluster();
            cluster.setGpus(gpusObjList);
            cluster.setCpus(cpusObjList);

            // conference objects
            JsonObject conferences = gson.fromJson(reader, JsonObject.class);
            JsonArray conferencesList = gson.fromJson(jsonObject.get("Conferences"), JsonArray.class);
            List<ConfrenceInformation> conferencesObjList = new LinkedList<ConfrenceInformation>();
            for (JsonElement conference: conferencesList){
                JsonObject conferenceJsonObj = conference.getAsJsonObject();
                ConfrenceInformation confrenceInformation = new ConfrenceInformation(
                        conferenceJsonObj.get("name").getAsString(),
                        conferenceJsonObj.get("date").getAsInt());

                conferencesObjList.add(confrenceInformation);
            }

            int tick = jsonObject.get("TickTime").getAsInt();
            int duration = jsonObject.get("Duration").getAsInt();

            // create threads
            List<Thread> threadList = new LinkedList<Thread>();

            int gpuIndex = 1;
            for (GPU gpu: gpusObjList){
                Thread t = new Thread(new GPUService("GPU" + gpuIndex, gpu), "GPU-thread " + gpuIndex);
                gpuIndex++;
                threadList.add(t);
                t.start();
                t.join(100);
            }

            int cpuIndex = 1;
            for (CPU cpu: cpusObjList){
                Thread t = new Thread(new CPUService("CPU" + cpuIndex, cpu), "CPU-thread " + cpuIndex);
                cpuIndex++;
                threadList.add(t);
                t.start();

            }

            for (ConfrenceInformation conference: conferencesObjList){
                Thread t = new Thread(new ConferenceService(conference));
                threadList.add(t);
                t.start();
            }

            for (Student student: studentsObjList){
                Thread t = new Thread(new StudentService(student.getName(), student), "Student-thread " + student.getName());
                threadList.add(t);
                t.start();
            }

            Thread t = new Thread(new TimeService(duration, tick), "Time-Thread");
            t.start();
            t.join();

            for (Thread thread: threadList){
                if (thread.isAlive()) {
                    thread.join();
                }
            }

            // output file
            PrintWriter outputFile = new PrintWriter("./outputfile.txt");

            // students
            String students = "Students :  ";
            outputFile.println(students);
            for (Student student: studentsObjList){
                String studentString = "  name: " + student.getName() + "\n"
                        + "  department: " + student.getDepartment() +"\n"
                        + "  status: " +student.getStatus() + "\n"
                        + "  publications: " + student.getPublications() + "\n"
                        +  "  papersRead: " + student.getPapersRead() + "\n";
                String testedModels ="  trainedModels: " + "\n";
                String publishedModels = "  publishedModels: \n";
                for (Model model: student.getModelList()){
                    if (model.getStatus().equals(Model.Status.Tested)){
                        testedModels += "    name: " + model.getName() + "\n"
                                + "    data : " + "\n"
                                + "      type: " + model.getData().getType() + "\n"
                                + "      size: " + + model.getData().getSize() + "\n"
                                + "    status: " + model.getStatus() + "\n"
                                +  "    results: " +model.getResult() + "\n";
                    }
                    else if (model.getStatus().equals(Model.Status.Published)){
                        publishedModels += "    name: " + model.getName() + "\n"
                                + "    data : " + "\n"
                                + "      type: " + model.getData().getType() + "\n"
                                + "      size: " + + model.getData().getSize() + "\n"
                                + "    status: " + model.getStatus() + "\n"
                                +  "    results: " +model.getResult() + "\n";
                    }
                }

                outputFile.println(studentString);
                outputFile.println(testedModels);
                outputFile.println(publishedModels);

            }

            // conference
            String conferencesS= "Conferences: " + "\n";
            outputFile.println(conferencesS);
            for (ConfrenceInformation conference: conferencesObjList){
                String conferenceString = "  name: " + conference.getName() + "\n"
                        + "  date: " + conference.getDate() + "\n"
                        + "  publications: " + "\n";
                for (Model model: conference.getModels()) {
                    if (model.getStatus().equals(Model.Status.Published)) {
                        conferenceString += "    name: " + model.getName() + "\n"
                                + "    data : " + "\n"
                                + "      type: " + model.getData().getType() + "\n"
                                + "      size: " + +model.getData().getSize() + "\n"
                                + "    status: " + model.getStatus() + "\n"
                                + "    results: " + model.getResult() + "\n";
                    }
                }
                outputFile.println(conferenceString);
            }

            // cpu
            int sumCpu = 0;
            for (CPU cpu: cpusObjList){
                sumCpu += cpu.getCpuRunningTime();
            }
            String cpuString = "cpuTimeUsed : " + sumCpu;
            outputFile.println(cpuString);

            // gpu
            int sumGpu = 0;
            for (GPU gpu: gpusObjList){
                sumGpu+=gpu.getGpuRunningTime();
            }
            String gpuString ="gpuTimeUsed : " + sumGpu;
            outputFile.println(gpuString);

            cluster.updateTimeUsed();
            String numberOfBatchesProcessed = "batchesProcessed : ";
            numberOfBatchesProcessed += cluster.getStatistics().getNumOfBatchesProcessedByCpu();
            outputFile.println(numberOfBatchesProcessed);
            outputFile.close();
        }
        catch (Exception ignored){}
    }
}
