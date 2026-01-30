package com.salman.bitclock.ui.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.salman.bitclock.R;
import com.salman.bitclock.data.models.Alarm;
import com.salman.bitclock.receivers.AlarmReceiver;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class SetAlarmActivity extends AppCompatActivity {

    public static final String EXTRA_ALARM_ID = "com.salman.bitclock.EXTRA_ALARM_ID";

    private AlarmViewModel alarmViewModel;

    private EditText alarmNameInput;
    private TextView hourPicker, minutePicker, ampmPicker;
    private TextView dayS, dayM, dayT, dayW, dayTh, dayF, daySa;
    private SwitchMaterial alarmSoundSwitch, vibrationSwitch, snoozeSwitch;
    private Button cancelButton, saveButton;

    private Set<Integer> selectedDays = new HashSet<>();
    private int alarmId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        alarmNameInput = findViewById(R.id.alarm_name_input);
        hourPicker = findViewById(R.id.time_picker_hour);
        minutePicker = findViewById(R.id.time_picker_minute);
        ampmPicker = findViewById(R.id.time_picker_ampm);
        dayS = findViewById(R.id.day_s);
        dayM = findViewById(R.id.day_m);
        dayT = findViewById(R.id.day_t);
        dayW = findViewById(R.id.day_w);
        dayTh = findViewById(R.id.day_th);
        dayF = findViewById(R.id.day_f);
        daySa = findViewById(R.id.day_sa);
        alarmSoundSwitch = findViewById(R.id.alarm_sound_switch);
        vibrationSwitch = findViewById(R.id.vibration_switch);
        snoozeSwitch = findViewById(R.id.snooze_switch);
        cancelButton = findViewById(R.id.cancel_button);
        saveButton = findViewById(R.id.save_button);

        setupDayPickers();

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ALARM_ID)) {
            alarmId = intent.getIntExtra(EXTRA_ALARM_ID, -1);
            if (alarmId != -1) {
                alarmViewModel.getAlarmById(alarmId).observe(this, this::populateUi);
            }
        }

        cancelButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> saveAlarm());
    }

    private void setupDayPickers() {
        View.OnClickListener dayClickListener = v -> {
            TextView dayView = (TextView) v;
            int day = (int) v.getTag();
            if (selectedDays.contains(day)) {
                selectedDays.remove(day);
                dayView.setSelected(false);
            } else {
                selectedDays.add(day);
                dayView.setSelected(true);
            }
        };

        dayS.setTag(Calendar.SUNDAY);
        dayM.setTag(Calendar.MONDAY);
        dayT.setTag(Calendar.TUESDAY);
        dayW.setTag(Calendar.WEDNESDAY);
        dayTh.setTag(Calendar.THURSDAY);
        dayF.setTag(Calendar.FRIDAY);
        daySa.setTag(Calendar.SATURDAY);

        dayS.setOnClickListener(dayClickListener);
        dayM.setOnClickListener(dayClickListener);
        dayT.setOnClickListener(dayClickListener);
        dayW.setOnClickListener(dayClickListener);
        dayTh.setOnClickListener(dayClickListener);
        dayF.setOnClickListener(dayClickListener);
        daySa.setOnClickListener(dayClickListener);
    }

    private void populateUi(Alarm alarm) {
        if (alarm == null) return;

        alarmNameInput.setText(alarm.getLabel());

        int hour = alarm.getHour();
        if (hour >= 12) {
            ampmPicker.setText("PM");
            if (hour > 12) {
                hour -= 12;
            }
        } else {
            ampmPicker.setText("AM");
            if (hour == 0) {
                hour = 12;
            }
        }
        hourPicker.setText(String.valueOf(hour));
        minutePicker.setText(String.format("%02d", alarm.getMinute()));

        selectedDays.clear();
        int repeatDays = alarm.getRepeatDays();
        if ((repeatDays & 1) != 0) selectedDays.add(Calendar.SUNDAY);
        if ((repeatDays & 2) != 0) selectedDays.add(Calendar.MONDAY);
        if ((repeatDays & 4) != 0) selectedDays.add(Calendar.TUESDAY);
        if ((repeatDays & 8) != 0) selectedDays.add(Calendar.WEDNESDAY);
        if ((repeatDays & 16) != 0) selectedDays.add(Calendar.THURSDAY);
        if ((repeatDays & 32) != 0) selectedDays.add(Calendar.FRIDAY);
        if ((repeatDays & 64) != 0) selectedDays.add(Calendar.SATURDAY);
        updateDaySelection();

        alarmSoundSwitch.setChecked(alarm.getSoundUri() != null);
        vibrationSwitch.setChecked(alarm.isVibrate());
        // Snooze not implemented in model yet
    }

    private void updateDaySelection() {
        dayS.setSelected(selectedDays.contains(Calendar.SUNDAY));
        dayM.setSelected(selectedDays.contains(Calendar.MONDAY));
        dayT.setSelected(selectedDays.contains(Calendar.TUESDAY));
        dayW.setSelected(selectedDays.contains(Calendar.WEDNESDAY));
        dayTh.setSelected(selectedDays.contains(Calendar.THURSDAY));
        dayF.setSelected(selectedDays.contains(Calendar.FRIDAY));
        daySa.setSelected(selectedDays.contains(Calendar.SATURDAY));
    }

    private void saveAlarm() {
        int hour = Integer.parseInt(hourPicker.getText().toString());
        int minute = Integer.parseInt(minutePicker.getText().toString());

        if (ampmPicker.getText().toString().equals("PM") && hour != 12) {
            hour += 12;
        } else if (ampmPicker.getText().toString().equals("AM") && hour == 12) {
            hour = 0;
        }

        int repeatDays = 0;
        for (int day : selectedDays) {
            switch (day) {
                case Calendar.SUNDAY: repeatDays |= 1; break;
                case Calendar.MONDAY: repeatDays |= 2; break;
                case Calendar.TUESDAY: repeatDays |= 4; break;
                case Calendar.WEDNESDAY: repeatDays |= 8; break;
                case Calendar.THURSDAY: repeatDays |= 16; break;
                case Calendar.FRIDAY: repeatDays |= 32; break;
                case Calendar.SATURDAY: repeatDays |= 64; break;
            }
        }

        Alarm alarm = new Alarm();
        alarm.setHour(hour);
        alarm.setMinute(minute);
        alarm.setLabel(alarmNameInput.getText().toString());
        alarm.setRepeatDays(repeatDays);
        alarm.setEnabled(true);
        alarm.setVibrate(vibrationSwitch.isChecked());
        if (alarmSoundSwitch.isChecked()) {
            alarm.setSoundUri("file:///android_asset/ringtones/default.mp3");
        }

        if (alarmId != -1) {
            alarm.setId(alarmId);
            alarmViewModel.update(alarm);
        } else {
            alarmViewModel.insert(alarm);
        }

        scheduleAlarm(alarm);

        finish();
    }

    private void scheduleAlarm(Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(EXTRA_ALARM_ID, alarm.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);

        // If alarm time has already passed today, schedule it for the next day
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (alarm.getRepeatDays() > 0) {
            // For repeating alarms, find the next occurrence
            while (!selectedDays.contains(calendar.get(Calendar.DAY_OF_WEEK))) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        } else {
            // For non-repeating alarms
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        Toast.makeText(this, "Alarm set for " + alarm.getHour() + ":" + alarm.getMinute(), Toast.LENGTH_SHORT).show();
    }
}
