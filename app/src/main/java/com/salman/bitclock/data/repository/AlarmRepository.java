package com.salman.bitclock.data.repository;

import androidx.lifecycle.LiveData;
import com.salman.bitclock.data.AppStateManager;
import com.salman.bitclock.data.database.AlarmDao;
import com.salman.bitclock.data.models.Alarm;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Optimized Repository for Alarm data.
 * Uses AppStateManager (HashMap) to cache database results in-memory, 
 * significantly reducing CPU and disk I/O usage.
 */
@Singleton
public class AlarmRepository {

    private final AlarmDao alarmDao;
    private final AppStateManager stateManager;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    // Key for in-memory cache
    private static final String ALARMS_KEY = "alarms_list";

    @Inject
    public AlarmRepository(AlarmDao alarmDao, AppStateManager stateManager) {
        this.alarmDao = alarmDao;
        this.stateManager = stateManager;
    }

    /**
     * Returns LiveData from the DB for real-time UI updates.
     */
    public LiveData<List<Alarm>> getAllAlarms() {
        return alarmDao.getAllAlarms();
    }

    /**
     * Returns LiveData for a specific alarm from the DB.
     */
    public LiveData<Alarm> getAlarmById(int id) {
        return alarmDao.getAlarmById(id);
    }

    /**
     * Fast access: Checks memory first, then the database.
     */
    public List<Alarm> getAllAlarmsSync() {
        List<Alarm> alarms = (List<Alarm>) stateManager.getData(ALARMS_KEY);
        if (alarms == null) {
            alarms = alarmDao.getAllAlarmsNonLiveData();
            stateManager.saveData(ALARMS_KEY, alarms);
        }
        return alarms;
    }

    /**
     * Saves data and clears the memory cache.
     */
    public void insert(Alarm alarm) {
        executorService.execute(() -> {
            alarmDao.insert(alarm);
            stateManager.removeData(ALARMS_KEY);
        });
    }

    public void update(Alarm alarm) {
        executorService.execute(() -> {
            alarmDao.update(alarm);
            stateManager.removeData(ALARMS_KEY);
        });
    }

    public void delete(Alarm alarm) {
        executorService.execute(() -> {
            alarmDao.delete(alarm);
            stateManager.removeData(ALARMS_KEY);
        });
    }

    public Alarm getAlarmByIdSync(int id) {
        return alarmDao.getAlarmByIdSync(id);
    }
}
