package com.example.uhabit;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * HabitAdapter is responsible for displaying each habit in the RecyclerView.
 */
public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private List<Habit> habitList;
    private OnHabitClickListener listener;
    private OnHabitInteractionListener interactionListener;

    public interface OnHabitClickListener {
        void onHabitClick(Habit habit);
    }

    public interface OnHabitInteractionListener {
        void onDailyCheckChanged(Habit habit, boolean isChecked);
        void onHabitCompleted(Habit habit);
    }

    public void setHabits(List<Habit> habits) {
        this.habitList = (habits != null) ? habits : new ArrayList<>();
        notifyDataSetChanged();
    }

    public HabitAdapter(List<Habit> habitList, OnHabitClickListener listener, OnHabitInteractionListener interactionListener) {
        this.habitList = (habitList != null) ? habitList : new ArrayList<>();
        this.listener = listener;
        this.interactionListener = interactionListener;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);

        holder.habitNameTextView.setText(habit.getName());
        holder.habitCategoryTextView.setText(habit.getCategory());
        holder.completionCountTextView.setText("Completed " + habit.getCompletionCount() + " days");
        holder.habitProgressBar.setProgress(habit.getCompletionPercentage());

        // Set the daily completion checkbox state without triggering the listener
        holder.dailyCompleteCheckBox.setOnCheckedChangeListener(null);
        holder.dailyCompleteCheckBox.setChecked(habit.isDailyCompleted());
        holder.dailyCompleteCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (interactionListener != null) {
                interactionListener.onDailyCheckChanged(habit, isChecked);
            }
        });

        holder.completeHabitButton.setOnClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onHabitCompleted(habit);
            }
        });

        holder.habitProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), HabitProfileActivity.class);
            intent.putExtra("habitId", habit.getId());
            v.getContext().startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHabitClick(habit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return habitList != null ? habitList.size() : 0;
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {

        public TextView habitNameTextView;
        public TextView habitCategoryTextView;
        public TextView completionCountTextView;
        public ProgressBar habitProgressBar;
        public CheckBox dailyCompleteCheckBox;
        public Button completeHabitButton;
        public Button habitProfileButton;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitNameTextView = itemView.findViewById(R.id.habitNameTextView);
            habitCategoryTextView = itemView.findViewById(R.id.habitCategoryTextView);
            completionCountTextView = itemView.findViewById(R.id.completionCountTextView);
            habitProgressBar = itemView.findViewById(R.id.habitProgressBar);
            dailyCompleteCheckBox = itemView.findViewById(R.id.dailyCompleteCheckBox);
            completeHabitButton = itemView.findViewById(R.id.completeHabitButton);
            habitProfileButton = itemView.findViewById(R.id.habitProfileButton);
        }
    }
}
