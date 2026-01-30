package com.salman.bitclock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.salman.bitclock.data.AlarmRepository;
import com.salman.bitclock.data.models.Alarm;
import com.salman.bitclock.di.AppModule;
import com.salman.bitclock.services.AlarmRingingService;
import com.salman.bitclock.utils.AlarmScheduler;

import dagger.hilt.android.EntryPointAccessors;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.e(TAG, "Received null intent");
            return;
        }

        final String action = intent.getAction();
        Log.d(TAG, "Received intent with action: " + action);

        if ("com.salman.bitclock.ALARM_TRIGGER".equals(action)) {
            final int alarmId = intent.getIntExtra("ALARM_ID", -1);
            if (alarmId == -1) {
                Log.e(TAG, "Invalid alarm ID");
                return;
            }

            final PendingResult pendingResult = goAsync();
            AppModule.AlarmRepositoryEntryPoint entryPoint = EntryPointAccessors.fromApplication(context, AppModule.AlarmRepositoryEntryPoint.class);
            final AlarmRepository alarmRepository = entryPoint.alarmRepository();
            final AlarmScheduler scheduler = new AlarmScheduler(context);

            new Thread(() -> {
                try {
                    // **FIX**: Use the synchronous method to get the alarm
                    Alarm alarm = alarmRepository.getAlarmByIdSync(alarmId);
                    if (alarm != null && alarm.isEnabled()) {
                        startAlarmService(context, alarm);
                        if (alarm.isRepeating()) {
                            scheduler.scheduleAlarm(alarm);
                            Log.d(TAG, "Rescheduled repeating alarm ID: " + alarm.getId());
                        } else {
                            alarm.setEnabled(false);
                            alarmRepository.update(alarm);
                            Log.d(TAG, "Disabled one-time alarm ID: " + alarm.getId());
                        }
                    } else {
                        Log.w(TAG, "Alarm is null or disabled, not starting service.");
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
        Log.d(TAG, "Started AlarmRingingService for alarm ID: " + alarm.getId());
    }
}
