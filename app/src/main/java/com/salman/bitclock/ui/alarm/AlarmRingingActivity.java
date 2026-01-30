package com.salman.bitclock.ui.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.salman.bitclock.R;
import com.salman.bitclock.receivers.AlarmReceiver;
import com.salman.bitclock.services.AlarmRingingService;

import java.util.Calendar;

public class AlarmRingingActivity extends AppCompatActivity {

    private static final String TAG = "AlarmRingingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        Log.d(TAG, "AlarmRingingActivity has started.");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        TextView alarmLabelTextView = findViewById(R.id.alarm_label_textview);
        Button dismissButton = findViewById(R.id.dismiss_button);
        Button snoozeButton = findViewById(R.id.snooze_button);

        String label = getIntent().getStringExtra("ALARM_LABEL");
        alarmLabelTextView.setText((label != null && !label.isEmpty()) ? label : "Alarm");

        dismissButton.setOnClickListener(v -> dismissAlarm());
        snoozeButton.setOnClickListener(v -> snoozeAlarm());
    }

    private void dismissAlarm() {
        Log.d(TAG, "Dismissing alarm.");
        stopAlarmService();
        finishAndRemoveTask();
    }

    private void snoozeAlarm() {
        Log.d(TAG, "Snoozing alarm.");
        stopAlarmService();

        int snoozeMinutes = 10; 

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, snoozeMinutes);
        
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("com.salman.bitclock.ALARM_TRIGGER");
        intent.putExtras(getIntent());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, 
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);

        Toast.makeText(this, "Snoozed for " + snoozeMinutes + " minutes", Toast.LENGTH_SHORT).show();
        finishAndRemoveTask();
    }

    private void stopAlarmService() {
        Intent intent = new Intent(this, AlarmRingingService.class);
        stopService(intent);
    }
}
