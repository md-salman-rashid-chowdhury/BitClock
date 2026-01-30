package com.salman.bitclock.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.salman.bitclock.data.models.Sleep;

@Dao
public interface SleepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Sleep sleep);

    @Query("SELECT * FROM sleep WHERE id = 1")
    LiveData<Sleep> getSleepSchedule();

}
