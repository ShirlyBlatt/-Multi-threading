package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private Integer tick;
    public TickBroadcast(Integer tick) {
        this.tick = tick;
    }

    public Integer getData(){
        return tick;
    }
}
