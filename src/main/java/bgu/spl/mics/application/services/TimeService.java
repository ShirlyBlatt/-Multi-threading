package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private int speed;
	private int duration;
	private int currentTime;
	private Timer globalTime;
	private TimerTask task;

	public TimeService(int duration, int speed) {
		super("Time Service");
		this.duration = duration;
		this.speed = speed;
		this.currentTime = 1;
		this.globalTime = new Timer(true);
		task = new TimerTask() {
			@Override
			public void run() {
				currentTime++;
				if (currentTime == duration){
					globalTime.cancel();
					sendBroadcast(new TickBroadcast(-1));
				}
				else{
					sendBroadcast(new TickBroadcast(currentTime));
				}
			}
		};
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, c -> {
			if (c.getData() == -1){
				this.terminate();
			}
		});
		globalTime.scheduleAtFixedRate(task, 0, this.speed);
	}

}
