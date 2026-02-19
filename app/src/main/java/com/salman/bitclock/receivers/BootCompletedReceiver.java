package com.salman.bitclock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.salman.bitclock.data.repository.AlarmRepository;
import com.salman.bitclock.data.models.Alarm;
import com.salman.bitclock.di.AppModule;
import com.salman.bitclock.utils.AlarmScheduler;

import java.util.List;

import dagger.hilt.android.EntryPointAccessors;

/**
 * Re-schedules alarms when the device boots up.
 * Optimized to use synchronous database calls in a background thread.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            
            final PendingResult pendingResult = goAsync();
            
            AppModule.AlarmRepositoryEntryPoint entryPoint = EntryPointAccessors.fromApplication(
                    context, AppModule.AlarmRepositoryEntryPoint.class);
            AlarmRepository alarmRepository = entryPoint.alarmRepository();
            AlarmScheduler scheduler = new AlarmScheduler(context);

            new Thread(() -> {
                try {
                    // Fetch alarms synchronously from the database
                    List<Alarm> alarms = alarmRepository.getAllAlarmsSync();
                    for (Alarm alarm : alarms) {
                        if (alarm.isEnabled()) {
                            scheduler.scheduleAlarm(alarm);
                        }
                    }
                } finally {
                    pendingResult.finish();
                }
            }).start();
        }
    }
}
