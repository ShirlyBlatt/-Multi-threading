package bgu.spl.mics.application.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CPUTest {

    static DataBatch dataBatch;
    static Data data;
    static GPU gpu;
    static CPU cpu;

    @Before
    public void setUp() throws Exception {
        gpu = new GPU("RTX2080");
        data= new Data(" ", 1);
        dataBatch = new DataBatch(data, 0, gpu);
        cpu= new CPU(8);
        cpu.setUnprocessedData(dataBatch);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getCores() {
    }

    @Test
    public void getUnprocessedData() {
    }

    @Test
    public void getRemainingTicksForProcessing() {
    }

    @Test
    public void getCluster() {
    }

    @Test
    public void getCpuRunningTime() {
    }

    @Test
    public void setUnprocessedData() {
        cpu.setUnprocessedData(dataBatch);
        assertEquals(dataBatch,cpu.getUnprocessedData());
    }

    @Test
    public void setRemainingTicksForProcessing() {
        int pre = cpu.getRemainingTicksForProcessing();
        cpu.setRemainingTicksForProcessing();
        assertEquals(pre-1,cpu.getRemainingTicksForProcessing());
    }

    @Test
    public void initRemainingTicksForProcessing() {
        cpu.initRemainingTicksForProcessing();
        switch (cpu.getUnprocessedData().getData().getType()){
            case Images:
                assertEquals(((32 / cpu.getCores()) * 4),cpu.getRemainingTicksForProcessing());
                break;
            case Text:
                assertEquals(((32 / cpu.getCores()) * 2),cpu.getRemainingTicksForProcessing());
                break;
            case Tabular:
                assertEquals(((32 / cpu.getCores()) ),cpu.getRemainingTicksForProcessing());
                break;
        }
    }

    @Test
    public void setCpuRunningTime() {
        int pre = cpu.getCpuRunningTime();
        cpu.setCpuRunningTime();
        assertEquals(pre + 1,cpu.getCpuRunningTime());
    }
}