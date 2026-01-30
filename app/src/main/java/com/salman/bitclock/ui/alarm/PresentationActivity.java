package com.salman.bitclock.ui.alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.TextView;

import com.salman.bitclock.R;
import com.salman.bitclock.data.models.Alarm;

public class PresentationActivity extends AppCompatActivity {

    private TextView alarmNameTextView;
    private TextView alarmTimeTextView;
    private AlarmViewModel alarmViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);

        alarmNameTextView = findViewById(R.id.alarmName);
        alarmTimeTextView = findViewById(R.id.alarmTime);

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        // Get alarm ID from intent
        int alarmId = getIntent().getIntExtra(AlarmDetailActivity.EXTRA_ALARM_ID, -1);

        alarmViewModel.getAlarmById(alarmId).observe(this, new Observer<Alarm>() {
            @Override
            public void onChanged(Alarm alarm) {
                if (alarm != null) {
                    alarmNameTextView.setText(alarm.getLabel());
                    alarmTimeTextView.setText(alarm.getHour() + ":" + alarm.getMinute());
                }
            }
        });
    }
}
