package com.salman.bitclock.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.salman.bitclock.data.models.Timer;

import java.util.List;

@Dao
public interface TimerDao {
    @Query("SELECT * FROM timers")
    LiveData<List<Timer>> getAllTimers();

    @Insert
    void insert(Timer timer);

    @Update
    void update(Timer timer);

    @Delete
    void delete(Timer timer);
}
