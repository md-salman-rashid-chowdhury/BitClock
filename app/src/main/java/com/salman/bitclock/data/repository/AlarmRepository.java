package com.salman.bitclock.data.repository;

import androidx.lifecycle.LiveData;

import com.salman.bitclock.data.database.AlarmDao;
import com.salman.bitclock.data.models.Alarm;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AlarmRepository {

    private final AlarmDao alarmDao;
    private final LiveData<List<Alarm>> allAlarms;

    @Inject
    public AlarmRepository(AlarmDao alarmDao) {
        this.alarmDao = alarmDao;
        this.allAlarms = alarmDao.getAllAlarms();
    }

    public LiveData<List<Alarm>> getAllAlarms() {
        return allAlarms;
    }

    public LiveData<Alarm> getAlarmById(int id) {
        return alarmDao.getAlarmById(id);
    }

    public Alarm getAlarmByIdSync(int alarmId) {
        return alarmDao.getAlarmByIdSync(alarmId);
    }

    public long insert(Alarm alarm) {
        return alarmDao.insert(alarm);
    }

    public void update(Alarm alarm) {
        alarmDao.update(alarm);
    }

    public void delete(Alarm alarm) {
        alarmDao.delete(alarm);
    }
}
