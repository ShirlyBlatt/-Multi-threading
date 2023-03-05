package bgu.spl.mics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//fields
	private static class SingeltonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> ServiceToQueue;
	private ConcurrentHashMap<Class<? extends Event<?>>, LinkedBlockingQueue<MicroService>> ServicesSubscribeToEvent;
	private ConcurrentHashMap<Class<? extends Event<?>>, BlockingQueue<MicroService>> eventToRoundRobingQueue;
	private ConcurrentHashMap<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> ServicesSubscribeToBroadcast;
	private ConcurrentHashMap<Event<?>, Future<?>> EventToFuture;
	private ConcurrentHashMap<Event<?>, MicroService> EventToService;
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Event<?>>> ServiceToEvent;
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Class<? extends Event<?>>>> EventsThatServiceRegisteredTo;
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Class<? extends Broadcast>>> BroadcastsThatServiceRegisteredTo;

	private MessageBusImpl() {
		ServiceToQueue = new ConcurrentHashMap<MicroService, BlockingQueue<Message>>();
		ServicesSubscribeToEvent = new ConcurrentHashMap<Class<? extends Event<?>>, LinkedBlockingQueue<MicroService>>();
		eventToRoundRobingQueue = new ConcurrentHashMap<Class<? extends Event<?>>, BlockingQueue<MicroService>>();
		ServicesSubscribeToBroadcast = new ConcurrentHashMap<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>>();
		EventToFuture = new ConcurrentHashMap<Event<?>, Future<?>>();
		EventToService = new ConcurrentHashMap<Event<?>, MicroService>();
		ServiceToEvent = new ConcurrentHashMap<MicroService, LinkedBlockingQueue<Event<?>>>();
		EventsThatServiceRegisteredTo = new ConcurrentHashMap<MicroService, LinkedBlockingQueue<Class<? extends Event<?>>>>();
		BroadcastsThatServiceRegisteredTo = new ConcurrentHashMap<MicroService, LinkedBlockingQueue<Class<? extends Broadcast>>>();
	}

	public static MessageBusImpl getInstance(){
		return SingeltonHolder.instance;
	}

	//Getters
	public ConcurrentHashMap<MicroService, BlockingQueue<Message>> getServiceToQueue() {
		return ServiceToQueue;
	}

	public ConcurrentHashMap<Class<? extends Event<?>>, LinkedBlockingQueue<MicroService>> getServicesSubscribeToEvent() {
		return ServicesSubscribeToEvent;
	}

	public ConcurrentHashMap<Class<? extends Event<?>>, BlockingQueue<MicroService>> getRoundRobingQueue() {
		return eventToRoundRobingQueue;
	}

	public ConcurrentHashMap<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> getServicesSubscribeToBroadcast() {
		return ServicesSubscribeToBroadcast;
	}

	public ConcurrentHashMap<Event<?>, Future<?>> getEventToFuture() {
		return EventToFuture;
	}

	public ConcurrentHashMap<Event<?>, MicroService> getEventToService() {
		return EventToService;
	}

	public ConcurrentHashMap<MicroService, LinkedBlockingQueue<Event<?>>> getServiceToEvent() {
		return ServiceToEvent;
	}

	public ConcurrentHashMap<MicroService, LinkedBlockingQueue<Class<? extends Event<?>>>> getEventsThatServiceRegisteredTo() {
		return EventsThatServiceRegisteredTo;
	}

	public ConcurrentHashMap<MicroService, LinkedBlockingQueue<Class<? extends Broadcast>>> getBroadcastsThatServiceRegisteredTo() {
		return BroadcastsThatServiceRegisteredTo;
	}


	@Override
	public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (ServicesSubscribeToEvent.get(type) != null) {
			if (!ServicesSubscribeToEvent.get(type).contains(m)) {
				try {
					ServicesSubscribeToEvent.get(type).put(m);
				}catch (InterruptedException ignored){}

			}
		}
		else{
			ServicesSubscribeToEvent.put(type, new LinkedBlockingQueue<MicroService>());
			try {
				ServicesSubscribeToEvent.get(type).put(m);
			}catch (InterruptedException ignored){}
		}
		if (EventsThatServiceRegisteredTo.get(m) != null) {
			if (!EventsThatServiceRegisteredTo.get(m).contains(type)) {
				try {
					EventsThatServiceRegisteredTo.get(m).put(type);
				}catch (InterruptedException ignored){}
			}
		}
		else{
			EventsThatServiceRegisteredTo.put(m, new LinkedBlockingQueue<Class<? extends Event<?>>>());
			try {
				EventsThatServiceRegisteredTo.get(m).put(type);
			}catch (InterruptedException ignored){}
		}
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (ServicesSubscribeToBroadcast.get(type) != null) {
			if (!ServicesSubscribeToBroadcast.get(type).contains(m)) {
				try {
					ServicesSubscribeToBroadcast.get(type).put(m);
				}catch (InterruptedException ignored){}
			}
		}
		else{
			ServicesSubscribeToBroadcast.put(type, new LinkedBlockingQueue<MicroService>());
			try {
				ServicesSubscribeToBroadcast.get(type).put(m);
			}catch (InterruptedException ignored){}
		}
		if (BroadcastsThatServiceRegisteredTo.get(m) != null) {
			if (!BroadcastsThatServiceRegisteredTo.get(m).contains(type)) {
				try {
					BroadcastsThatServiceRegisteredTo.get(m).put(type);
				}catch (InterruptedException ignored){}
			}
		}
		else{
			BroadcastsThatServiceRegisteredTo.put(m, new LinkedBlockingQueue<Class<? extends Broadcast>>());
			try {
				BroadcastsThatServiceRegisteredTo.get(m).put(type);
			}catch (InterruptedException ignored){}
		}

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		MicroService ms = EventToService.get(e);
		ServiceToEvent.get(ms).remove(e);
		if (ServiceToEvent.get(ms).isEmpty()){
			ServiceToEvent.remove(ms);
		}
		ServiceToQueue.get(ms).remove(e);
		EventToService.remove(e);
		Future<T> futureEvent = (Future<T>) EventToFuture.get(e);
		futureEvent.resolve(result);
		EventToFuture.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		LinkedBlockingQueue<MicroService> serviceList = ServicesSubscribeToBroadcast.get(b.getClass());
		if (serviceList != null){
			for (MicroService ms: serviceList){
				if (ServiceToQueue.get(ms) != null){
					try {
						ServiceToQueue.get(ms).put((Message) b);
					}
					catch (Exception ignored){}
				}
			}
		}
	}

	private  MicroService chooseMicroService(Event<?> e,LinkedBlockingQueue<MicroService> listOfServices){
		if (eventToRoundRobingQueue.get(e.getClass()) == null){
			eventToRoundRobingQueue.put((Class<? extends Event<?>>) e.getClass(), listOfServices);
		}
		if (!eventToRoundRobingQueue.get(e.getClass()).isEmpty()) {
			MicroService result = eventToRoundRobingQueue.get(e.getClass()).poll();
			eventToRoundRobingQueue.get(e.getClass()).add(result);
			return result;
		}
		return null;
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		LinkedBlockingQueue<MicroService> serviceList = ServicesSubscribeToEvent.get(e.getClass());
		if (serviceList != null){
			MicroService ms = chooseMicroService(e, serviceList);
			EventToService.putIfAbsent(e, ms);
			if (ServiceToEvent.get(ms) != null){
				if (!ServiceToEvent.get(ms).contains(e)){
					ServiceToEvent.get(ms).add(e);
				}
			}
			else{
				ServiceToEvent.put(ms, new LinkedBlockingQueue<Event<?>>());
				ServiceToEvent.get(ms).add(e);
			}
			if (ServiceToQueue.get(ms) != null){
				try {
					ServiceToQueue.get(ms).put((Message) e);
				}
				catch (Exception ignored){}
			}
			else{
				return null;
			}
			if (EventToFuture.get(e) == null){
				Future<T> resultFuture = new Future<T>();
				EventToFuture.put(e, resultFuture);
				return resultFuture;
			}
			else{
				return null;
			}
		}
		else{
			return null;
		}
	}

	@Override
	public void register(MicroService m) {
		if (ServiceToQueue.get(m) == null){
			BlockingQueue<Message> serviceQueue = new LinkedBlockingQueue<>();
			ServiceToQueue.put(m, serviceQueue);
		}
	}

	@Override
	public void unregister(MicroService m) {
		if (EventsThatServiceRegisteredTo.get(m) != null) {
			for (Class<? extends Event<?>> event : EventsThatServiceRegisteredTo.get(m)) {
					if (ServicesSubscribeToEvent.get(event) != null) {
						synchronized (ServicesSubscribeToEvent.get(event)) {
							ServicesSubscribeToEvent.get(event).remove(m);
							if (ServicesSubscribeToEvent.get(event).isEmpty()) {
								ServicesSubscribeToEvent.remove(event);
							}
						}
					}
			}
			EventsThatServiceRegisteredTo.remove(m);
		}

		if (BroadcastsThatServiceRegisteredTo.get(m) != null) {
			for (Class<? extends Broadcast> broadcast : BroadcastsThatServiceRegisteredTo.get(m)) {
				if (ServicesSubscribeToBroadcast.get(broadcast) != null) {
					ServicesSubscribeToBroadcast.get(broadcast).remove(m);
					if (ServicesSubscribeToBroadcast.get(broadcast).isEmpty()) {
						ServicesSubscribeToBroadcast.remove(broadcast);
					}
				}
				BroadcastsThatServiceRegisteredTo.remove(m);
			}

		}
		if (ServiceToEvent.get(m) != null){
			for (Event<?> event: ServiceToEvent.get(m)){
				EventToService.remove(event);
			}
			ServiceToEvent.remove(m);
		}

		ServiceToQueue.get(m).clear();
		ServiceToQueue.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return ServiceToQueue.get(m).take();
	}

	

}
