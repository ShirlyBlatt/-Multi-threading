package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class MessageBusImplTest {

    static MessageBusImpl msgBus;
    static Student student;
    static Model model;
    static Data data;
    static GPU gpu;
    static CPU cpu;

     //subscribe event
     static StudentService studentService1;
     static TrainModelEvent trainEvent1;
     static GPUService gpuService1;
     static TestModelEvent testModelEvent1;

     //subscribe broadcast
    static StudentService studentService2;
    static GPUService gpuService2;
    static TickBroadcast tickBroadcast2;
    static PublishConferenceBroadcast publishConferenceBroadcast2;

    //complete
    static ExampleEvent exampleEvent3;
    static ConfrenceInformation confrenceInformation3;
    static ConferenceService conferenceService3;

    //send broadcast
    static TickBroadcast tickBroadcast4;
    static StudentService studentService4;

    //send event
    static PublishResultsEvent publishResultsEvent5;
    static StudentService studentService5;

    //register
    static CPUService cpuService6;

    //unregister
    static StudentService studentService7;
    static TrainModelEvent trainModelEvent7;
    static TickBroadcast tickBroadcast7;

    //await message
    static StudentService studentService8;
    static TrainModelEvent trainModelEvent8;

    @Before
    public void setUp() throws Exception {
        msgBus = MessageBusImpl.getInstance();
        student = new Student("name ", "department ", "PhD ");
        data = new Data(" Image", 1);
        model = new Model("name", data,student);
        gpu = new GPU("RTX2080");
        cpu = new CPU(1);

        //subscribe event
        studentService1 = new StudentService(" ", student);
        msgBus.register(studentService1);
        trainEvent1 = new TrainModelEvent(model);
        gpuService1 = new GPUService(" ",gpu);
        msgBus.register(gpuService1);
        testModelEvent1 = new TestModelEvent(model);

        //subscribe broadcast
        studentService2 = new StudentService(" ", student);
        msgBus.register(studentService2);
        gpuService2 = new GPUService(" ",gpu);
        msgBus.register(gpuService2);
        tickBroadcast2 = new TickBroadcast(1);
        List<Model> modelList = new LinkedList<Model>();
        modelList.add(model);
        publishConferenceBroadcast2 = new PublishConferenceBroadcast(modelList);

        //complete
        exampleEvent3 = new ExampleEvent(" ");
        confrenceInformation3 = new ConfrenceInformation(" ", 200);
        conferenceService3 = new ConferenceService(confrenceInformation3);


        //send broadcast
        tickBroadcast4= new TickBroadcast(1);
        studentService4= new StudentService(" ", student);
        msgBus.register(studentService4);
        msgBus.subscribeBroadcast(tickBroadcast4.getClass(),studentService4);

        //send event
        publishResultsEvent5 = new PublishResultsEvent(model);
        studentService5 = new StudentService(" ", student);


        //register
        cpuService6 = new CPUService("", cpu);

        //unregister
        studentService7 = new StudentService(" ", student);
        trainModelEvent7 = new TrainModelEvent(model);
        tickBroadcast7 = new TickBroadcast(1);
        msgBus.register(studentService7);
        msgBus.subscribeEvent(trainModelEvent7.getClass(),studentService7);
        msgBus.subscribeBroadcast(tickBroadcast7.getClass(),studentService7);
        msgBus.sendEvent(trainModelEvent7);
        msgBus.sendBroadcast(tickBroadcast7);

        //await message
        studentService8 = new StudentService(" ", student);
        msgBus.register(studentService8);
        trainModelEvent8 = new TrainModelEvent(model);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void subscribeEvent() {
        //// pre ////
        ConcurrentHashMap<Class<? extends Event<?>>, LinkedBlockingQueue<MicroService>> ServicesSubscribeToEvent = msgBus.getServicesSubscribeToEvent();
        assertNotNull(ServicesSubscribeToEvent);
        LinkedBlockingQueue<MicroService> eventList = ServicesSubscribeToEvent.get(trainEvent1.getClass());
        if (eventList == null){
            msgBus.subscribeEvent(trainEvent1.getClass(), studentService1);
            eventList = ServicesSubscribeToEvent.get(trainEvent1.getClass());
            assertNotNull(eventList);
            assertEquals(1, eventList.size());
        }
        int preSizeOfEvent = eventList.size();
        msgBus.subscribeEvent(trainEvent1.getClass(), gpuService1);
        assertEquals(preSizeOfEvent + 1, eventList.size());
        //// post ////
        ConcurrentHashMap<MicroService, LinkedBlockingQueue<Class<? extends Event<?>>>> EventsThatServiceRegisteredTo = msgBus.getEventsThatServiceRegisteredTo();
        assertNotNull(EventsThatServiceRegisteredTo);
        LinkedBlockingQueue<Class<? extends Event<?>>> serviceListOfEvents = EventsThatServiceRegisteredTo.get(gpuService1);
        if (serviceListOfEvents == null){
            msgBus.subscribeEvent(testModelEvent1.getClass(), gpuService1);
            serviceListOfEvents = EventsThatServiceRegisteredTo.get(gpuService1);
            assertNotNull(serviceListOfEvents);
            assertEquals(1, serviceListOfEvents.size());
        }
        int preSizeOfService = serviceListOfEvents.size();
        msgBus.subscribeEvent(testModelEvent1.getClass(), studentService1);
        serviceListOfEvents = EventsThatServiceRegisteredTo.get(gpuService1);
        assertEquals(preSizeOfService, serviceListOfEvents.size());
    }

    @Test
    public void subscribeBroadcast() {
        //// pre ////
        ConcurrentHashMap<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> ServicesSubscribeToBroadcast = msgBus.getServicesSubscribeToBroadcast();
        assertNotNull(ServicesSubscribeToBroadcast);
        LinkedBlockingQueue<MicroService> broadcastList = ServicesSubscribeToBroadcast.get(tickBroadcast2.getClass());
        if (broadcastList == null){
            msgBus.subscribeBroadcast(tickBroadcast2.getClass(), studentService2);
            broadcastList = ServicesSubscribeToBroadcast.get(tickBroadcast2.getClass());
            assertNotNull(ServicesSubscribeToBroadcast.get(tickBroadcast2.getClass()));
            assertEquals(1, broadcastList.size());
        }
        int preSizeOfBroadcast = broadcastList.size();
        msgBus.subscribeBroadcast(tickBroadcast2.getClass(), gpuService2);
        assertEquals(preSizeOfBroadcast + 1, broadcastList.size());
        //// post ////
        ConcurrentHashMap<MicroService, LinkedBlockingQueue<Class<? extends Broadcast>>> BroadcastsThatServiceRegisteredTo = msgBus.getBroadcastsThatServiceRegisteredTo();
        assertNotNull(BroadcastsThatServiceRegisteredTo);
        LinkedBlockingQueue<Class<? extends Broadcast>> serviceListOfBroadcast = BroadcastsThatServiceRegisteredTo.get(gpuService2);
        if (serviceListOfBroadcast == null){
            msgBus.subscribeBroadcast(publishConferenceBroadcast2.getClass(), gpuService2);
            assertNotNull(BroadcastsThatServiceRegisteredTo.get(gpuService2));
            serviceListOfBroadcast = BroadcastsThatServiceRegisteredTo.get(gpuService2);
            assertNotNull(serviceListOfBroadcast);
            assertEquals(1, serviceListOfBroadcast.size());
        }
        int preSizeOfService = serviceListOfBroadcast.size();
        msgBus.subscribeBroadcast(publishConferenceBroadcast2.getClass(), studentService2);
        assertEquals(preSizeOfService, serviceListOfBroadcast.size());
    }

    @Test
    public void complete() {
        msgBus.register(conferenceService3);
        msgBus.subscribeEvent(exampleEvent3.getClass(),conferenceService3);
        msgBus.sendEvent(exampleEvent3);
        //// pre ////
        ConcurrentHashMap<Event<?>, MicroService> EventToService = msgBus.getEventToService();
        assertNotNull(EventToService);
        assertNotNull(EventToService.get(exampleEvent3));


        ConcurrentHashMap<Class<? extends Event<?>>, LinkedBlockingQueue<MicroService>> ServicesSubscribeToEvent = msgBus.getServicesSubscribeToEvent();
        assertNotNull(ServicesSubscribeToEvent);
        LinkedBlockingQueue<MicroService> eventList = ServicesSubscribeToEvent.get(exampleEvent3.getClass());
        assertNotNull(eventList);

        ConcurrentHashMap<MicroService, LinkedBlockingQueue<Event<?>>> ServiceToEvent = msgBus.getServiceToEvent();
        assertNotNull(ServiceToEvent);
        ConcurrentHashMap<MicroService, BlockingQueue<Message>> ServiceToQueue = msgBus.getServiceToQueue();
        assertNotNull(ServiceToQueue);
        int index = 0;
        int[] preEventListSize = new int[eventList.size()];
        for (MicroService ser: eventList){
            LinkedBlockingQueue<Event<?>> serviceListOfEvents = ServiceToEvent.get(ser);
            if(serviceListOfEvents == null){
                preEventListSize[index] = 0;
            }
            else{
                preEventListSize[index] = serviceListOfEvents.size();
                boolean found = false;
                for (Event<?> event: serviceListOfEvents){
                    if (event.equals(exampleEvent3)){
                        found = true;
                        break;
                    }
                }
                assertTrue(found);
            }
            index++;
            assertFalse(ServiceToQueue.get(ser).isEmpty());
        }

        ConcurrentHashMap<Event<?>, Future<?>> EventToFuture = msgBus.getEventToFuture();
        assertNotNull(EventToFuture);
        assertNotNull(EventToFuture.get(exampleEvent3));

        // check queue size
        int[] queueSize = new int[eventList.size()];
        int Qindex = 0;
        for (MicroService serviceRegisteredToEvent: eventList){
            Queue<Message> serviceQueue = ServiceToQueue.get(serviceRegisteredToEvent);
            assertNotNull(serviceQueue);
            queueSize[Qindex] = serviceQueue.size();
            Qindex++;
        }

        msgBus.complete(exampleEvent3," ");

        //// post ////
        assertNull(EventToService.get(exampleEvent3));
        index = 0;
        for (MicroService ser: eventList){
            LinkedBlockingQueue<Event<?>> serviceListOfEvents = ServiceToEvent.get(ser);
            if (serviceListOfEvents != null){
                assertEquals(preEventListSize[index] - 1, serviceListOfEvents.size());
            }
            index++;
        }

        // check queue size
        Qindex = 0;
        for (MicroService serviceRegisteredToEvent: eventList){
            Queue<Message> serviceQueue = ServiceToQueue.get(serviceRegisteredToEvent);
            assertEquals(queueSize[Qindex] - 1, serviceQueue.size());
            Qindex++;
        }
    }

    @Test
    public void sendBroadcast() {
        //// pre ////
        ConcurrentHashMap<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> ServicesSubscribeToBroadcast = msgBus.getServicesSubscribeToBroadcast();
        assertNotNull(ServicesSubscribeToBroadcast);
        LinkedBlockingQueue<MicroService> broadcastList = ServicesSubscribeToBroadcast.get(tickBroadcast4.getClass());
        assertNotNull(broadcastList);
        int[] queueSize = new int[broadcastList.size()];
        int index = 0;
        ConcurrentHashMap<MicroService, BlockingQueue<Message>> ServiceToQueue = msgBus.getServiceToQueue();
        for (MicroService serviceRegisteredToBroadcast: broadcastList){
            Queue<Message> serviceQueue = ServiceToQueue.get(serviceRegisteredToBroadcast);
            assertNotNull(serviceQueue);
            queueSize[index] = serviceQueue.size();
            index++;
        }
        //// post ////
        msgBus.sendBroadcast(tickBroadcast4);
        index = 0;
        for (MicroService serviceRegisteredToBroadcast: broadcastList){
            Queue<Message> serviceQueue = ServiceToQueue.get(serviceRegisteredToBroadcast);
            assertEquals(queueSize[index] + 1, serviceQueue.size());
            index++;
        }
    }

    @Test
    public void sendEvent() {
        msgBus.register(studentService5);
        msgBus.subscribeEvent(publishResultsEvent5.getClass(),studentService5);
        //// pre ////
        ConcurrentHashMap<Class<? extends Event<?>>, LinkedBlockingQueue<MicroService>> ServicesSubscribeToEvent = msgBus.getServicesSubscribeToEvent();
        assertNotNull(ServicesSubscribeToEvent);
        LinkedBlockingQueue<MicroService> eventList = ServicesSubscribeToEvent.get(publishResultsEvent5.getClass());
        assertNotNull(eventList);

        ConcurrentHashMap<MicroService, LinkedBlockingQueue<Event<?>>> ServiceToEvent = msgBus.getServiceToEvent();
        assertNotNull(ServiceToEvent);

        int index = 0;
        int[] preEventListSize = new int[eventList.size()];
        for (MicroService ser: eventList){
            LinkedBlockingQueue<Event<?>> serviceListOfEvents = ServiceToEvent.get(ser);
            if(serviceListOfEvents == null){
                preEventListSize[index] = 0;
            }
            else{
                preEventListSize[index] = serviceListOfEvents.size();
                for (Event<?> event: serviceListOfEvents){
                    assertNotEquals(event, publishResultsEvent5);
                }
            }
            index++;
        }
        ConcurrentHashMap<Event<?>, MicroService> EventToService = msgBus.getEventToService();
        assertNotNull(EventToService);
        assertNull(EventToService.get(publishResultsEvent5));

        ConcurrentHashMap<Event<?>, Future<?>> EventToFuture = msgBus.getEventToFuture();
        assertNotNull(EventToFuture);
        assertNull(EventToFuture.get(publishResultsEvent5));

        //check pre queue size
        int[] queueSize = new int[eventList.size()];
        int Qindex = 0;
        ConcurrentHashMap<MicroService, BlockingQueue<Message>> ServiceToQueue = msgBus.getServiceToQueue();
        for (MicroService serviceRegisteredToEvent: eventList){
            Queue<Message> serviceQueue = ServiceToQueue.get(serviceRegisteredToEvent);
            assertNotNull(serviceQueue);
            queueSize[Qindex] = serviceQueue.size();
            Qindex++;
        }

        //// post ////
        msgBus.sendEvent(publishResultsEvent5);
        assertNotNull(EventToService.get(publishResultsEvent5));
        // check service list size
        index = 0;
        for (MicroService ser: eventList){
            LinkedBlockingQueue<Event<?>> serviceListOfEvents = ServiceToEvent.get(ser);
            assertEquals(preEventListSize[index] + 1, serviceListOfEvents.size());
            index++;
        }
        // check queue size
        Qindex = 0;
        for (MicroService serviceRegisteredToEvent: eventList){
            Queue<Message> serviceQueue = ServiceToQueue.get(serviceRegisteredToEvent);
            assertEquals(queueSize[Qindex] + 1, serviceQueue.size());
            Qindex++;
        }
        assertNotNull(EventToFuture.get(publishResultsEvent5));
    }

    @Test
    public void register() {
        ConcurrentHashMap<MicroService, BlockingQueue<Message>> ServiceToQueue = msgBus.getServiceToQueue();
        assertNotNull(ServiceToQueue);
        assertNull(ServiceToQueue.get(cpuService6));
        msgBus.register(cpuService6);
        assertNotNull(ServiceToQueue.get(cpuService6));
        assertTrue(ServiceToQueue.get(cpuService6).isEmpty());
    }

    @Test
    public void unregister() {
        //// pre ////
        ConcurrentHashMap<MicroService, LinkedBlockingQueue<Class<? extends Event<?>>>> EventsThatServiceRegisteredTo = msgBus.getEventsThatServiceRegisteredTo();
        assertNotNull(EventsThatServiceRegisteredTo);
        ConcurrentHashMap<Class<? extends Event<?>>, LinkedBlockingQueue<MicroService>> ServicesSubscribeToEvent = msgBus.getServicesSubscribeToEvent();
        assertNotNull(ServicesSubscribeToEvent);

        int[] eventListSize = null;
        int eventListIndex = 0;
        LinkedBlockingQueue<Class<? extends Event<?>>> eventList = null;
        if (EventsThatServiceRegisteredTo.get(studentService7) != null){
            eventList = EventsThatServiceRegisteredTo.get(studentService7);
            eventListSize = new int[eventList.size()];
            for (Class<?> event: eventList){
                eventListSize[eventListIndex] = ServicesSubscribeToEvent.get(event).size();
                eventListIndex++;
            }
        }

        ConcurrentHashMap<MicroService, LinkedBlockingQueue<Class<? extends Broadcast>>> BroadcastsThatServiceRegisteredTo = msgBus.getBroadcastsThatServiceRegisteredTo();
        assertNotNull(BroadcastsThatServiceRegisteredTo);
        ConcurrentHashMap<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> ServicesSubscribeToBroadcast = msgBus.getServicesSubscribeToBroadcast();
        assertNotNull(ServicesSubscribeToBroadcast);
        int[] BroadcastListSize = null;
        int BroadcastListIndex = 0;
        LinkedBlockingQueue<Class<? extends Broadcast>> BroadcastList = null;
        if (BroadcastsThatServiceRegisteredTo.get(studentService7) != null){
            BroadcastList = BroadcastsThatServiceRegisteredTo.get(studentService7);
            BroadcastListSize = new int[BroadcastList.size()];
            for (Class<?> broadcast: BroadcastList){
                BroadcastListSize[BroadcastListIndex] = ServicesSubscribeToBroadcast.get(broadcast).size();
                BroadcastListIndex++;
            }
        }


        ConcurrentHashMap<MicroService, BlockingQueue<Message>> ServiceToQueue = msgBus.getServiceToQueue();
        assertNotNull(ServiceToQueue);
        assertNotNull(ServiceToQueue.get(studentService7));
        Queue<Message> ServiceQueue = ServiceToQueue.get(studentService7);

        msgBus.unregister(studentService7);

        //// post ////

        eventListIndex = 0;
        if (eventListSize != null && eventList != null) {
            for (Class<?> event : eventList) {
                assertEquals(eventListSize[eventListIndex] - 1, ServicesSubscribeToEvent.get(event).size());
                eventListIndex++;
            }
        }

        BroadcastListIndex = 0;
        if (BroadcastListSize != null && BroadcastList != null) {
            for (Class<?> event : BroadcastList) {
                assertEquals(BroadcastListSize[BroadcastListIndex] - 1, ServicesSubscribeToBroadcast.get(event).size());
                BroadcastListIndex++;
            }
        }

        ConcurrentHashMap<Event<?>, MicroService> EventToService = msgBus.getEventToService();
        assertNotNull(EventToService);
        assertFalse(EventToService.contains(trainModelEvent7));

        ConcurrentHashMap<MicroService, LinkedBlockingQueue<Event<?>>> ServiceToEvent = msgBus.getServiceToEvent();
        assertNotNull(ServiceToEvent);
        assertNull(ServiceToEvent.get(studentService7));

        assertTrue(ServiceQueue.isEmpty());
        assertNull(ServiceToQueue.get(studentService7));

        assertNull(EventsThatServiceRegisteredTo.get(studentService7));

        assertNull(BroadcastsThatServiceRegisteredTo.get(studentService7));
    }

    @Test
    public void awaitMessage() {
        //test wait when queue is empty
        //// pre ////
        ConcurrentHashMap<MicroService, BlockingQueue<Message>> ServiceToQueue = msgBus.getServiceToQueue();
        assertNotNull(ServiceToQueue);
        assertNotNull(ServiceToQueue.get(studentService8));
        int queueSize = ServiceToQueue.get(studentService8).size();
        Thread t = new Thread(() -> {
            try {
                sleep(4000);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
            ServiceToQueue.get(studentService8).add(trainModelEvent8);
        });
        t.start();
        try {
            msgBus.awaitMessage(studentService8);
        }
        catch (Exception e){
            fail();
        }

        //// post ////
        assertEquals(queueSize , ServiceToQueue.get(studentService8).size());


        //test 2 if queue is not empty
        //pre
        ServiceToQueue.get(studentService8).add(trainModelEvent8);
        queueSize = ServiceToQueue.get(studentService8).size();
        try {
            msgBus.awaitMessage(studentService8);
        }
        catch (Exception e){
            fail();
        }
        assertEquals(queueSize -1, ServiceToQueue.get(studentService8).size());
    }

    @Test
    public void getServiceToQueue() {
    }

    @Test
    public void getServicesSubscribeToEvent() {
    }

    @Test
    public void getServicesSubscribeToBroadcast() {
    }

    @Test
    public void getEventToFuture() {
    }

    @Test
    public void getEventToService() {
    }

    @Test
    public void getServiceToEvent() {
    }

    @Test
    public void getEventsThatServiceRegisteredTo() {
    }

    @Test
    public void getBroadcastsThatServiceRegisteredTo() {
    }
}