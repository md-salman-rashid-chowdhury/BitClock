package com.salman.bitclock.data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Objects;

@Entity(tableName = "alarms")
public class Alarm {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "hour")
    private int hour;

    @ColumnInfo(name = "minute")
    private int minute;

    @ColumnInfo(name = "is_enabled")
    private boolean enabled;

    @ColumnInfo(name = "label")
    private String label;

    @ColumnInfo(name = "repeat_days")
    private int repeatDays; // Bitmask for repeating days

    @ColumnInfo(name = "snooze_duration")
    private int snoozeMinutes;

    @ColumnInfo(name = "sound_uri")
    private String soundUri;

    @ColumnInfo(name = "vibrate")
    private boolean vibrate;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(int repeatDays) {
        this.repeatDays = repeatDays;
    }

    public int getSnoozeMinutes() {
        return snoozeMinutes;
    }

    public void setSnoozeMinutes(int snoozeMinutes) {
        this.snoozeMinutes = snoozeMinutes;
    }

    public String getSoundUri() {
        return soundUri;
    }

    public void setSoundUri(String soundUri) {
        this.soundUri = soundUri;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods

    public boolean isRepeating() {
        return repeatDays != 0;
    }

    public long getNextAlarmTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (!isRepeating()) {
            return calendar.getTimeInMillis();
        }

        for (int i = 0; i < 7; i++) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // SUNDAY = 1, MONDAY = 2, ...
            int dayBit = 1 << ((dayOfWeek + 5) % 7); // Convert to Mon-Sun bitmask

            if ((repeatDays & dayBit) != 0) {
                return calendar.getTimeInMillis();
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return 0; // Should not happen
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alarm alarm = (Alarm) o;
        return id == alarm.id &&
                hour == alarm.hour &&
                minute == alarm.minute &&
                enabled == alarm.enabled &&
                repeatDays == alarm.repeatDays &&
                snoozeMinutes == alarm.snoozeMinutes &&
                vibrate == alarm.vibrate &&
                createdAt == alarm.createdAt &&
                Objects.equals(label, alarm.label) &&
                Objects.equals(soundUri, alarm.soundUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hour, minute, enabled, label, repeatDays, snoozeMinutes, soundUri, vibrate, createdAt);
    }
}
