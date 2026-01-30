package com.salman.bitclock.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.salman.bitclock.MainActivity;
import com.salman.bitclock.data.models.Alarm;
import com.salman.bitclock.receivers.AlarmReceiver;

public class AlarmScheduler {

    private static final String TAG = "AlarmScheduler";
    private final Context context;
    private final AlarmManager alarmManager;

    public AlarmScheduler(Context context) {
        this.context = context.getApplicationContext();
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void scheduleAlarm(Alarm alarm) {
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.w(TAG, "Cannot schedule exact alarms. App needs SCHEDULE_EXACT_ALARM permission.");
            // The UI layer is responsible for handling permission requests and user feedback.
            return;
        }

        long triggerTime = alarm.getNextAlarmTime();
        Log.d(TAG, "Scheduling alarm ID: " + alarm.getId() + " for: " + triggerTime);

        PendingIntent pendingIntent = createPendingIntent(alarm.getId());
        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(triggerTime, getMainActivityPendingIntent());

        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
        // **FIX:** Removed the Toast message that was causing a crash on background threads.
    }

    public void cancelAlarm(int alarmId) {
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null");
            return;
        }
        PendingIntent pendingIntent = createPendingIntent(alarmId);
        alarmManager.cancel(pendingIntent);
        Log.d(TAG, "Canceled alarm ID: " + alarmId);
    }

    private PendingIntent createPendingIntent(int alarmId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("com.salman.bitclock.ALARM_TRIGGER");
        intent.putExtra("ALARM_ID", alarmId);
        return PendingIntent.getBroadcast(context, alarmId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private PendingIntent getMainActivityPendingIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context, -1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
