package com.salman.bitclock.ui.timer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.salman.bitclock.R;
import com.salman.bitclock.data.models.Timer;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TimerFragment extends Fragment {

    private TimerViewModel timerViewModel;
    private RecyclerView recyclerView;
    private TimerAdapter adapter;
    private FloatingActionButton fabAddTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.timer_recyclerview);
        fabAddTimer = view.findViewById(R.id.fab_add_timer);

        adapter = new TimerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        adapter.setOnTimerInteractionListener(new TimerAdapter.OnTimerInteractionListener() {
            @Override
            public void onPlayPauseClicked(Timer timer) {
                // Toggle timer status (Running/Paused)
                timer.status = (timer.status == 1) ? 0 : 1;
                timerViewModel.update(timer);
            }

            @Override
            public void onTimerFinished(Timer timer) {
                timer.status = 0;
                timer.remainingMs = 0;
                timerViewModel.update(timer);
                Toast.makeText(requireContext(), "Timer \"" + timer.name + "\" finished!", Toast.LENGTH_LONG).show();
            }
        });

        fabAddTimer.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), TimerEditActivity.class);
            startActivity(intent);
        });

        timerViewModel.getAllTimers().observe(getViewLifecycleOwner(), timers -> {
            if (timers != null) {
                adapter.setTimers(timers);
            }
        });
    }
}
