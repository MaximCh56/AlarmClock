package com.example.maxim.wakeupalarm.model;



public class AlarmClock {
    private long time;
    private int id;
    private int active;


    public AlarmClock(long time,int id,int active) {
        this.time = time;
        this.id=id;
        this.active=active;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
