package com.salman.bitclock.ui.alarm;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.salman.bitclock.R;
import com.salman.bitclock.data.models.Alarm;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private final List<Alarm> alarms = new ArrayList<>();
    private final OnAlarmListener onAlarmListener;

    public AlarmAdapter(OnAlarmListener onAlarmListener) {
        this.onAlarmListener = onAlarmListener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view, onAlarmListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        holder.bind(alarms.get(position));
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public Alarm getAlarmAt(int position) {
        return alarms.get(position);
    }

    public void setAlarms(List<Alarm> newAlarms) {
        final AlarmDiffCallback diffCallback = new AlarmDiffCallback(new ArrayList<>(this.alarms), newAlarms);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.alarms.clear();
        this.alarms.addAll(newAlarms);
        diffResult.dispatchUpdatesTo(this);
    }

    public interface OnAlarmListener {
        void onAlarmClick(Alarm alarm);
        void onAlarmToggle(Alarm alarm, boolean isEnabled);
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        private final TextView alarmTime;
        private final TextView alarmLabel;
        private final MaterialSwitch alarmSwitch;
        private final TextView[] dayTextViews;
        private final OnAlarmListener onAlarmListener;

        public AlarmViewHolder(@NonNull View itemView, OnAlarmListener onAlarmListener) {
            super(itemView);
            this.onAlarmListener = onAlarmListener;

            alarmTime = itemView.findViewById(R.id.item_alarm_time);
            alarmLabel = itemView.findViewById(R.id.item_alarm_label);
            alarmSwitch = itemView.findViewById(R.id.item_alarm_switch);
            
            dayTextViews = new TextView[] {
                itemView.findViewById(R.id.item_day_sun),
                itemView.findViewById(R.id.item_day_mon),
                itemView.findViewById(R.id.item_day_tue),
                itemView.findViewById(R.id.item_day_wed),
                itemView.findViewById(R.id.item_day_thu),
                itemView.findViewById(R.id.item_day_fri),
                itemView.findViewById(R.id.item_day_sat)
            };
        }

        public void bind(Alarm alarm) {
            int hour = alarm.getHour();
            String amPm = "AM";
            if (hour >= 12) {
                amPm = "PM";
                if (hour > 12) hour -= 12;
            }
            if (hour == 0) hour = 12;
            alarmTime.setText(String.format(Locale.getDefault(), "%d:%02d %s", hour, alarm.getMinute(), amPm));

            boolean hasLabel = alarm.getLabel() != null && !alarm.getLabel().isEmpty();
            boolean isRepeating = alarm.isRepeating();

            if (hasLabel && !isRepeating) {
                alarmLabel.setText(alarm.getLabel());
                alarmLabel.setVisibility(View.VISIBLE);
            } else if (isRepeating) {
                alarmLabel.setText(hasLabel ? alarm.getLabel() : "Repeating");
                alarmLabel.setVisibility(View.VISIBLE);
            } else {
                alarmLabel.setVisibility(View.GONE);
            }
            
            itemView.findViewById(R.id.item_day_container).setVisibility(isRepeating ? View.VISIBLE : View.GONE);

            alarmSwitch.setOnCheckedChangeListener(null);
            alarmSwitch.setChecked(alarm.isEnabled());
            alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> onAlarmListener.onAlarmToggle(alarm, isChecked));

            int repeatDays = alarm.getRepeatDays();
            int activeColor = ContextCompat.getColor(itemView.getContext(), R.color.primary);
            int inactiveColor = ContextCompat.getColor(itemView.getContext(), R.color.outline);
            
            for (int i = 0; i < dayTextViews.length; i++) {
                TextView dayView = dayTextViews[i];
                if ((repeatDays & (1 << i)) != 0) {
                    dayView.setTextColor(activeColor);
                } else {
                    dayView.setTextColor(inactiveColor);
                }
            }
            
            itemView.setOnClickListener(v -> onAlarmListener.onAlarmClick(alarm));
        }
    }

    private static class AlarmDiffCallback extends DiffUtil.Callback {
        private final List<Alarm> oldList;
        private final List<Alarm> newList;

        public AlarmDiffCallback(List<Alarm> oldList, List<Alarm> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Alarm oldAlarm = oldList.get(oldItemPosition);
            Alarm newAlarm = newList.get(newItemPosition);
            return Objects.equals(oldAlarm, newAlarm);
        }
    }
}
