package com.salman.bitclock.data.models;

public class Lap {
    public int lapNumber;
    public long lapTime;
    public long overallTime;

    public Lap(int lapNumber, long lapTime, long overallTime) {
        this.lapNumber = lapNumber;
        this.lapTime = lapTime;
        this.overallTime = overallTime;
    }
}
