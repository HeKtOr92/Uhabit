package com.example.uhabit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class HabitProfileActivity extends AppCompatActivity {

    private HabitViewModel habitViewModel;
    private Habit habit;

    private TextView habitNameTextView;
    private TextView totalDaysTextView;
    private TextView completedDaysTextView;
    private TextView skippedDaysTextView;
    private TextView completionPercentageTextView;
    private TextView currentStreakTextView;
    private TextView longestStreakTextView;
    private CheckBox completedTodayCheckBox;

    private RecyclerView calendarRecyclerView;
    private CalendarAdapter calendarAdapter;
    private Button editHabitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_profile);

        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);

        int habitId = getIntent().getIntExtra("habitId", -1);
        if (habitId == -1) {
            finish();
            return;
        }

        habitNameTextView = findViewById(R.id.habitNameTextView);
        totalDaysTextView = findViewById(R.id.totalDaysTextView);
        completedDaysTextView = findViewById(R.id.completedDaysTextView);
        skippedDaysTextView = findViewById(R.id.skippedDaysTextView);
        completionPercentageTextView = findViewById(R.id.completionPercentageTextView);
        currentStreakTextView = findViewById(R.id.currentStreakTextView);
        longestStreakTextView = findViewById(R.id.longestStreakTextView);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        editHabitButton = findViewById(R.id.editHabitButton);
        completedTodayCheckBox = findViewById(R.id.completedTodayCheckBox);

        calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        calendarAdapter = new CalendarAdapter();
        calendarRecyclerView.setAdapter(calendarAdapter);

        habitViewModel.getAllHabits().observe(this, habits -> {
            for (Habit h : habits) {
                if (h.getId() == habitId) {
                    habit = h;
                    habitNameTextView.setText(habit.getName());
                    loadHabitStatistics();
                    loadTodayCompletionState();
                    break;
                }
            }
        });

        editHabitButton.setOnClickListener(v -> showEditHabitDialog());
    }

    private void loadTodayCompletionState() {
        habitViewModel.isHabitCompletedOnDate(habit.getId(), LocalDate.now()).observe(this, isCompleted -> {
            if (isCompleted != null) {
                completedTodayCheckBox.setChecked(isCompleted);
            } else {
                completedTodayCheckBox.setChecked(false);
            }
        });

        completedTodayCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            completedTodayCheckBox.setEnabled(false);

            new Thread(() -> {
                HabitRecord record = habitViewModel.getTodayHabitRecordSync(habit.getId(), LocalDate.now());
                if (record == null && isChecked) {
                    HabitRecord newRecord = new HabitRecord(habit.getId(), LocalDate.now(), true);
                    habitViewModel.insertHabitRecord(newRecord);
                } else if (record != null) {
                    record.setCompleted(isChecked);
                    habitViewModel.updateHabitRecord(record);
                }

                runOnUiThread(() -> {
                    completedTodayCheckBox.setEnabled(true);
                    loadHabitStatistics();
                });
            }).start();
        });
    }

    private void loadHabitStatistics() {
        habitViewModel.getHabitRecords(habit.getId()).observe(this, habitRecords -> {
            int completedDays = 0, skippedDays = 0, currentStreak = 0, longestStreak = 0, tempStreak = 0;

            if (habitRecords != null) {
                for (HabitRecord record : habitRecords) {
                    if (record.isCompleted()) {
                        completedDays++;
                        tempStreak++;
                        longestStreak = Math.max(longestStreak, tempStreak);
                    } else {
                        skippedDays++;
                        tempStreak = 0;
                    }
                }
            }

            // Reflect the accurate completion count
            habit.setCompletionCount(completedDays);
            habitViewModel.update(habit);

            float completionPercentage = habit.getDuration() > 0
                    ? ((float) completedDays / habit.getDuration()) * 100
                    : 0;

            totalDaysTextView.setText(getString(R.string.total_days, habit.getDuration()));
            completedDaysTextView.setText(getString(R.string.completed_days, completedDays));
            skippedDaysTextView.setText(getString(R.string.skipped_days, skippedDays));
            completionPercentageTextView.setText(getString(R.string.completion_percentage, completionPercentage));
            currentStreakTextView.setText(getString(R.string.current_streak, currentStreak));
            longestStreakTextView.setText(getString(R.string.longest_streak, longestStreak));
        });
    }


    private void showEditHabitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_habit, null);
        builder.setView(dialogView);
        builder.setTitle("Edit Habit");

        EditText habitNameEditText = dialogView.findViewById(R.id.habitNameEditText);
        EditText habitExplanationEditText = dialogView.findViewById(R.id.habitExplanationEditText);
        EditText startDateEditText = dialogView.findViewById(R.id.startDateEditText);
        EditText reminderTimeEditText = dialogView.findViewById(R.id.reminderTimeEditText);
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        Spinner frequencySpinner = dialogView.findViewById(R.id.frequencySpinner);
        EditText durationEditText = dialogView.findViewById(R.id.durationEditText);
        Button saveHabitButton = dialogView.findViewById(R.id.saveHabitButton);

        habitNameEditText.setText(habit.getName());
        habitExplanationEditText.setText(habit.getExplanation());
        startDateEditText.setText(formatDate(habit.getStartDate()));
        reminderTimeEditText.setText(habit.getReminderTime());
        durationEditText.setText(String.valueOf(habit.getDuration()));

        startDateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> startDateEditText.setText(
                            String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        reminderTimeEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> reminderTimeEditText.setText(
                            String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(this)
            );
            timePickerDialog.show();
        });

        AlertDialog alertDialog = builder.create();

        saveHabitButton.setOnClickListener(v -> {
            String newName = habitNameEditText.getText().toString();
            String newExplanation = habitExplanationEditText.getText().toString();
            String newStartDate = startDateEditText.getText().toString();
            String newReminderTime = reminderTimeEditText.getText().toString();
            String newDurationText = durationEditText.getText().toString();

            if (newName.isEmpty() || newStartDate.isEmpty() || newReminderTime.isEmpty() || newDurationText.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            habit.setName(newName);
            habit.setExplanation(newExplanation);
            habit.setStartDate(LocalDate.parse(newStartDate));
            habit.setReminderTime(newReminderTime);
            habit.setDuration(Integer.parseInt(newDurationText));

            habitViewModel.update(habit);

            AlarmUtil.cancelAlarm(this, habit.getId());
            AlarmUtil.setAlarm(this, habit.getId(), newReminderTime);

            Toast.makeText(this, "Habit updated!", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> alertDialog.dismiss());

        alertDialog.show();
    }

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
