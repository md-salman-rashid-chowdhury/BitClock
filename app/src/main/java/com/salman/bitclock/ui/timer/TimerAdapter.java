package com.salman.bitclock.ui.timer;

import android.os.CountDownTimer;
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
        return new TimerViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerViewHolder holder, int position) {
        holder.bind(timers.get(position));
    }

    @Override
    public void onViewRecycled(@NonNull TimerViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cancelTimer();
    }

    @Override
    public int getItemCount() {
        return timers.size();
    }

    public void setTimers(List<Timer> timers) {
        this.timers = timers;
        notifyDataSetChanged();
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
        private Timer currentTimer;

        public TimerViewHolder(@NonNull View itemView, OnTimerInteractionListener listener) {
            super(itemView);
            this.listener = listener;
            timerName = itemView.findViewById(R.id.timer_name);
            timerTime = itemView.findViewById(R.id.timer_time);
            playPauseButton = itemView.findViewById(R.id.play_pause_button);

            playPauseButton.setOnClickListener(v -> {
                if (listener != null && currentTimer != null) {
                    listener.onPlayPauseClicked(currentTimer);
                }
            });
        }

        public void bind(Timer timer) {
            this.currentTimer = timer;
            timerName.setText(timer.name);
            cancelTimer();

            if (timer.status == 1) { // Running
                playPauseButton.setImageResource(R.drawable.ic_pause);
                countDownTimer = new CountDownTimer(timer.remainingMs, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        currentTimer.remainingMs = millisUntilFinished;
                        timerTime.setText(formatTime(millisUntilFinished));
                    }

                    @Override
                    public void onFinish() {
                        currentTimer.remainingMs = 0;
                        currentTimer.status = 0; // Stopped
                        timerTime.setText(formatTime(0));
                        playPauseButton.setImageResource(R.drawable.ic_play_arrow);
                        if (listener != null) {
                            listener.onTimerFinished(currentTimer);
                        }
                    }
                }.start();
            } else { // Paused or stopped
                playPauseButton.setImageResource(R.drawable.ic_play_arrow);
                timerTime.setText(formatTime(timer.remainingMs));
            }
        }

        public void cancelTimer() {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
        }

        private String formatTime(long time) {
            long seconds = (time / 1000) % 60;
            long minutes = (time / (1000 * 60)) % 60;
            long hours = (time / (1000 * 60 * 60)) % 24;
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        }
    }
}
