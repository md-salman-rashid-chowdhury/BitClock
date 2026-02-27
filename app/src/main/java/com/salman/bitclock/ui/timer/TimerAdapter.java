package com.salman.bitclock.ui.timer;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.salman.bitclock.R;
import com.salman.bitclock.data.models.Timer;

import java.util.Locale;

public class TimerAdapter extends ListAdapter<Timer, TimerAdapter.TimerViewHolder> {

    private OnTimerInteractionListener listener;

    public TimerAdapter() {
        super(new TimerDiffCallback());
    }

    @NonNull
    @Override
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timer, parent, false);
        return new TimerViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public void onViewRecycled(@NonNull TimerViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cancelTimer();
    }

    public interface OnTimerInteractionListener {
        void onPlayPauseClicked(Timer timer);
        void onTimerFinished(Timer timer);
    }

    public void setOnTimerInteractionListener(OnTimerInteractionListener listener) {
        this.listener = listener;
    }

    static class TimerViewHolder extends RecyclerView.ViewHolder {
        private final TextView timerName;
        private final TextView timerTime;
        private final ImageButton playPauseButton;
        private final OnTimerInteractionListener listener;
        private CountDownTimer countDownTimer;
        private int timerId = -1;

        public TimerViewHolder(@NonNull View itemView, OnTimerInteractionListener listener) {
            super(itemView);
            this.listener = listener;
            timerName = itemView.findViewById(R.id.timer_name);
            timerTime = itemView.findViewById(R.id.timer_time);
            playPauseButton = itemView.findViewById(R.id.play_pause_button);
        }

        public void bind(Timer timer) {
            this.timerId = timer.id;
            timerName.setText(timer.name);
            cancelTimer();

            playPauseButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlayPauseClicked(timer);
                }
            });

            if (timer.status == 1 && timer.remainingMs > 0) { // Running
                playPauseButton.setImageResource(R.drawable.ic_pause);
                startCountdown(timer);
            } else { // Paused or stopped
                playPauseButton.setImageResource(R.drawable.ic_play_arrow);
                timerTime.setText(formatTime(timer.remainingMs));
            }
        }

        private void startCountdown(Timer timer) {
            countDownTimer = new CountDownTimer(timer.remainingMs, 100) { // Higher precision for UI
                @Override
                public void onTick(long millisUntilFinished) {
                    // Update the model reference so if it's paused, we have the latest time
                    timer.remainingMs = millisUntilFinished;
                    timerTime.setText(formatTime(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    timer.remainingMs = 0;
                    timer.status = 0;
                    timerTime.setText(formatTime(0));
                    playPauseButton.setImageResource(R.drawable.ic_play_arrow);
                    if (listener != null) {
                        listener.onTimerFinished(timer);
                    }
                }
            }.start();
        }

        public void cancelTimer() {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
        }

        private String formatTime(long time) {
            if (time < 0) time = 0;
            long seconds = (time / 1000) % 60;
            long minutes = (time / (1000 * 60)) % 60;
            long hours = (time / (1000 * 60 * 60));
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        }
    }

    private static class TimerDiffCallback extends DiffUtil.ItemCallback<Timer> {
        @Override
        public boolean areItemsTheSame(@NonNull Timer oldItem, @NonNull Timer newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Timer oldItem, @NonNull Timer newItem) {
            // Optimization: If the timer is running, we don't want to re-bind every tick 
            // from the DB unless the status or name actually changed.
            // This prevents UI flickering.
            return oldItem.status == newItem.status && 
                   oldItem.name.equals(newItem.name) &&
                   (oldItem.status == 1 || oldItem.remainingMs == newItem.remainingMs);
        }
    }
}
