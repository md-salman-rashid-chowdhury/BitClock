package com.salman.bitclock.ui.timer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.salman.bitclock.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TimerFragment extends Fragment {

    private TimerViewModel timerViewModel;
    private RecyclerView recyclerView;
    private TimerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_timer, container, false);

        // **FIX:** Initialize ViewModel in onViewCreated, not here.
        // recyclerView = root.findViewById(R.id.timer_recyclerview);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Correctly initialize ViewModel
        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        // Observe the LiveData from the ViewModel
        timerViewModel.getAllTimers().observe(getViewLifecycleOwner(), timers -> {
            // Update the UI
            if(adapter != null) {
                // adapter.setTimers(timers);
            }
        });
    }
}
