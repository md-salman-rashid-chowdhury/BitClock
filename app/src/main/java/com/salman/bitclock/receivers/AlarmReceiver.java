package com.salman.bitclock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.salman.bitclock.data.repository.AlarmRepository;
import com.salman.bitclock.data.models.Alarm;
import com.salman.bitclock.di.AppModule;
import com.salman.bitclock.services.AlarmRingingService;
import com.salman.bitclock.utils.AlarmScheduler;

import dagger.hilt.android.EntryPointAccessors;

/**
 * Receiver that handles alarm triggers.
 * Optimized to use the Repository and AlarmScheduler efficiently.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        String action = intent.getAction();
        if ("com.salman.bitclock.ALARM_TRIGGER".equals(action)) {
            int alarmId = intent.getIntExtra("ALARM_ID", -1);
            if (alarmId == -1) return;

            final PendingResult pendingResult = goAsync();
            
            // Access the repository via Hilt EntryPoint
            AppModule.AlarmRepositoryEntryPoint entryPoint = EntryPointAccessors.fromApplication(
                    context, AppModule.AlarmRepositoryEntryPoint.class);
            AlarmRepository alarmRepository = entryPoint.alarmRepository();
            AlarmScheduler scheduler = new AlarmScheduler(context);

            // Use a background thread to handle database/logic to keep CPU usage low on main thread
            new Thread(() -> {
                try {
                    Alarm alarm = alarmRepository.getAlarmByIdSync(alarmId);
                    if (alarm != null && alarm.isEnabled()) {
                        startAlarmService(context, alarm);
                        
                        // Handle rescheduling for repeating alarms
                        if (alarm.isRepeating()) {
                            scheduler.scheduleAlarm(alarm);
                        } else {
                            alarm.setEnabled(false);
                            alarmRepository.update(alarm);
                        }
                    }
                } finally {
                    pendingResult.finish();
                }
            }).start();
        }
    }

    private void startAlarmService(Context context, Alarm alarm) {
        Intent serviceIntent = new Intent(context, AlarmRingingService.class);
        serviceIntent.putExtra("ALARM_ID", alarm.getId());
        serviceIntent.putExtra("ALARM_LABEL", alarm.getLabel());
        serviceIntent.putExtra("SOUND_URI", alarm.getSoundUri());
        serviceIntent.putExtra("VIBRATE", alarm.isVibrate());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
