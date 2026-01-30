package com.salman.bitclock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.salman.bitclock.data.AlarmRepository;
import com.salman.bitclock.data.models.Alarm;
import com.salman.bitclock.di.AppModule;
import com.salman.bitclock.utils.AlarmScheduler;

import java.util.List;

import dagger.hilt.android.EntryPointAccessors;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Boot completed, re-scheduling alarms.");
            
            final PendingResult pendingResult = goAsync();
            AppModule.AlarmRepositoryEntryPoint entryPoint = EntryPointAccessors.fromApplication(context, AppModule.AlarmRepositoryEntryPoint.class);
            final AlarmRepository alarmRepository = entryPoint.alarmRepository();
            final AlarmScheduler scheduler = new AlarmScheduler(context);

            new Thread(() -> {
                try {
                    // Room LiveData needs to be observed, so we can't use it here.
                    // We need a way to get all alarms synchronously for the boot receiver.
                    // This will require a small modification to the DAO.
                    List<Alarm> alarms = alarmRepository.getAllAlarmsNonLiveData();
                    for (Alarm alarm : alarms) {
                        if (alarm.isEnabled()) {
                            scheduler.scheduleAlarm(alarm);
                            Log.d(TAG, "Re-scheduled alarm ID: " + alarm.getId());
                        }
                    }
                } finally {
                    pendingResult.finish();
                }
            }).start();
        }
    }
}
