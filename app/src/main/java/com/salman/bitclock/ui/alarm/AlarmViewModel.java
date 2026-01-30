package com.salman.bitclock.ui.alarm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.salman.bitclock.data.repository.AlarmRepository;
import com.salman.bitclock.data.models.Alarm;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

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

    public LiveData<List<Alarm>> getAllAlarms() {
        return allAlarms;
    }

    public LiveData<Alarm> getAlarmById(int id) {
        return alarmRepository.getAlarmById(id);
    }

    public Future<Long> insert(Alarm alarm) {
        Callable<Long> insertCallable = () -> alarmRepository.insert(alarm);
        return executorService.submit(insertCallable);
    }

    public Future<Void> update(Alarm alarm) {
        Callable<Void> updateCallable = () -> {
            alarmRepository.update(alarm);
            return null;
        };
        return executorService.submit(updateCallable);
    }

    public Future<Void> delete(Alarm alarm) {
        Callable<Void> deleteCallable = () -> {
            alarmRepository.delete(alarm);
            return null;
        };
        return executorService.submit(deleteCallable);
    }
}
