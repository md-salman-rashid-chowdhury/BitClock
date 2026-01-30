package com.salman.bitclock.ui.stopwatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salman.bitclock.R;

import java.util.Locale;

public class StopwatchFragment extends Fragment {

    private StopwatchViewModel stopwatchViewModel;
    private TextView stopwatchDisplay;
    private Button startButton, pauseButton, resetButton, lapButton;
    private RecyclerView lapsRecyclerView;
    private LapAdapter lapAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        stopwatchDisplay = root.findViewById(R.id.stopwatch_display);
        startButton = root.findViewById(R.id.start_button);
        pauseButton = root.findViewById(R.id.pause_button);
        resetButton = root.findViewById(R.id.reset_button);
        lapButton = root.findViewById(R.id.lap_button);
        lapsRecyclerView = root.findViewById(R.id.laps_recyclerview);

        lapsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        lapAdapter = new LapAdapter();
        lapsRecyclerView.setAdapter(lapAdapter);

        stopwatchViewModel = new ViewModelProvider(this).get(StopwatchViewModel.class);

        stopwatchViewModel.getElapsedTime().observe(getViewLifecycleOwner(), time -> {
            long millis = time % 1000 / 10;
            long seconds = (time / 1000) % 60;
            long minutes = (time / (1000 * 60)) % 60;
            long hours = (time / (1000 * 60 * 60)) % 24;

            stopwatchDisplay.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d.%02d", hours, minutes, seconds, millis));
        });

        stopwatchViewModel.getLaps().observe(getViewLifecycleOwner(), laps -> {
            lapAdapter.setLaps(laps);
        });

        stopwatchViewModel.getIsRunning().observe(getViewLifecycleOwner(), isRunning -> {
            if (isRunning) {
                startButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
            } else {
                startButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
            }
        });

        startButton.setOnClickListener(v -> stopwatchViewModel.start());
        pauseButton.setOnClickListener(v -> stopwatchViewModel.pause());
        resetButton.setOnClickListener(v -> stopwatchViewModel.reset());
        lapButton.setOnClickListener(v -> stopwatchViewModel.lap());

        return root;
    }
}
