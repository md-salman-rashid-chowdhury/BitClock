package com.salman.bitclock.ui.alarm;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.salman.bitclock.R;
import com.salman.bitclock.data.models.Alarm;
import com.salman.bitclock.utils.AlarmScheduler;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AlarmDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ALARM_ID = "com.salman.bitclock.ALARM_ID";

    private AlarmViewModel alarmViewModel;
    
    @Inject
    AlarmScheduler alarmScheduler;

    private NumberPicker pickerHour, pickerMinute, pickerAmPm;
    private ChipGroup chipGroupRepeatDays;
    private TextInputEditText labelEditText;
    private RelativeLayout soundSelector, snoozeSelector;
    private TextView soundSelectionTextView, snoozeSelectionTextView;
    private MaterialSwitch vibrationSwitch;
    private MaterialButton saveButton, buttonOnetime, buttonEveryday;
    private MaterialToolbar toolbar;

    private int currentAlarmId = -1;
    private Set<Integer> selectedDays = new HashSet<>();
    private int selectedSnoozeMinutes = 10;
    private Uri selectedSoundUri;

    private final ActivityResultLauncher<Intent> ringtonePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    if (uri != null) {
                        selectedSoundUri = uri;
                        updateSoundSelectionText(uri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        initializeViews();
        setupToolbar();
        setupTimePickers();
        setupDayChips();
        setupClickListeners();

        currentAlarmId = getIntent().getIntExtra(EXTRA_ALARM_ID, -1);
        if (currentAlarmId != -1) {
            toolbar.setTitle("Edit Alarm");
            loadAlarmData(currentAlarmId);
        } else {
            selectedSoundUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
            updateSoundSelectionText(selectedSoundUri);
        }
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        pickerHour = findViewById(R.id.picker_hour);
        pickerMinute = findViewById(R.id.picker_minute);
        pickerAmPm = findViewById(R.id.picker_am_pm);
        chipGroupRepeatDays = findViewById(R.id.chip_group_repeat_days);
        labelEditText = findViewById(R.id.label_edit_text);
        soundSelector = findViewById(R.id.sound_selector);
        snoozeSelector = findViewById(R.id.snooze_selector);
        soundSelectionTextView = findViewById(R.id.sound_selection_textview);
        snoozeSelectionTextView = findViewById(R.id.snooze_selection_textview);
        vibrationSwitch = findViewById(R.id.vibration_switch);
        saveButton = findViewById(R.id.save_button);
        buttonOnetime = findViewById(R.id.button_onetime);
        buttonEveryday = findViewById(R.id.button_everyday);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTimePickers() {
        pickerHour.setMinValue(1);
        pickerHour.setMaxValue(12);
        pickerHour.setWrapSelectorWheel(true);

        pickerMinute.setMinValue(0);
        pickerMinute.setMaxValue(59);
        pickerMinute.setFormatter(value -> String.format("%02d", value));
        pickerMinute.setWrapSelectorWheel(true);

        pickerAmPm.setMinValue(0);
        pickerAmPm.setMaxValue(1);
        pickerAmPm.setDisplayedValues(new String[]{"AM", "PM"});
    }

    private void setupDayChips() {
        for (int i = 0; i < chipGroupRepeatDays.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupRepeatDays.getChildAt(i);
            int day = i + 1; // 1=Sun, 2=Mon, ...
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedDays.add(day);
                } else {
                    selectedDays.remove(day);
                }
            });
        }
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> saveAlarm());
        snoozeSelector.setOnClickListener(v -> showSnoozeDurationDialog());
        soundSelector.setOnClickListener(v -> openRingtonePicker());
        buttonOnetime.setOnClickListener(v -> selectRepeatDays(false));
        buttonEveryday.setOnClickListener(v -> selectRepeatDays(true));
    }

    private void selectRepeatDays(boolean selectAll) {
        for (int i = 0; i < chipGroupRepeatDays.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupRepeatDays.getChildAt(i);
            chip.setChecked(selectAll);
        }
    }
    
    private void showSnoozeDurationDialog() {
        final String[] snoozeOptions = {"5 minutes", "10 minutes", "15 minutes", "20 minutes", "30 minutes"};
        final int[] snoozeValues = {5, 10, 15, 20, 30};

        new AlertDialog.Builder(this)
                .setTitle("Snooze Duration")
                .setItems(snoozeOptions, (dialog, which) -> {
                    selectedSnoozeMinutes = snoozeValues[which];
                    snoozeSelectionTextView.setText(snoozeOptions[which]);
                })
                .show();
    }

    private void openRingtonePicker() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedSoundUri);
        ringtonePickerLauncher.launch(intent);
    }

    private void updateSoundSelectionText(Uri uri) {
        if (uri == null) {
            soundSelectionTextView.setText("Silent");
            return;
        }
        Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
        if (ringtone != null) {
            String name = ringtone.getTitle(this);
            soundSelectionTextView.setText(name);
        } else {
            soundSelectionTextView.setText("Unknown");
        }
    }

    private void loadAlarmData(int alarmId) {
        alarmViewModel.getAlarmById(alarmId).observe(this, alarm -> {
            if (alarm != null) {
                int hour = alarm.getHour();
                if (hour >= 12) {
                    pickerAmPm.setValue(1); // PM
                    if (hour > 12) hour -= 12;
                } else {
                    pickerAmPm.setValue(0); // AM
                    if (hour == 0) hour = 12;
                }
                pickerHour.setValue(hour);
                pickerMinute.setValue(alarm.getMinute());

                labelEditText.setText(alarm.getLabel());
                vibrationSwitch.setChecked(alarm.isVibrate());

                selectedSnoozeMinutes = alarm.getSnoozeMinutes();
                snoozeSelectionTextView.setText(String.format(Locale.getDefault(), "%d minutes", selectedSnoozeMinutes));

                if (alarm.getSoundUri() != null && !alarm.getSoundUri().isEmpty()) {
                    selectedSoundUri = Uri.parse(alarm.getSoundUri());
                }
                updateSoundSelectionText(selectedSoundUri);

                int repeatDaysBitmask = alarm.getRepeatDays();
                for (int i = 0; i < 7; i++) {
                    Chip chip = (Chip) chipGroupRepeatDays.getChildAt(i);
                    if ((repeatDaysBitmask & (1 << i)) != 0) {
                        chip.setChecked(true);
                    }
                }
            }
        });
    }

    private void saveAlarm() {
        int hour = pickerHour.getValue();
        if (pickerAmPm.getValue() == 1) { // PM
            if (hour != 12) hour += 12;
        } else { // AM
            if (hour == 12) hour = 0;
        }
        int minute = pickerMinute.getValue();

        Alarm alarm = new Alarm();
        if (currentAlarmId != -1) {
            alarm.setId(currentAlarmId);
        }

        alarm.setHour(hour);
        alarm.setMinute(minute);
        alarm.setEnabled(true);
        alarm.setLabel(labelEditText.getText().toString());
        alarm.setVibrate(vibrationSwitch.isChecked());
        alarm.setSnoozeMinutes(selectedSnoozeMinutes);
        alarm.setSoundUri(selectedSoundUri != null ? selectedSoundUri.toString() : "");
        
        int repeatDaysBitmask = 0;
        for (Integer day : selectedDays) {
            repeatDaysBitmask |= (1 << (day - 1));
        }
        alarm.setRepeatDays(repeatDaysBitmask);

        if (currentAlarmId != -1) {
            alarmViewModel.update(alarm);
            alarmScheduler.scheduleAlarm(alarm);
            finish();
        } else {
            try {
                long newId = alarmViewModel.insert(alarm).get();
                alarm.setId((int) newId);
                alarmScheduler.scheduleAlarm(alarm);
                finish();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(this, PresentationActivity.class);
        intent.putExtra(EXTRA_ALARM_ID, alarm.getId());
        startActivity(intent);

        Toast.makeText(this, "Alarm saved!", Toast.LENGTH_SHORT).show();
    }
}
