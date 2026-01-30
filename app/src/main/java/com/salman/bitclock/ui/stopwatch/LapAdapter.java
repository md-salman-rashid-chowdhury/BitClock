package com.salman.bitclock.ui.stopwatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salman.bitclock.R;
import com.salman.bitclock.data.models.Lap;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LapAdapter extends RecyclerView.Adapter<LapAdapter.LapViewHolder> {

    private List<Lap> laps = new ArrayList<>();

    @NonNull
    @Override
    public LapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lap, parent, false);
        return new LapViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LapViewHolder holder, int position) {
        Lap currentLap = laps.get(position);

        holder.lapNumber.setText("Lap " + currentLap.lapNumber);
        holder.lapTime.setText(formatTime(currentLap.lapTime));
        holder.overallTime.setText(formatTime(currentLap.overallTime));
    }

    @Override
    public int getItemCount() {
        return laps.size();
    }

    public void setLaps(List<Lap> laps) {
        this.laps = laps;
        notifyDataSetChanged();
    }

    private String formatTime(long time) {
        long millis = time % 1000 / 10;
        long seconds = (time / 1000) % 60;
        long minutes = (time / (1000 * 60)) % 60;

        return String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, millis);
    }

    static class LapViewHolder extends RecyclerView.ViewHolder {
        private TextView lapNumber;
        private TextView lapTime;
        private TextView overallTime;

        public LapViewHolder(@NonNull View itemView) {
            super(itemView);
            lapNumber = itemView.findViewById(R.id.lap_number);
            lapTime = itemView.findViewById(R.id.lap_time);
            overallTime = itemView.findViewById(R.id.overall_time);
        }
    }
}
