package com.salman.bitclock.data.repository;

import androidx.lifecycle.LiveData;
import com.salman.bitclock.data.AppStateManager;
import com.salman.bitclock.data.database.TimerDao;
import com.salman.bitclock.data.models.Timer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository to manage Timer data.
 * Uses AppStateManager (HashMap) to cache timers in memory for quick retrieval.
 */
@Singleton
public class TimerRepository {

    private final TimerDao timerDao;
    private final AppStateManager stateManager;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    // Key for storing timers in the HashMap
    private static final String TIMERS_KEY = "timers_list";

    @Inject
    public TimerRepository(TimerDao timerDao, AppStateManager stateManager) {
        this.timerDao = timerDao;
        this.stateManager = stateManager;
    }

    /**
     * Gets all timers as LiveData for the UI to observe changes.
     */
    public LiveData<List<Timer>> getAllTimers() {
        return timerDao.getAllTimers();
    }

    /**
     * Retrieves all timers from memory if cached, otherwise fetches from database.
     */
    public List<Timer> getAllTimersCached() {
        List<Timer> timers = (List<Timer>) stateManager.getData(TIMERS_KEY);
        if (timers == null) {
            timers = timerDao.getAllTimersSync(); // Assuming this method exists in TimerDao
            stateManager.saveData(TIMERS_KEY, timers);
        }
        return timers;
    }

    /**
     * Inserts a new timer and clears the cache to ensure data consistency.
     */
    public void insert(Timer timer) {
        executorService.execute(() -> {
            timerDao.insert(timer);
            stateManager.removeData(TIMERS_KEY); // Invalidate cache
        });
    }

    /**
     * Updates an existing timer and clears the cache.
     */
    public void update(Timer timer) {
        executorService.execute(() -> {
            timerDao.update(timer);
            stateManager.removeData(TIMERS_KEY); // Invalidate cache
        });
    }

    /**
     * Deletes a timer and clears the cache.
     */
    public void delete(Timer timer) {
        executorService.execute(() -> {
            timerDao.delete(timer);
            stateManager.removeData(TIMERS_KEY); // Invalidate cache
        });
    }
}
