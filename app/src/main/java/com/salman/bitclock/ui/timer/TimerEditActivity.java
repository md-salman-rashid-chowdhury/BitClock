package com.salman.bitclock.ui.timer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.salman.bitclock.R;
import com.salman.bitclock.data.models.Timer;

public class TimerEditActivity extends AppCompatActivity {

    private EditText timerNameInput;
    private NumberPicker hoursPicker, minutesPicker, secondsPicker;
    private Button saveTimerButton;

    private TimerViewModel timerViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_edit);

        timerNameInput = findViewById(R.id.timer_name_input);
        hoursPicker = findViewById(R.id.hours_picker);
        minutesPicker = findViewById(R.id.minutes_picker);
        secondsPicker = findViewById(R.id.seconds_picker);
        saveTimerButton = findViewById(R.id.save_timer_button);

        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(23);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);

        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        saveTimerButton.setOnClickListener(v -> {
            String name = timerNameInput.getText().toString();
            long duration = (hoursPicker.getValue() * 3600 + minutesPicker.getValue() * 60 + secondsPicker.getValue()) * 1000;

            Timer timer = new Timer();
            timer.name = name;
            timer.initialDurationMs = duration;
            timer.remainingMs = duration;
            timer.status = 0; // Stopped

            timerViewModel.insert(timer);
            finish();
        });
    }
}
