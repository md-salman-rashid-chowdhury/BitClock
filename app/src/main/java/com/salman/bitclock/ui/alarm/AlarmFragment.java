package com.salman.bitclock.ui.alarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.salman.bitclock.R;
import com.salman.bitclock.data.models.Alarm;
import com.salman.bitclock.utils.AlarmScheduler;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AlarmFragment extends Fragment implements AlarmAdapter.OnAlarmListener {

    private AlarmViewModel alarmViewModel;
    private RecyclerView recyclerView;
    private AlarmAdapter alarmAdapter;
    private FloatingActionButton fab;

    @Inject
    AlarmScheduler alarmScheduler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alarm, container, false);

        recyclerView = root.findViewById(R.id.alarms_recyclerview);
        fab = root.findViewById(R.id.fab_add_alarm);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        setupRecyclerView();
        setupFab();

        alarmViewModel.getAllAlarms().observe(getViewLifecycleOwner(), alarms -> {
            alarmAdapter.setAlarms(alarms);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alarmAdapter = new AlarmAdapter(this);
        recyclerView.setAdapter(alarmAdapter);

        // **NEW**: Attach ItemTouchHelper for swipe-to-delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // We don't need move functionality
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Alarm alarmToDelete = alarmAdapter.getAlarmAt(position);

                // Delete the alarm from the ViewModel
                alarmViewModel.delete(alarmToDelete);
                alarmScheduler.cancelAlarm(alarmToDelete.getId());

                // Show Snackbar with Undo option
                Snackbar.make(recyclerView, "Alarm deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            // Re-insert the alarm if Undo is clicked
                            alarmViewModel.insert(alarmToDelete);
                            alarmScheduler.scheduleAlarm(alarmToDelete);
                        }).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void setupFab() {
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AlarmDetailActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onAlarmClick(Alarm alarm) {
        Intent intent = new Intent(getActivity(), AlarmDetailActivity.class);
        intent.putExtra(AlarmDetailActivity.EXTRA_ALARM_ID, alarm.getId());
        startActivity(intent);
    }

    @Override
    public void onAlarmToggle(Alarm alarm, boolean isEnabled) {
        alarm.setEnabled(isEnabled);
        alarmViewModel.update(alarm);
        if (isEnabled) {
            alarmScheduler.scheduleAlarm(alarm);
        } else {
            alarmScheduler.cancelAlarm(alarm.getId());
        }
    }
}
