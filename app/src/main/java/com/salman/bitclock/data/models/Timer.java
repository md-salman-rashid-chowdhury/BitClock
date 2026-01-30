package com.salman.bitclock.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timers")
public class Timer {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public long initialDurationMs;
    public long remainingMs;
    public int status; // e.g., 0 = stopped, 1 = running
}
