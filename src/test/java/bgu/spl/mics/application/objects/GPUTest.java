package bgu.spl.mics.application.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class GPUTest {

    static GPU gpu;
    static DataBatch databatch;
    static Data data;
    static Student student;
    static Model model;

    @Before
    public void setUp() throws Exception {
        gpu = new GPU("RTX2080");
        data= new Data(" ", 1);
        databatch = new DataBatch(data, 0, gpu);
        student = new Student(" " , " ", " ");
        model = new Model(" ", data , student);
        gpu.setModel(model);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void getType() {
    }

    @Test
    public void getModel() {
    }

    @Test
    public void getCluster() {
    }

    @Test
    public void getVram() {
    }

    @Test
    public void getProcessedData() {
    }

    @Test
    public void getGpuRunningTime() {
    }

    @Test
    public void getNumOfDataPieces() {
    }

    @Test
    public void getRemainingTicksForProcessing() {
    }

    @Test
    public void split() {
        assertNotNull(gpu.getModel());
        int size = gpu.getModel().getSize();
        if (size % 1000 != 0){
            size = (size / 1000) + 1;
        }
        else {
            size /= 1000;
        }
        gpu.split();
        assertEquals(size, gpu.getNumOfDataPieces());

    }

    @Test
    public void setRemainingTicksForProcessing() {
        int pre= gpu.getRemainingTicksForProcessing();
        gpu.setRemainingTicksForProcessing();
        assertEquals(pre - 1, gpu.getRemainingTicksForProcessing());
    }

    @Test
    public void initRemainingTicksForProcessing() {
        gpu.initRemainingTicksForProcessing();
        switch (gpu.getType()){
            case RTX3090:
                assertEquals(1,gpu.getRemainingTicksForProcessing());
                break;
            case RTX2080:
                assertEquals(2, gpu.getRemainingTicksForProcessing());
                break;
            case GTX1080:
                assertEquals(4, gpu.getRemainingTicksForProcessing());
                break;
        }
    }

    @Test
    public void setGpuRunningTime() {
        int pre= gpu.getGpuRunningTime();
        gpu.setGpuRunningTime();
        assertEquals(pre +1, gpu.getGpuRunningTime());
    }

    @Test
    public void setProcessedData() {
        gpu.setProcessedData(databatch);
        assertEquals(databatch, gpu.getProcessedData());
    }

    @Test
    public void setModel() {
        gpu.setModel(model);
        assertEquals(model,gpu.getModel());
    }

    @Test
    public void setNumOfDataPieces() {
       gpu.setNumOfDataPieces(5);
       assertEquals(5, gpu.getNumOfDataPieces());
        gpu.setNumOfDataPieces(100);
        assertEquals(100, gpu.getNumOfDataPieces());
    }

    @Test
    public void decreaseNumberOfDataPieces() {
        int pre= gpu.getNumOfDataPieces();
        gpu.decreaseNumberOfDataPieces();
        assertEquals(pre -1, gpu.getNumOfDataPieces());
    }
}