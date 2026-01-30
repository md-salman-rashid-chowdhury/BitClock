package com.salman.bitclock.ui.clock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.salman.bitclock.R;

public class ClockFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_clock, container, false);

        FloatingActionButton fabSettings = root.findViewById(R.id.fab_settings);
        fabSettings.setOnClickListener(v -> {
            // Placeholder for settings screen
            Toast.makeText(getContext(), "Clock settings clicked!", Toast.LENGTH_SHORT).show();
        });

        return root;
    }
}
