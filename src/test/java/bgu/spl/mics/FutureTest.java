package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class FutureTest {

    static String result;
    static Future<String> future;

    @Before
    public void setUp() throws Exception {
        result = "";
        future = new Future<String>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getResult() {
    }

    @Test
    public void get() {
        Thread t = new Thread(() -> {
            try {
                sleep(4000);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
            future.resolve("resolved");
        });
        t.start();
        long currentTime = System.currentTimeMillis();
        String s = future.get();
        currentTime = System.currentTimeMillis() - currentTime;
        assertEquals("resolved", s);
        assertTrue(currentTime > 100);
    }

    @Test
    public void resolve() {
        future.resolve(result);
        assertEquals(result, future.getResult());
    }

    @Test
    public void isDone() {
        assertEquals(future.getResult() != null, future.isDone());
    }

    @Test
    public void testGet() {
        Thread t = new Thread(() -> {
            try {
                sleep(4000);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
            future.resolve("resolved");
        });
        t.start();
        long currentTime = System.currentTimeMillis();
        String s = future.get(5000, TimeUnit.MILLISECONDS);
        currentTime = System.currentTimeMillis() - currentTime;
        assertEquals("resolved", s);
        assertTrue(currentTime > 100);
    }
}