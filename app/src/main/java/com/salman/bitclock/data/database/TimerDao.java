package com.salman.bitclock.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.salman.bitclock.data.models.Timer;
import java.util.List;

/**
 * Data Access Object for Timer entities.
 */
@Dao
public interface TimerDao {
    
    /**
     * Gets all timers as LiveData to observe database changes.
     */
    @Query("SELECT * FROM timers")
    LiveData<List<Timer>> getAllTimers();

    /**
     * Synchronously gets all timers from the database.
     */
    @Query("SELECT * FROM timers")
    List<Timer> getAllTimersSync();

    @Insert
    void insert(Timer timer);

    @Update
    void update(Timer timer);

    @Delete
    void delete(Timer timer);
}
