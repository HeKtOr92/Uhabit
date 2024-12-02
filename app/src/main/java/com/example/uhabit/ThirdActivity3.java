package com.example.uhabit;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ThirdActivity3 extends AppCompatActivity implements HabitAdapter.OnHabitInteractionListener {

    private HabitViewModel habitViewModel;
    private HabitAdapter habitAdapter;
    private TextView statsTextView;
    private int totalHabitsCompleted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third3);

        RecyclerView completedHabitsRecyclerView = findViewById(R.id.completedHabitsRecyclerView);
        statsTextView = findViewById(R.id.statsTextView);

        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);

        // Initialize the adapter
        habitAdapter = new HabitAdapter(null, habit -> {
            // Optionally, handle item clicks
        }, this); // Pass 'this' as the interaction listener

        completedHabitsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        completedHabitsRecyclerView.setAdapter(habitAdapter);

        habitViewModel.getCompletedHabits().observe(this, habits -> {
            habitAdapter.setHabits(habits);
            totalHabitsCompleted = habits.size();
            updateStats();
        });
    }

    private void updateStats() {
        statsTextView.setText(getString(R.string.total_habits_completed, totalHabitsCompleted));
    }

    // Implement the OnHabitInteractionListener methods (empty since not needed here)
    @Override
    public void onDailyCheckChanged(Habit habit, boolean isChecked) {
        // Not needed for completed habits
    }

    @Override
    public void onHabitCompleted(Habit habit) {
        // Not needed for completed habits
    }
}
