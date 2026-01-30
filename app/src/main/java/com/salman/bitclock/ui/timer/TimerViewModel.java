package com.salman.bitclock.ui.timer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.salman.bitclock.data.models.Timer;
import com.salman.bitclock.data.repository.TimerRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TimerViewModel extends ViewModel {

    private final TimerRepository repository;
    private final LiveData<List<Timer>> allTimers;

    @Inject
    public TimerViewModel(TimerRepository repository) {
        this.repository = repository;
        this.allTimers = repository.getAllTimers();
    }

    public LiveData<List<Timer>> getAllTimers() {
        return allTimers;
    }

    public void insert(Timer timer) {
        repository.insert(timer);
    }

    public void update(Timer timer) {
        repository.update(timer);
    }

    public void delete(Timer timer) {
        repository.delete(timer);
    }
}
