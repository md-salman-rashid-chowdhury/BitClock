package com.salman.bitclock.ui.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.salman.bitclock.R;

public class SleepFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep, container, false);

        Button getStartedButton = view.findViewById(R.id.get_started_button);
        getStartedButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SetSleepScheduleActivity.class);
            startActivity(intent);
        });

        return view;
    }
}