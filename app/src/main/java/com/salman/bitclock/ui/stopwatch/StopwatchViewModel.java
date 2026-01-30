package com.salman.bitclock.ui.stopwatch;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.salman.bitclock.data.models.Lap;

import java.util.ArrayList;
import java.util.List;

public class StopwatchViewModel extends ViewModel {

    private MutableLiveData<Long> elapsedTime = new MutableLiveData<>(0L);
    private MutableLiveData<List<Lap>> laps = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Boolean> isRunning = new MutableLiveData<>(false);

    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updateTime = 0L;
    private long lastLapTime = 0L;

    private Handler handler = new Handler(Looper.getMainLooper());

    public LiveData<Long> getElapsedTime() {
        return elapsedTime;
    }

    public LiveData<List<Lap>> getLaps() {
        return laps;
    }

    public LiveData<Boolean> getIsRunning() {
        return isRunning;
    }

    public void start() {
        if (!isRunning.getValue()) {
            startTime = SystemClock.uptimeMillis();
            handler.post(updateTimerThread);
            isRunning.setValue(true);
        }
    }

    public void pause() {
        if (isRunning.getValue()) {
            timeSwapBuff += timeInMilliseconds;
            handler.removeCallbacks(updateTimerThread);
            isRunning.setValue(false);
        }
    }

    public void reset() {
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updateTime = 0L;
        lastLapTime = 0L;
        elapsedTime.setValue(0L);
        laps.setValue(new ArrayList<>());
        handler.removeCallbacks(updateTimerThread);
        isRunning.setValue(false);
    }

    public void lap() {
        if (isRunning.getValue()) {
            long currentTime = updateTime;
            long lapTime = currentTime - lastLapTime;
            lastLapTime = currentTime;

            List<Lap> currentLaps = laps.getValue();
            currentLaps.add(0, new Lap(currentLaps.size() + 1, lapTime, currentTime));
            laps.setValue(currentLaps);
        }
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;
            elapsedTime.setValue(updateTime);
            handler.postDelayed(this, 10);
        }
    };
}
