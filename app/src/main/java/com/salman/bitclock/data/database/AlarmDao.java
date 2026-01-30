package com.salman.bitclock.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.salman.bitclock.data.models.Alarm;

import java.util.List;

@Dao
public interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Alarm alarm);

    @Update
    void update(Alarm alarm);

    @Delete
    void delete(Alarm alarm);

    @Query("SELECT * FROM alarms ORDER BY hour, minute ASC")
    LiveData<List<Alarm>> getAllAlarms();
    
    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    LiveData<Alarm> getAlarmById(int alarmId);

    @Query("SELECT * FROM alarms")
    List<Alarm> getAllAlarmsNonLiveData();

    // **FIX**: Added for synchronous fetch in AlarmReceiver
    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    Alarm getAlarmByIdSync(int alarmId);
}
