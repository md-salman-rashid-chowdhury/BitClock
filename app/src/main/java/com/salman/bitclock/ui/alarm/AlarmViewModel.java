package com.salman.bitclock.ui.alarm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.salman.bitclock.data.models.Alarm;
import com.salman.bitclock.data.repository.AlarmRepository;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for managing Alarms in the UI.
 * Communicates with the AlarmRepository to fetch and update data.
 */
@HiltViewModel
public class AlarmViewModel extends ViewModel {

    private final AlarmRepository alarmRepository;
    private final LiveData<List<Alarm>> allAlarms;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Inject
    public AlarmViewModel(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
        this.allAlarms = alarmRepository.getAllAlarms();
    }

    /**
     * Observable list of all alarms.
     */
    public LiveData<List<Alarm>> getAllAlarms() {
        return allAlarms;
    }

    /**
     * Gets a single alarm by ID for editing or details.
     */
    public LiveData<Alarm> getAlarmById(int id) {
        return alarmRepository.getAlarmById(id);
    }

    /**
     * Inserts a new alarm and returns a Future with the result ID.
     */
    public Future<Long> insert(Alarm alarm) {
        Callable<Long> insertCallable = () -> {
            alarmRepository.insert(alarm);
            return 0L;
        };
        return executorService.submit(insertCallable);
    }

    /**
     * Updates an existing alarm.
     */
    public Future<Void> update(Alarm alarm) {
        Callable<Void> updateCallable = () -> {
            alarmRepository.update(alarm);
            return null;
        };
        return executorService.submit(updateCallable);
    }

    /**
     * Deletes an alarm.
     */
    public Future<Void> delete(Alarm alarm) {
        Callable<Void> deleteCallable = () -> {
            alarmRepository.delete(alarm);
            return null;
        };
        return executorService.submit(deleteCallable);
    }
}
