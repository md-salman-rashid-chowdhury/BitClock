package com.salman.bitclock.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sleep")
public class Sleep {

    @PrimaryKey
    private int id = 1; // Only one sleep schedule

    private int bedtimeHour;
    private int bedtimeMinute;
    private int wakeupHour;
    private int wakeupMinute;
    private boolean isEnabled;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBedtimeHour() {
        return bedtimeHour;
    }

    public void setBedtimeHour(int bedtimeHour) {
        this.bedtimeHour = bedtimeHour;
    }

    public int getBedtimeMinute() {
        return bedtimeMinute;
    }

    public void setBedtimeMinute(int bedtimeMinute) {
        this.bedtimeMinute = bedtimeMinute;
    }

    public int getWakeupHour() {
        return wakeupHour;
    }

    public void setWakeupHour(int wakeupHour) {
        this.wakeupHour = wakeupHour;
    }

    public int getWakeupMinute() {
        return wakeupMinute;
    }

    public void setWakeupMinute(int wakeupMinute) {
        this.wakeupMinute = wakeupMinute;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
