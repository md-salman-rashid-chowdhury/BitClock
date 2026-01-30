package com.salman.bitclock.data;

import androidx.lifecycle.LiveData;

import com.salman.bitclock.data.database.AlarmDao;
import com.salman.bitclock.data.models.Alarm;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AlarmRepository {

    private final AlarmDao alarmDao;
    private final LiveData<List<Alarm>> allAlarms;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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

    // **FIX**: Added for synchronous fetch in AlarmReceiver
    public Alarm getAlarmByIdSync(int id) {
        return alarmDao.getAlarmByIdSync(id);
    }

    public List<Alarm> getAllAlarmsNonLiveData() {
        return alarmDao.getAllAlarmsNonLiveData();
    }

    public long insert(Alarm alarm) {
        return alarmDao.insert(alarm);
    }

    public void update(Alarm alarm) {
        executorService.execute(() -> alarmDao.update(alarm));
    }

    public void delete(Alarm alarm) {
        executorService.execute(() -> alarmDao.delete(alarm));
    }
}
