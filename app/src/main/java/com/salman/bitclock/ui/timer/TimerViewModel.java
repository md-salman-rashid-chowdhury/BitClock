package com.salman.bitclock.ui.timer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.salman.bitclock.data.models.Timer;
import com.salman.bitclock.data.repository.TimerRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TimerViewModel extends ViewModel {

    private final TimerRepository repository;
    private final LiveData<List<Timer>> allTimers;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Inject
    public TimerViewModel(TimerRepository repository) {
        this.repository = repository;
        this.allTimers = repository.getAllTimers();
    }

    public LiveData<List<Timer>> getAllTimers() {
        return allTimers;
    }

    public void insert(Timer timer) {
        executorService.execute(() -> repository.insert(timer));
    }

    public void update(Timer timer) {
        executorService.execute(() -> repository.update(timer));
    }

    public void delete(Timer timer) {
        executorService.execute(() -> repository.delete(timer));
    }
}
