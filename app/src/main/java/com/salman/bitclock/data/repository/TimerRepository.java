package com.salman.bitclock.data.repository;

import androidx.lifecycle.LiveData;

import com.salman.bitclock.data.database.TimerDao;
import com.salman.bitclock.data.models.Timer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TimerRepository {

    private final TimerDao timerDao;
    private final LiveData<List<Timer>> allTimers;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Inject
    public TimerRepository(TimerDao timerDao) {
        this.timerDao = timerDao;
        this.allTimers = timerDao.getAllTimers();
    }

    public LiveData<List<Timer>> getAllTimers() {
        return allTimers;
    }

    public void insert(Timer timer) {
        executorService.execute(() -> timerDao.insert(timer));
    }

    public void update(Timer timer) {
        executorService.execute(() -> timerDao.update(timer));
    }

    public void delete(Timer timer) {
        executorService.execute(() -> timerDao.delete(timer));
    }
}
