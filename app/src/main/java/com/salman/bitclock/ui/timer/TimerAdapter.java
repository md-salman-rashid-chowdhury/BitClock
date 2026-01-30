package com.salman.bitclock.ui.timer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salman.bitclock.R;
import com.salman.bitclock.data.models.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.TimerViewHolder> {

    private List<Timer> timers = new ArrayList<>();
    private OnTimerInteractionListener listener;

    @NonNull
    @Override
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timer, parent, false);
        return new TimerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerViewHolder holder, int position) {
        Timer currentTimer = timers.get(position);
        holder.timerName.setText(currentTimer.name);
        holder.timerTime.setText(formatTime(currentTimer.remainingMs));

        // Set the play/pause button icon based on the timer status
        if (currentTimer.status == 1) { // Running
            holder.playPauseButton.setImageResource(R.drawable.ic_pause);
        } else { // Paused or stopped
            holder.playPauseButton.setImageResource(R.drawable.ic_play_arrow);
        }

        holder.playPauseButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayPauseClicked(currentTimer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timers.size();
    }

    public void setTimers(List<Timer> timers) {
        this.timers = timers;
        notifyDataSetChanged();
    }

    private String formatTime(long time) {
        long seconds = (time / 1000) % 60;
        long minutes = (time / (1000 * 60)) % 60;
        long hours = (time / (1000 * 60 * 60)) % 24;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public interface OnTimerInteractionListener {
        void onPlayPauseClicked(Timer timer);
    }

    public void setOnTimerInteractionListener(OnTimerInteractionListener listener) {
        this.listener = listener;
    }

    static class TimerViewHolder extends RecyclerView.ViewHolder {
        private TextView timerName;
        private TextView timerTime;
        private ImageButton playPauseButton;

        public TimerViewHolder(@NonNull View itemView) {
            super(itemView);
            timerName = itemView.findViewById(R.id.timer_name);
            timerTime = itemView.findViewById(R.id.timer_time);
            playPauseButton = itemView.findViewById(R.id.play_pause_button);
        }
    }
}
